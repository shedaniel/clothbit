/*
 * This file is part of Clothbit.
 * Copyright (C) 2021 shedaniel
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package me.shedaniel.clothbit.impl.client.gui.entry;

import com.google.common.base.MoreObjects;
import com.google.common.base.Suppliers;
import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.clothbit.api.client.gui.ScissorsStack;
import me.shedaniel.clothbit.api.options.Option;
import me.shedaniel.clothbit.api.options.OptionType;
import me.shedaniel.clothbit.api.options.OptionTypesContext;
import me.shedaniel.clothbit.impl.client.gui.entry.component.FieldNameComponent;
import me.shedaniel.clothbit.impl.client.gui.entry.component.ResetButtonComponent;
import me.shedaniel.clothbit.impl.client.gui.entry.component.SandwichIconComponent;
import me.shedaniel.clothbit.impl.client.gui.entry.component.value.EntryValueEntryComponent;
import me.shedaniel.clothbit.impl.client.gui.widgets.ListWidget;
import me.shedaniel.clothbit.impl.utils.Animator;
import me.shedaniel.clothbit.impl.utils.BooleanAnimator;
import me.shedaniel.clothbit.impl.utils.Observable;
import me.shedaniel.math.Rectangle;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BaseOptionEntry<T> extends ListWidget.Entry<BaseOptionEntry<T>> {
    public static final int INDENT = 20;
    public final String id;
    public final Object option;
    public final OptionType<T> type;
    
    protected final Rectangle bounds = new Rectangle();
    public final Option<?>[] parents;
    private final Supplier<Integer> extraHeight = extraHeightSupplier(false);
    private final Supplier<Integer> extraHeightExpended = extraHeightSupplier(true);
    private final List<GuiEventListener> children = new ArrayList<>();
    private final List<Consumer<ListWidget<BaseOptionEntry<T>>>> onParentUpdate = new ArrayList<>();
    private final List<Observable<?>> observables = new ArrayList<>();
    private final List<Animator> animators = new ArrayList<>();
    public final T originalValue;
    public final Observable<T> value;
    public final Observable<Boolean> hovered = observe(false);
    public final BooleanAnimator selected = animate(false);
    public ValueEntryComponent<T> valueHolder;
    private final List<EntryComponent<T>> entryComponents = new ArrayList<>();
    
    public BaseOptionEntry(String id, Object option, OptionType<T> type, T value, OptionTypesContext ctx, Option<?>[] parents) {
        this.id = id;
        this.option = MoreObjects.firstNonNull(option, type);
        this.type = type;
        this.originalValue = value;
        this.parents = parents;
        this.value = observe(type.copy(value, ctx));
        this.valueHolder = new EntryValueEntryComponent<>(this);
    }
    
    public Rectangle getBounds() {
        return bounds;
    }
    
    @Override
    public boolean containsMouse(double mouseX, double mouseY) {
        return bounds.contains(mouseX, mouseY);
    }
    
    private Supplier<Integer> extraHeightSupplier(boolean expended) {
        return Suppliers.memoize(() -> {
            int max = 0;
            for (EntryComponent<T> component : this.entryComponents) {
                int height = component.getExtraHeight(expended);
                if (height > max) max = height;
            }
            return max;
        });
    }
    
    public <R extends GuiEventListener> R addChild(R listener) {
        this.children.add(listener);
        return listener;
    }
    
    public <R extends EntryComponent<T>> R addComponent(R component) {
        if (component instanceof ValueEntryComponent) {
            if (valueHolder != null && !(valueHolder instanceof EntryValueEntryComponent))
                throw new IllegalStateException("Only ValueEntryComponent is allowed!");
            valueHolder = (ValueEntryComponent<T>) component;
        }
        this.entryComponents.add(component);
        return addChild(component);
    }
    
    @Nullable
    public <R extends EntryComponent<T>> R getComponent(Class<R> type) {
        for (EntryComponent<T> entryComponent : this.entryComponents) {
            if (type.isInstance(entryComponent)) return (R) entryComponent;
        }
        return null;
    }
    
    protected <R> Observable<R> observe(R value) {
        Observable<R> observable = new Observable<>(value);
        this.observables.add(observable);
        return observable;
    }
    
    protected Animator animate(double value) {
        Animator animator = new Animator(value);
        this.animators.add(animator);
        return animator;
    }
    
    protected BooleanAnimator animate(boolean value) {
        BooleanAnimator animator = new BooleanAnimator(value);
        this.animators.add(animator);
        return animator;
    }
    
    public void listenParentChange(Consumer<ListWidget<BaseOptionEntry<T>>> listener) {
        this.onParentUpdate.add(listener);
    }
    
    @Override
    public void setParent(ListWidget<BaseOptionEntry<T>> parent) {
        super.setParent(parent);
        for (Consumer<ListWidget<BaseOptionEntry<T>>> listener : this.onParentUpdate) {
            listener.accept(parent);
        }
    }
    
    @Override
    public List<? extends GuiEventListener> children() {
        return children;
    }
    
    @Override
    public void render(PoseStack matrices, int index, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        this.bounds.setBounds(x, y, entryWidth, entryHeight);
        ScissorsStack.getInstance().push(this.bounds).applyStack();
        //        fill(matrices, 0, 0, 10000, 10000, 0x20FFFFFF);
        this.hovered.set(isHovered);
        
        for (Observable<?> observable : this.observables) {
            observable.update();
        }
        
        for (Animator animator : this.animators) {
            animator.update(delta);
        }
        
        for (EntryComponent<T> component : entryComponents) {
            component.render(matrices, index, x, y, entryWidth, entryHeight, mouseX, mouseY, isHovered, isHovered && component.containsMouse(mouseX, mouseY), delta);
        }
        ScissorsStack.getInstance().pop().applyStack();
    }
    
    @Override
    public int getItemHeight() {
        return 24 + extraHeight.get() + (int) Math.round(selected.progress() * extraHeightExpended.get());
    }
    
    public void addFieldName(Option<T> option) {
        addComponent(new SandwichIconComponent<>(this));
        addComponent(new FieldNameComponent<>(option, this));
    }
    
    public void addFieldName(Supplier<Component> fieldName) {
        addComponent(new SandwichIconComponent<>(this));
        addComponent(new FieldNameComponent<>(fieldName, this));
    }
    
    public void addResetButton() {
        addComponent(new ResetButtonComponent<>(this));
    }
    
    public T getDefaultValue() {
        if (option instanceof Option) return ((Option<T>) option).getDefaultValue();
        return ((OptionType<T>) option).getDefaultValue();
    }
}

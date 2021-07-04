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

import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.clothbit.api.options.Option;
import me.shedaniel.clothbit.api.options.OptionTypesContext;
import me.shedaniel.clothbit.impl.client.gui.entry.component.FieldNameComponent;
import me.shedaniel.clothbit.impl.client.gui.entry.component.ResetButtonComponent;
import me.shedaniel.clothbit.impl.client.gui.entry.component.SandwichIconComponent;
import me.shedaniel.clothbit.impl.client.gui.entry.component.value.EntryValueEntryComponent;
import me.shedaniel.clothbit.impl.client.gui.widgets.ListWidget;
import me.shedaniel.clothbit.impl.utils.Animator;
import me.shedaniel.clothbit.impl.utils.BooleanAnimator;
import me.shedaniel.clothbit.impl.utils.Observable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BaseOptionEntry<T> extends ListWidget.Entry<BaseOptionEntry<T>> {
    public final String id;
    public final Option<T> option;
    
    public final Option<?>[] parents;
    private final List<GuiEventListener> children = new ArrayList<>();
    private final List<Observable<?>> observables = new ArrayList<>();
    private final List<Animator> animators = new ArrayList<>();
    public final T originalValue;
    public final Observable<T> value;
    public final Observable<Boolean> hovered = observe(false);
    public final BooleanAnimator selected = animate(false);
    public ValueEntryComponent<T> valueHolder;
    
    private final List<EntryComponent<T>> entryComponents = new ArrayList<>();
    
    public BaseOptionEntry(String id, Option<T> option, T value, OptionTypesContext ctx, Option<?>[] parents) {
        this.id = id;
        this.option = option;
        this.originalValue = value;
        this.parents = parents;
        this.value = observe(option.getType().copy(value, ctx));
        this.valueHolder = new EntryValueEntryComponent<>(this);
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
    
    @Override
    public List<? extends GuiEventListener> children() {
        return children;
    }
    
    @Override
    public void render(PoseStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
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
    }
    
    @Override
    public int getItemHeight() {
        return 24 + (int) Math.round(Math.pow(selected.progress(), 1) * 48);
    }
    
    public void addFieldName() {
        addComponent(new SandwichIconComponent<>(this));
        addComponent(new FieldNameComponent<>(this));
    }
    
    public void addResetButton() {
        addComponent(new ResetButtonComponent<>(this));
    }
}

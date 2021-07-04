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
import me.shedaniel.clothbit.impl.client.gui.widgets.WidgetWithBounds;
import me.shedaniel.clothbit.impl.utils.Animator;
import me.shedaniel.clothbit.impl.utils.Observable;
import me.shedaniel.math.Rectangle;
import net.minecraft.client.gui.components.events.GuiEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class EntryComponent<T> extends WidgetWithBounds {
    protected final BaseOptionEntry<T> parent;
    protected final Rectangle bounds = new Rectangle();
    private final List<GuiEventListener> children = new ArrayList<>();
    private final List<Observable<?>> observables = new ArrayList<>();
    private final List<Animator> animators = new ArrayList<>();
    private final List<net.minecraft.client.gui.components.Widget> drawables = new ArrayList<>();
    public final Observable<Boolean> hovered = observe(false);
    public final Animator hovering = animate(0.0);
    
    public EntryComponent(BaseOptionEntry<T> parent) {
        this.parent = parent;
        listenParentHovered(hovered -> hovering.setTo(hovered ? 100 : 0, 100));
    }
    
    protected void listenValue(Runnable listener) {
        this.parent.value.addListener(listener);
    }
    
    protected void listenHovered(Consumer<Boolean> listener) {
        this.hovered.addListener(listener);
    }
    
    protected void listenParentHovered(Consumer<Boolean> listener) {
        this.parent.hovered.addListener(listener);
    }
    
    @Override
    public List<? extends GuiEventListener> children() {
        return children;
    }
    
    @Override
    public final Rectangle getBounds() {
        return bounds;
    }
    
    protected <R extends GuiEventListener & net.minecraft.client.gui.components.Widget> R addChild(R widget) {
        this.children.add(widget);
        this.drawables.add(widget);
        return widget;
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
    
    public void render(PoseStack poses, int index, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, boolean componentHovered, float delta) {
        this.hovered.set(componentHovered);
        
        for (Observable<?> observable : this.observables) {
            observable.update();
        }
        
        for (Animator animator : this.animators) {
            animator.update(delta);
        }
        
        for (net.minecraft.client.gui.components.Widget widget : this.drawables) {
            widget.render(poses, mouseX, mouseY, delta);
        }
    }
    
    @Override
    @Deprecated
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        throw new UnsupportedOperationException();
    }
}

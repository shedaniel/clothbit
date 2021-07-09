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

package me.shedaniel.clothbit.impl.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.clothbit.impl.client.gui.cursor.CursorType;
import me.shedaniel.clothbit.impl.client.gui.widgets.Widget;
import me.shedaniel.clothbit.impl.utils.Observable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class WidgetScreen extends Screen {
    private final List<Observable<?>> observables = new ArrayList<>();
    protected List<Widget> widgets = new ArrayList<>();
    protected Observable<CursorType> cursorType = observe(CursorType.ARROW);
    
    protected WidgetScreen(Component component) {
        super(component);
        this.cursorType.addListener(type -> {
            RenderSystem.recordRenderCall(() -> {
                GLFW.glfwSetCursor(Minecraft.getInstance().getWindow().getWindow(), type.getCursor());
            });
        });
    }
    
    @Override
    public List<Widget> children() {
        return widgets;
    }
    
    @Override
    protected void init() {
        this.widgets.clear();
        this.initWidgets();
        super.init();
        this.children.addAll(widgets);
    }
    
    protected void initWidgets() {}
    
    protected void addWidget(Widget widget) {
        this.widgets.add(widget);
    }
    
    protected <R> Observable<R> observe(R value) {
        Observable<R> observable = new Observable<>(value);
        this.observables.add(observable);
        return observable;
    }
    
    @Override
    public void removed() {
        this.cursorType.setHidden(CursorType.ARROW);
        RenderSystem.recordRenderCall(() -> {
            GLFW.glfwSetCursor(Minecraft.getInstance().getWindow().getWindow(), CursorType.ARROW.getCursor());
            this.cursorType.setHidden(CursorType.ARROW);
        });
    }
    
    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        this.cursorType.set(CursorType.ARROW);
        for (Widget widget : this.widgets) {
            if (widget.handleCursorType(mouseX, mouseY, this.cursorType)) {
                break;
            }
        }
        
        for (Observable<?> observable : this.observables) {
            observable.update();
        }
        
        super.render(poseStack, mouseX, mouseY, delta);
        for (Widget widget : this.widgets) {
            widget.render(poseStack, mouseX, mouseY, delta);
        }
    }
}

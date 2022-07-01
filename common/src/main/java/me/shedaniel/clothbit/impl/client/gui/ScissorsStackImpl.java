/*
 * This file is part of Clothbit.
 * Copyright (C) 2021, 2022 shedaniel
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

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Window;
import me.shedaniel.clothbit.api.client.gui.ScissorsStack;
import me.shedaniel.math.Rectangle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import java.util.Collection;
import java.util.List;

@Environment(EnvType.CLIENT)
public enum ScissorsStackImpl implements ScissorsStack {
    INSTANCE;
    private final List<Rectangle> scissorsAreas = Lists.newArrayList();
    
    @Override
    public ScissorsStack applyScissors(@Nullable Rectangle rectangle) {
        if (rectangle == null) {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        } else {
            Window window = Minecraft.getInstance().getWindow();
            double scale = window.getGuiScale();
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            if (rectangle.isEmpty()) {
                GL11.glScissor(0, 0, 0, 0);
            } else {
                GL11.glScissor((int) (rectangle.x * scale), (int) ((window.getGuiScaledHeight() - rectangle.height - rectangle.y) * scale), (int) (rectangle.width * scale), (int) (rectangle.height * scale));
            }
        }
        return this;
    }
    
    @Override
    public ScissorsStack push(Rectangle rectangle) {
        scissorsAreas.add(rectangle);
        return this;
    }
    
    @Override
    public ScissorsStack pushAll(Collection<Rectangle> rectangles) {
        scissorsAreas.addAll(rectangles);
        return this;
    }
    
    @Override
    public ScissorsStack pop() {
        if (scissorsAreas.isEmpty())
            throw new IllegalStateException("There is no entries in the stack!");
        scissorsAreas.remove(scissorsAreas.size() - 1);
        return this;
    }
    
    @Override
    public ScissorsStack popAll() {
        scissorsAreas.clear();
        return this;
    }
    
    @Override
    public List<Rectangle> getCurrentStack() {
        return scissorsAreas;
    }
    
    @Override
    public ScissorsStack applyStack() {
        if (scissorsAreas.isEmpty()) {
            return applyScissors(null);
        }
        Rectangle rectangle = null;
        for (Rectangle area : scissorsAreas) {
            if (rectangle == null) {
                rectangle = area.clone();
            } else {
                rectangle = rectangle.intersection(area);
            }
        }
        return applyScissors(rectangle);
    }
}
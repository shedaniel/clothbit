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

package me.shedaniel.clothbit.impl.client.gui.cursor;

import com.google.common.base.Suppliers;
import org.lwjgl.glfw.GLFW;

import java.util.function.LongSupplier;

public enum CursorType {
    ARROW(GLFW.GLFW_ARROW_CURSOR),
    TEXT(GLFW.GLFW_IBEAM_CURSOR),
    CROSSHAIR(GLFW.GLFW_CROSSHAIR_CURSOR),
    HAND(GLFW.GLFW_HAND_CURSOR),
    H_RESIZE(GLFW.GLFW_HRESIZE_CURSOR),
    V_RESIZE(GLFW.GLFW_VRESIZE_CURSOR),
    ;
    
    private final LongSupplier cursor;
    
    CursorType(int shape) {
        this.cursor = Suppliers.memoize(() -> {
            return GLFW.glfwCreateStandardCursor(shape);
        })::get;
    }
    
    public long getCursor() {
        return cursor.getAsLong();
    }
}

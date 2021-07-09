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

package me.shedaniel.clothbit.impl.client.gui.widgets;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.math.Matrix4f;
import me.shedaniel.clothbit.impl.client.gui.cursor.CursorType;
import me.shedaniel.clothbit.impl.utils.Observable;
import me.shedaniel.math.Point;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public abstract class Widget extends AbstractContainerEventHandler implements net.minecraft.client.gui.components.Widget, CursorTypeHandler {
    /**
     * The Minecraft Client instance
     */
    protected final Minecraft minecraft = Minecraft.getInstance();
    /**
     * The font for rendering text
     */
    protected final Font font = minecraft.font;
    
    public int getZ() {
        return this.getBlitOffset();
    }
    
    public void setZ(int z) {
        this.setBlitOffset(z);
    }
    
    public boolean containsMouse(double mouseX, double mouseY) {
        return false;
    }
    
    @SuppressWarnings("RedundantCast")
    public final boolean containsMouse(int mouseX, int mouseY) {
        return containsMouse((double) mouseX, (double) mouseY);
    }
    
    @SuppressWarnings("RedundantCast")
    public final boolean containsMouse(Point point) {
        return containsMouse((double) point.x, (double) point.y);
    }
    
    @Override
    public final boolean isMouseOver(double mouseX, double mouseY) {
        return containsMouse(mouseX, mouseY);
    }
    
    public final void bindTexture(ResourceLocation texture) {
        minecraft.getTextureManager().bind(texture);
    }
    
    @Override
    public boolean handleCursorType(int mouseX, int mouseY, Observable<CursorType> type) {
        if (useHandCursorIfHovered()) {
            if (containsMouse(mouseX, mouseY)) {
                type.set(CursorType.HAND);
                return true;
            }
        }
        
        for (GuiEventListener child : this.children()) {
            if (child instanceof CursorTypeHandler) {
                if (((CursorTypeHandler) child).handleCursorType(mouseX, mouseY, type)) return true;
            }
        }
        
        return false;
    }
    
    public void line(BufferBuilder bufferBuilder, Matrix4f pose, float x1, float y1, float x2, float y2, float a, float r, float g, float b) {
        float z = getZ();
        bufferBuilder.vertex(pose, x1, y1, z).color(r, g, b, a).endVertex();
        bufferBuilder.vertex(pose, x2, y2, z).color(r, g, b, a).endVertex();
    }
    
    public boolean useHandCursorIfHovered() {
        return false;
    }
}
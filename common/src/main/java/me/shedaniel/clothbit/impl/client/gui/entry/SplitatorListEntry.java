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

package me.shedaniel.clothbit.impl.client.gui.entry;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Matrix4f;
import me.shedaniel.clothbit.impl.client.gui.widgets.ListWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;

import java.util.List;

public class SplitatorListEntry extends ListWidget.Entry<SplitatorListEntry> {
    private final ListWidget.Entry<?> parent;
    
    public SplitatorListEntry(ListWidget.Entry<?> parent) {
        this.parent = parent;
    }
    
    @Override
    public void render(PoseStack matrices, int index, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        this.parent.render(matrices, index, x, y + 2, entryWidth, entryHeight - 6, mouseX, mouseY, isHovered, delta);
        dotHLine(matrices, x, x + entryWidth, y + entryHeight, 0xFF777777);
    }
    
    private void dotHLine(PoseStack matrices, int x1, int x2, int y, int color) {
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(7425);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        bufferBuilder.begin(7, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f pose = matrices.last().pose();
        
        float a = (color >> 24 & 255) / 255.0F;
        float r = (color >> 16 & 255) / 255.0F;
        float g = (color >> 8 & 255) / 255.0F;
        float b = (color & 255) / 255.0F;
        
        for (int x = x1; x < x2; x += 2) {
            fill(bufferBuilder, pose, x, y, x + 1, y + 1, getZ(), a, r, g, b);
        }
        
        tesselator.end();
        RenderSystem.shadeModel(7424);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
    }
    
    private void fill(BufferBuilder bufferBuilder, Matrix4f pose, float x1, float y1, float x2, float y2, float z, float a, float r, float g, float b) {
        bufferBuilder.vertex(pose, x2, y1, z).color(r, g, b, a).endVertex();
        bufferBuilder.vertex(pose, x1, y1, z).color(r, g, b, a).endVertex();
        bufferBuilder.vertex(pose, x1, y2, z).color(r, g, b, a).endVertex();
        bufferBuilder.vertex(pose, x2, y2, z).color(r, g, b, a).endVertex();
    }
    
    @Override
    public void setParent(ListWidget<SplitatorListEntry> parent) {
        super.setParent(parent);
        this.parent.setParent((ListWidget) parent);
    }
    
    @Override
    public int getItemHeight() {
        return this.parent.getItemHeight() + 7;
    }
    
    @Override
    public List<? extends GuiEventListener> children() {
        return this.parent.children();
    }
}

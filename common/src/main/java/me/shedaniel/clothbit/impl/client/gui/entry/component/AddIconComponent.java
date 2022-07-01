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

package me.shedaniel.clothbit.impl.client.gui.entry.component;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Matrix4f;
import me.shedaniel.clothbit.impl.client.gui.entry.BaseOptionEntry;
import me.shedaniel.math.Color;
import me.shedaniel.math.Rectangle;
import org.lwjgl.opengl.GL11;

public abstract class AddIconComponent<T> extends IconButtonComponent<T> {
    public AddIconComponent(BaseOptionEntry<T> parent) {
        super(parent);
    }
    
    @Override
    public void render(PoseStack poses, int index, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, boolean componentHovered, float delta) {
        ResetButtonComponent<T> resetButton = parent.getComponent(ResetButtonComponent.class);
        int extraOffset = 0;
        if (resetButton != null) extraOffset = resetButton.getBounds().width + 2;
        bounds.setBounds(x + entryWidth - extraOffset - 14, y + 4, 12, 12);
        super.render(poses, index, x, y, entryWidth, entryHeight, mouseX, mouseY, isHovered, componentHovered, delta);
        renderIcon(poses, bounds);
    }
    
    private void renderIcon(PoseStack poses, Rectangle bounds) {
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.defaultBlendFunc();
        float thickness = 1.2f;
        RenderSystem.shadeModel(7425);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        bufferBuilder.begin(GL11.GL_TRIANGLES, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f pose = poses.last().pose();
        int color = getColor();
        float a = (color >> 24 & 255) / 255.0F;
        float r = (color >> 16 & 255) / 255.0F;
        float g = (color >> 8 & 255) / 255.0F;
        float b = (color & 255) / 255.0F;
        float stX1 = bounds.x + 2.5f;
        float stX2 = bounds.getMaxX() - 2.5f;
        
        line(bufferBuilder, pose, bounds.x + 2.5f, bounds.getCenterY(), bounds.getMaxX() - 2.5f, bounds.getCenterY(), thickness, thickness, a, r, g, b);
        line(bufferBuilder, pose, bounds.getCenterX(), bounds.y + 2.5f, bounds.getCenterX(), bounds.getMaxY() - 2.5f, thickness, thickness, a, r, g, b);
        
        tesselator.end();
        RenderSystem.shadeModel(7424);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
    }
    
    private int getColor() {
        int i = (int) (170 + 60 * hoveringColor.progress());
        return Color.ofRGB(i, i, i).getColor();
    }
}

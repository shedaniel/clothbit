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

package me.shedaniel.clothbit.impl.client.gui.entry.component;

import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.clothbit.impl.client.gui.entry.BaseOptionEntry;
import me.shedaniel.clothbit.impl.client.gui.entry.EntryComponent;
import me.shedaniel.math.Color;

public abstract class IconButtonComponent<T> extends EntryComponent<T> {
    public IconButtonComponent(BaseOptionEntry<T> parent) {
        super(parent);
    }
    
    @Override
    public void render(PoseStack poses, int index, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, boolean componentHovered, float delta) {
        int color = getColor();
        fillGradient(poses, bounds.x - 1, bounds.y - 1, bounds.getMaxX() + 1, bounds.getMaxY() + 1, color, color);
        super.render(poses, index, x, y, entryWidth, entryHeight, mouseX, mouseY, isHovered, componentHovered, delta);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && containsMouse(mouseX, mouseY)) {
            onClicked();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    protected abstract void onClicked();
    
    private int getColor() {
        int i = (int) (50 * hoveringColor.progress());
        return Color.ofRGBA(255, 255, 255, i).getColor();
    }
    
    @Override
    public boolean useHandCursorIfHovered() {
        return true;
    }
}

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

package me.shedaniel.clothbit.impl.client.gui.entry.component.value;

import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.clothbit.impl.client.gui.entry.BaseOptionEntry;
import me.shedaniel.clothbit.impl.client.gui.entry.ValueEntryComponent;

import java.util.List;

public class ArrayValueEntryComponent<T> extends ValueEntryComponent<T> {
    private final List<BaseOptionEntry<T>> entries;
    
    public ArrayValueEntryComponent(BaseOptionEntry<T> parent, List<BaseOptionEntry<T>> entries) {
        super(parent);
        this.entries = entries;
        this.children.addAll(this.entries);
    }
    
    @Override
    public void render(PoseStack poses, int index, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, boolean componentHovered, float delta) {
        super.render(poses, index, x, y, entryWidth, entryHeight, mouseX, mouseY, isHovered, componentHovered, delta);
        int yy = y;
        for (BaseOptionEntry<T> entry : entries) {
            entry.render(poses, index, yy += entry.getItemHeight(), x + BaseOptionEntry.INDENT, entryWidth - BaseOptionEntry.INDENT, entry.getItemHeight(), mouseX, mouseY, isHovered && entry.containsMouse(mouseX, mouseY), delta);
        }
    }
    
    @Override
    public int getExtraHeight(boolean expended) {
        if (expended) return entries.stream().mapToInt(BaseOptionEntry::getItemHeight).sum();
        return super.getExtraHeight(expended);
    }
}

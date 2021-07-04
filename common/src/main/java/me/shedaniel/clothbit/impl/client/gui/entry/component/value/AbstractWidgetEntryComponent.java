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
import me.shedaniel.clothbit.impl.client.gui.entry.component.ResetButtonComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;

public class AbstractWidgetEntryComponent<T, W extends AbstractWidget> extends ValueEntryComponent<T> {
    public static final int BUTTON_WIDTH = 100;
    public static final int BUTTON_HEIGHT = 20;
    public W widget;
    
    public AbstractWidgetEntryComponent(BaseOptionEntry<T> parent) {
        super(parent);
    }
    
    @Override
    public void render(PoseStack poses, int index, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, boolean componentHovered, float delta) {
        int i = (widget instanceof EditBox ? 1 : 0);
        ResetButtonComponent<T> resetButton = parent.getComponent(ResetButtonComponent.class);
        int extraOffset = 0;
        if (resetButton != null) extraOffset = resetButton.getBounds().width + 2;
        widget.x = x + entryWidth - 100 - extraOffset + i;
        widget.y = y + i + 1;
        bounds.setBounds(widget.x - i, widget.y - i, widget.getWidth() + i * 2, widget.getHeight() + i * 2);
        super.render(poses, index, x, y, entryWidth, entryHeight, mouseX, mouseY, isHovered, componentHovered, delta);
    }
}

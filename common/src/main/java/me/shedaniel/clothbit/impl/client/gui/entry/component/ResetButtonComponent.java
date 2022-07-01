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

import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.clothbit.impl.client.gui.entry.BaseOptionEntry;
import me.shedaniel.clothbit.impl.client.gui.entry.EntryComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TextComponent;

import java.util.Objects;

public class ResetButtonComponent<T> extends EntryComponent<T> {
    private final Button resetButton = addChild(new Button(0, 0, 46, 20, new TextComponent("Reset"), this::onResetPressed));
    
    public ResetButtonComponent(BaseOptionEntry<T> parent) {
        super(parent);
        listenValue(() -> resetButton.active = !Objects.equals(parent.getDefaultValue(), parent.value.get()));
    }
    
    private void onResetPressed(Button button) {
        parent.value.set(parent.getDefaultValue());
    }
    
    @Override
    public void render(PoseStack poses, int index, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, boolean componentHovered, float delta) {
        resetButton.x = x + entryWidth - 46;
        resetButton.y = y;
        bounds.setBounds(resetButton.x, resetButton.y, resetButton.getWidth(), resetButton.getHeight());
        super.render(poses, index, x, y, entryWidth, entryHeight, mouseX, mouseY, isHovered, componentHovered, delta);
    }
    
    @Override
    public boolean useHandCursorIfHovered() {
        return resetButton.active;
    }
}

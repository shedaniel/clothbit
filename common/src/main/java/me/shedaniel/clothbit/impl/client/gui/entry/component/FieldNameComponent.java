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

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.clothbit.api.options.Option;
import me.shedaniel.clothbit.impl.client.gui.OptionsScreenImpl;
import me.shedaniel.clothbit.impl.client.gui.entry.BaseOptionEntry;
import me.shedaniel.clothbit.impl.client.gui.entry.EntryComponent;
import me.shedaniel.math.Color;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;

import java.util.function.Supplier;

public class FieldNameComponent<T> extends EntryComponent<T> {
    public static final int ERROR_COLOR = 0xFF5555;
    public static final int UNEDITED_COLOR = 0xAAAAAA;
    public static final int TEXT_COLOR = 0xFFFFFF;
    public final Supplier<Component> fieldName;
    
    public FieldNameComponent(Option<T> option, BaseOptionEntry<T> parent) {
        this(Suppliers.memoize(() -> {
            StringBuilder s = new StringBuilder(parent.id);
            for (Option<?> optionParent : parent.parents) {
                s.append(".").append(optionParent.getName());
            }
            s.append(".").append(option.getName());
            return OptionsScreenImpl.getName(s.toString());
        }), parent);
    }
    
    public FieldNameComponent(Supplier<Component> fieldName, BaseOptionEntry<T> parent) {
        super(parent);
        this.fieldName = fieldName;
    }
    
    @Override
    public void render(PoseStack poses, int index, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, boolean componentHovered, float delta) {
        bounds.setBounds(x, y, entryWidth, 22);
        
        MutableComponent text = fieldName.get().copy();
        boolean edited = parent.valueHolder.isEdited || parent.valueHolder.hasErrors;
        if (edited) {
            text.withStyle(ChatFormatting.ITALIC);
            if (parent.valueHolder.hasErrors) {
                text.withStyle(style -> style.withColor(TextColor.fromRgb(ERROR_COLOR)));
            }
        } else {
            text.withStyle(style -> style.withColor(TextColor.fromRgb(getUneditedColor())));
        }
        font.draw(poses, text, x + 16, y + 6, TEXT_COLOR);
        
        super.render(poses, index, x, y, entryWidth, entryHeight, mouseX, mouseY, isHovered, componentHovered, delta);
    }
    
    private int getUneditedColor() {
        int i = (int) (140 + 60 * Math.max(parent.selected.progress() * 1.7, hoveringColor.progress()));
        return Color.ofRGB(i, i, i).getColor();
    }
}

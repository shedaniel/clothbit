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

package me.shedaniel.clothbit.impl.client.gui.entry.component.value;

import me.shedaniel.clothbit.impl.client.gui.cursor.CursorType;
import me.shedaniel.clothbit.impl.client.gui.entry.BaseOptionEntry;
import me.shedaniel.clothbit.impl.client.gui.widgets.ClothEditBox;
import me.shedaniel.clothbit.impl.utils.Observable;
import net.minecraft.client.Minecraft;

import java.util.function.Function;

public class TextFieldValueEntryComponent<T> extends AbstractWidgetEntryComponent<T, ClothEditBox> {
    public TextFieldValueEntryComponent(BaseOptionEntry<T> parent, Function<T, String> toString, Function<String, T> fromString) {
        super(parent);
        this.widget = addChild(new ClothEditBox(Minecraft.getInstance().font, 0, 0, BUTTON_WIDTH - 2, BUTTON_HEIGHT - 2, null));
        this.widget.setMaxLength(1000000);
        this.listenValue(() -> {
            String text = toString.apply(parent.value.get());
            if (!this.widget.getValue().equals(text)) {
                this.widget.setValue(text);
            }
        });
        this.widget.setResponder(text -> {
            try {
                parent.value.set(fromString.apply(text));
                hasErrors = false;
            } catch (Exception e) {
                hasErrors = true;
            }
        });
    }
    
    @Override
    public boolean handleCursorType(int mouseX, int mouseY, Observable<CursorType> type) {
        if (containsMouse(mouseX, mouseY)) {
            type.set(CursorType.TEXT);
            return true;
        }
        
        return super.handleCursorType(mouseX, mouseY, type);
    }
}

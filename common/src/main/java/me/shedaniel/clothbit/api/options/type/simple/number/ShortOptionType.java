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

package me.shedaniel.clothbit.api.options.type.simple.number;

import me.shedaniel.clothbit.api.options.OptionTypesContext;
import me.shedaniel.clothbit.api.options.OptionType;
import me.shedaniel.clothbit.api.serializers.ValueReader;
import me.shedaniel.clothbit.api.serializers.ValueWriter;
import org.jetbrains.annotations.Nullable;

public class ShortOptionType implements OptionType<Short> {
    private final boolean primitive;
    
    public ShortOptionType(boolean primitive) {
        this.primitive = primitive;
    }
    
    @Override
    public void write(Short value, ValueWriter writer, OptionTypesContext ctx) {
        if (value == null) {
            writer.writeNull();
        } else {
            writer.writeShort(value);
        }
    }
    
    @Override
    public Short read(ValueReader reader) {
        return reader.peek().isNull() ? reader.readNull() : reader.readShort();
    }
    
    @Override
    public boolean isNullable() {
        return !primitive;
    }
    
    @Override
    @Nullable
    public Short getDefaultValue() {
        return primitive ? (short) 0 : null;
    }
}

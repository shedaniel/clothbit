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

package me.shedaniel.clothbit.api.options.type.simple.number;

import me.shedaniel.clothbit.api.options.OptionType;
import me.shedaniel.clothbit.api.options.OptionTypesContext;
import me.shedaniel.clothbit.api.serializers.reader.ValueReader;
import me.shedaniel.clothbit.api.serializers.writer.ValueWriter;
import org.jetbrains.annotations.Nullable;

public class LongOptionType implements AbstractNumberOptionType<Long> {
    private static final LongOptionType PRIMITIVE_INSTANCE = new LongOptionType(true);
    private static final LongOptionType BOXED_INSTANCE = new LongOptionType(false);
    
    public static OptionType<Long> primitive() {
        return PRIMITIVE_INSTANCE;
    }
    
    public static OptionType<Long> boxed() {
        return BOXED_INSTANCE;
    }
    
    private final boolean primitive;
    
    public LongOptionType(boolean primitive) {
        this.primitive = primitive;
    }
    
    @Override
    public void write(Long value, ValueWriter writer, OptionTypesContext ctx) {
        if (value == null) {
            writer.writeNull();
        } else {
            writer.writeLong(value);
        }
    }
    
    @Override
    public Long read(ValueReader reader) {
        return reader.peek().isNull() ? reader.readNull() : reader.readLong();
    }
    
    @Override
    public boolean isNullable() {
        return !primitive;
    }
    
    @Override
    @Nullable
    public Long getDefaultValue() {
        return primitive ? 0L : null;
    }
}

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

package me.shedaniel.clothbit.api.options.type.simple;

import me.shedaniel.clothbit.api.options.OptionType;
import me.shedaniel.clothbit.api.options.OptionTypesContext;
import me.shedaniel.clothbit.api.serializers.reader.ValueReader;
import me.shedaniel.clothbit.api.serializers.writer.ValueWriter;
import org.jetbrains.annotations.Nullable;

public class AnyOptionType<T> implements OptionType<T> {
    private static final AnyOptionType<Object> INSTANCE = new AnyOptionType<>();
    
    public static <T> OptionType<T> instance() {
        return (AnyOptionType<T>) INSTANCE;
    }
    
    @Override
    public void write(Object value, ValueWriter writer, OptionTypesContext ctx) {
        writer.writeAny(value, ctx);
    }
    
    @Override
    public T read(ValueReader reader) {
        return (T) reader.readAny();
    }
    
    @Override
    public boolean isNullable() {
        return true;
    }
    
    @Override
    @Nullable
    public T getDefaultValue() {
        return null;
    }
}

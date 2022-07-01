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

import com.google.common.base.Preconditions;
import me.shedaniel.clothbit.api.options.OptionType;
import me.shedaniel.clothbit.api.options.OptionTypesContext;
import me.shedaniel.clothbit.api.serializers.reader.ValueReader;
import me.shedaniel.clothbit.api.serializers.writer.ValueWriter;
import org.jetbrains.annotations.Nullable;

public class NullOptionType implements OptionType<Object> {
    private static final NullOptionType INSTANCE = new NullOptionType();
    
    public static OptionType<Object> instance() {
        return INSTANCE;
    }
    
    @Override
    public void write(Object value, ValueWriter writer, OptionTypesContext ctx) {
        Preconditions.checkArgument(value == null, "Expected null in NullOptionType!");
        writer.writeNull();
    }
    
    @Override
    public Object read(ValueReader reader) {
        return reader.readNull();
    }
    
    @Override
    public boolean isNullable() {
        return true;
    }
    
    @Override
    @Nullable
    public Object getDefaultValue() {
        return null;
    }
}

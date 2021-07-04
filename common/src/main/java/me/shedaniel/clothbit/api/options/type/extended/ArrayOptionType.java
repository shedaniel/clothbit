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

package me.shedaniel.clothbit.api.options.type.extended;

import com.google.common.base.Preconditions;
import me.shedaniel.clothbit.api.options.OptionType;
import me.shedaniel.clothbit.api.options.OptionTypesContext;
import me.shedaniel.clothbit.api.serializers.reader.ValueReader;
import me.shedaniel.clothbit.api.serializers.writer.ValueWriter;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ArrayOptionType<T> implements OptionType<Object> {
    private final OptionType<T> parent;
    private final Class<T> type;
    
    public ArrayOptionType(OptionType<T> parent, Class<T> type) {
        this.parent = parent;
        this.type = type;
    }
    
    @Override
    public void write(Object value, ValueWriter writer, OptionTypesContext ctx) {
        if (value == null) {
            writer.writeNull();
        } else {
            writer.writeArray(arrayWriter -> {
                Preconditions.checkArgument(value.getClass().isArray(), value + " is not an array");
                for (int i = 0; i < Array.getLength(value); i++) {
                    this.parent.write((T) Array.get(value, i), arrayWriter, ctx);
                }
            });
        }
    }
    
    @Override
    public Object read(ValueReader reader) {
        List<T> values = new ArrayList<>();
        reader.readArray(arrayReader -> {
            values.add(this.parent.read(arrayReader));
        });
        Object array = Array.newInstance(type, values.size());
        int i = 0;
        for (T value : values) {
            Array.set(array, i, values.get(i++));
        }
        return array;
    }
    
    @Override
    public Object copy(Object value, OptionTypesContext ctx) {
        int length = Array.getLength(value);
        Object array = Array.newInstance(type, length);
        for (int i = 0; i < length; i++) {
            Array.set(array, i, this.parent.copy((T) Array.get(value, i), ctx));
        }
        return array;
    }
    
    @Override
    public boolean isNullable() {
        return true;
    }
    
    @Override
    @Nullable
    public T[] getDefaultValue() {
        return (T[]) Array.newInstance(type, 0);
    }
}

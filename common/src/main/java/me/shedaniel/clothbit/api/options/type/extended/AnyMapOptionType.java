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

package me.shedaniel.clothbit.api.options.type.extended;

import me.shedaniel.clothbit.api.options.OptionType;
import me.shedaniel.clothbit.api.options.OptionTypesContext;
import me.shedaniel.clothbit.api.serializers.reader.ValueReader;
import me.shedaniel.clothbit.api.serializers.writer.ValueWriter;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class AnyMapOptionType<T> implements OptionType<Map<String, T>> {
    private OptionType<T> valueType;
    
    public AnyMapOptionType(OptionType<T> valueType) {
        this.valueType = valueType;
    }
    
    @Override
    public void write(Map<String, T> value, ValueWriter writer, OptionTypesContext ctx) {
        writer.writeObject(this.valueType, ctx, objectWriter -> {
            for (Map.Entry<String, T> entry : value.entrySet()) {
                this.valueType.toOption(entry.getKey()).build().withValue(entry.getValue())
                        .write(objectWriter, ctx);
            }
        });
    }
    
    @Override
    public Map<String, T> read(ValueReader reader) {
        Map<String, T> value = new LinkedHashMap<>();
        reader.readObject((key, objectReader) -> {
            value.put(key, this.valueType.read(objectReader));
            return true;
        });
        return value;
    }
    
    @Override
    public boolean isNullable() {
        return true;
    }
    
    @Override
    @Nullable
    public Map<String, T> getDefaultValue() {
        return new LinkedHashMap<>();
    }
}

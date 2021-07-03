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

package me.shedaniel.clothbit.api.options.type.simple;

import me.shedaniel.clothbit.api.options.OptionTypesContext;
import me.shedaniel.clothbit.api.options.OptionType;
import me.shedaniel.clothbit.api.serializers.ReadType;
import me.shedaniel.clothbit.api.serializers.ValueReader;
import me.shedaniel.clothbit.api.serializers.ValueWriter;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnyOptionType implements OptionType<Object> {
    @Override
    public void write(Object value, ValueWriter writer, OptionTypesContext ctx) {
        OptionType<Object> type = ctx.resolveType((Class<Object>) value.getClass());
        if (type instanceof AnyOptionType) {
            throw new IllegalStateException("Can not resolve option type for " + value);
        } else {
            type.write(value, writer, ctx);
        }
    }
    
    @Override
    public Object read(ValueReader reader) {
        ReadType peek = reader.peek();
        switch (peek) {
            case BYTE:
                return reader.readByte();
            case SHORT:
                return reader.readShort();
            case INT:
                return reader.readInt();
            case LONG:
                return reader.readLong();
            case FLOAT:
                return reader.readFloat();
            case DOUBLE:
                return reader.readDouble();
            case OBJECT:
                return readMap(reader);
            case ARRAY:
                return readList(reader);
        }
        if (peek.isNumber()) return reader.readNumber();
        if (peek.isBoolean()) return reader.readBoolean();
        if (peek.isNull()) return reader.readNull();
        if (peek.isString()) return reader.readString();
        throw new IllegalStateException("Reading unknown type: " + peek);
    }
    
    private Map<String, Object> readMap(ValueReader reader) {
        Map<String, Object> data = new HashMap<>();
        reader.readObject((key, valueReader) -> {
            data.put(key, read(valueReader));
            return true;
        });
        return data;
    }
    
    private List<Object> readList(ValueReader reader) {
        List<Object> data = new ArrayList<>();
        reader.readArray(valueReader -> {
            data.add(read(valueReader));
        });
        return data;
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

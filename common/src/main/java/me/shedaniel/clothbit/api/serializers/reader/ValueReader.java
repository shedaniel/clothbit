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

package me.shedaniel.clothbit.api.serializers.reader;

import me.shedaniel.clothbit.api.options.OptionTypesContext;
import me.shedaniel.clothbit.api.serializers.ReadType;
import me.shedaniel.clothbit.api.serializers.ValueBuffer;
import me.shedaniel.clothbit.api.serializers.writer.ValueWriter;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

public interface ValueReader extends Closeable {
    @Nullable <T> T readNull();
    
    String readString();
    
    boolean readBoolean();
    
    char readCharacter();
    
    byte readByte();
    
    short readShort();
    
    int readInt();
    
    long readLong();
    
    float readFloat();
    
    double readDouble();
    
    Number readNumber();
    
    void readObject(BiPredicate<String, ValueReader> consumer);
    
    void readArray(Consumer<ValueReader> consumer);
    
    ReadType peek();
    
    @Override
    void close();
    
    default void writeTo(ValueWriter writer, OptionTypesContext ctx) {
        ValueBuffer buffer = new ValueBuffer();
        buffer.writeFrom(this, ctx);
        writer.writeAny(buffer.readAny(), ctx);
    }
    
    default Object readAny() {
        ReadType peek = peek();
        switch (peek) {
            case BYTE:
                return readByte();
            case SHORT:
                return readShort();
            case INT:
                return readInt();
            case LONG:
                return readLong();
            case FLOAT:
                return readFloat();
            case DOUBLE:
                return readDouble();
            case CHARACTER:
                return readCharacter();
            case OBJECT:
                Map<String, Object> dataMap = new LinkedHashMap<>();
                readObject((key, valueReader) -> {
                    dataMap.put(key, valueReader.readAny());
                    return true;
                });
                return dataMap;
            case ARRAY:
                List<Object> dataList = new ArrayList<>();
                readArray(valueReader -> {
                    dataList.add(valueReader.readAny());
                });
                return dataList;
        }
        if (peek.isNumber()) return readNumber();
        if (peek.isBoolean()) return readBoolean();
        if (peek.isNull()) return readNull();
        if (peek.isString()) return readString();
        throw new IllegalStateException("Reading unknown type: " + peek);
    }
}

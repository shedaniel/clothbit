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

package me.shedaniel.clothbit.api.serializers.writer;

import com.google.common.base.Preconditions;
import me.shedaniel.clothbit.api.options.Option;
import me.shedaniel.clothbit.api.options.OptionType;
import me.shedaniel.clothbit.api.options.OptionTypesContext;
import me.shedaniel.clothbit.api.options.type.simple.AnyOptionType;
import me.shedaniel.clothbit.api.options.type.simple.NullOptionType;
import me.shedaniel.clothbit.api.serializers.ReadType;
import me.shedaniel.clothbit.api.serializers.ValueBuffer;
import me.shedaniel.clothbit.api.serializers.reader.ValueReader;

import java.io.Closeable;
import java.util.function.Consumer;

public interface ValueWriter extends Closeable {
    void writeNull();
    
    void writeString(String value);
    
    void writeBoolean(boolean value);
    
    void writeCharacter(char value);
    
    void writeNumber(Number value);
    
    default void writeByte(byte value) {
        writeNumber(value);
    }
    
    default void writeShort(short value) {
        writeNumber(value);
    }
    
    default void writeInt(int value) {
        writeNumber(value);
    }
    
    default void writeLong(long value) {
        writeNumber(value);
    }
    
    default void writeFloat(float value) {
        writeNumber(value);
    }
    
    default void writeDouble(double value) {
        writeNumber(value);
    }
    
    void writeObject(Consumer<OptionWriter<Option<?>>> consumer);
    
    void writeArray(Consumer<ValueWriter> consumer);
    
    @Override
    void close();
    
    default void writeFrom(ValueReader reader, OptionTypesContext ctx) {
        ReadType peek = reader.peek();
        switch (peek) {
            case BYTE:
                writeByte(reader.readByte());
                return;
            case SHORT:
                writeShort(reader.readShort());
                return;
            case INT:
                writeInt(reader.readInt());
                return;
            case LONG:
                writeLong(reader.readLong());
                return;
            case FLOAT:
                writeFloat(reader.readFloat());
                return;
            case DOUBLE:
                writeDouble(reader.readDouble());
                return;
            case CHARACTER:
                writeCharacter(reader.readCharacter());
                return;
            case OBJECT:
                writeObject(writer -> {
                    reader.readObject((key, valueReader) -> {
                        ValueBuffer buffer = new ValueBuffer();
                        buffer.writeFrom(valueReader, ctx);
                        Object obj = buffer.peekObj();
                        OptionType<?> type = obj == null ? NullOptionType.getInstance() : ctx.resolveType(obj.getClass());
                        Option<?> option = type.toOption(key).build();
                        ValueWriter optionWriter = writer.forOption(option);
                        buffer.writeTo(optionWriter, ctx);
                        return true;
                    });
                });
                return;
            case ARRAY:
                writeArray(writer -> {
                    reader.readArray(valueReader -> {
                        writer.writeFrom(valueReader, ctx);
                    });
                });
                return;
        }
        if (peek.isNumber()) writeNumber(reader.readNumber());
        else if (peek.isBoolean()) writeBoolean(reader.readBoolean());
        else if (peek.isNull()) {
            Object aNull = reader.readNull();
            Preconditions.checkArgument(aNull == null, "readNull did not return null!");
            writeNull();
        } else if (peek.isString()) writeString(reader.readString());
        throw new IllegalStateException("Reading unknown type: " + peek);
    }
    
    default void writeAny(Object value, OptionTypesContext ctx) {
        if (value == null) {
            writeNull();
            return;
        }
        OptionType<Object> type = ctx.resolveType((Class<Object>) value.getClass());
        if (type instanceof AnyOptionType) {
            throw new IllegalStateException("Can not resolve option type for " + value);
        } else {
            type.write(value, this, ctx);
        }
    }
}

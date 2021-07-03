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

package me.shedaniel.clothbit.api.serializers;

import me.shedaniel.clothbit.api.options.Option;

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
}

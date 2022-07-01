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

import me.shedaniel.clothbit.api.serializers.ReadType;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiPredicate;
import java.util.function.Consumer;

public interface NothingValueReader extends ValueReader {
    @Override
    @Nullable
    default <T> T readNull() {
        throw new UnsupportedOperationException("Could not read value from nothing");
    }
    
    @Override
    default String readString() {
        throw new UnsupportedOperationException("Could not read value from nothing");
    }
    
    @Override
    default boolean readBoolean() {
        throw new UnsupportedOperationException("Could not read value from nothing");
    }
    
    @Override
    default char readCharacter() {
        throw new UnsupportedOperationException("Could not read value from nothing");
    }
    
    @Override
    default byte readByte() {
        throw new UnsupportedOperationException("Could not read value from nothing");
    }
    
    @Override
    default short readShort() {
        throw new UnsupportedOperationException("Could not read value from nothing");
    }
    
    @Override
    default int readInt() {
        throw new UnsupportedOperationException("Could not read value from nothing");
    }
    
    @Override
    default long readLong() {
        throw new UnsupportedOperationException("Could not read value from nothing");
    }
    
    @Override
    default float readFloat() {
        throw new UnsupportedOperationException("Could not read value from nothing");
    }
    
    @Override
    default double readDouble() {
        throw new UnsupportedOperationException("Could not read value from nothing");
    }
    
    @Override
    default Number readNumber() {
        throw new UnsupportedOperationException("Could not read value from nothing");
    }
    
    @Override
    default void readObject(BiPredicate<String, ValueReader> consumer) {
        throw new UnsupportedOperationException("Could not read value from nothing");
    }
    
    @Override
    default void readArray(Consumer<ValueReader> consumer) {
        throw new UnsupportedOperationException("Could not read value from nothing");
    }
    
    @Override
    default ReadType peek() {
        throw new UnsupportedOperationException("Could not read value from nothing");
    }
    
    @Override
    default void close() {}
}

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

import me.shedaniel.clothbit.api.options.Option;
import me.shedaniel.clothbit.api.options.OptionType;

import java.util.function.Consumer;

public interface NothingValueWriter extends ValueWriter {
    @Override
    default void writeNull() {
        throw new UnsupportedOperationException("Can not start writing values to nothing!");
    }
    
    @Override
    default void writeString(String value) {
        throw new UnsupportedOperationException("Can not start writing values to nothing!");
    }
    
    @Override
    default void writeBoolean(boolean value) {
        throw new UnsupportedOperationException("Can not start writing values to nothing!");
    }
    
    @Override
    default void writeCharacter(char value) {
        throw new UnsupportedOperationException("Can not start writing values to nothing!");
    }
    
    @Override
    default void writeNumber(Number value) {
        throw new UnsupportedOperationException("Can not start writing values to nothing!");
    }
    
    @Override
    default void writeObject(Consumer<OptionWriter<Option<?>>> consumer) {
        throw new UnsupportedOperationException("Can not start writing values to nothing!");
    }
    
    @Override
    default void writeArray(Consumer<OptionWriter<OptionType<?>>> consumer) {
        throw new UnsupportedOperationException("Can not start writing values to nothing!");
    }
    
    @Override
    default void close() {}
}

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

package me.shedaniel.clothbit.api.options;

import me.shedaniel.clothbit.api.serializers.ValueReader;
import me.shedaniel.clothbit.api.serializers.ValueWriter;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * An option type is an interface to read and write values from writers and readers.
 *
 * @param <T> the type of option
 */
public interface OptionType<T> {
    void write(T value, ValueWriter writer, OptionTypesContext ctx);
    
    T read(ValueReader reader);
    
    boolean isNullable();
    
    @Nullable
    T getDefaultValue();
    
    default <R extends T> OptionValue<T> withValue(R value) {
        return OptionValue.of(this, value);
    }
    
    default <R extends T> Option.Builder<T> toOption(String name) {
        return Option.builder(this, name);
    }
    
    default <R> OptionType<R> map(Function<T, R> forwards, Function<R, T> backwards) {
        OptionType<T> self = this;
        return new OptionType<R>() {
            @Override
            public void write(R value, ValueWriter writer, OptionTypesContext ctx) {
                self.write(backwards.apply(value), writer, ctx);
            }
            
            @Override
            public R read(ValueReader reader) {
                return forwards.apply(self.read(reader));
            }
            
            @Override
            @Nullable
            public R getDefaultValue() {
                return forwards.apply(self.getDefaultValue());
            }
            
            @Override
            public boolean isNullable() {
                return self.isNullable();
            }
        };
    }
}

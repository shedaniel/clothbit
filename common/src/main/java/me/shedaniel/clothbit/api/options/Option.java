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

package me.shedaniel.clothbit.api.options;

import me.shedaniel.clothbit.api.options.property.CommentsOptionProperty;
import me.shedaniel.clothbit.api.options.property.NullableOptionProperty;
import me.shedaniel.clothbit.impl.options.OptionImpl;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * An option, holds properties and default values, but without the actual value.
 *
 * @param <T> the type of option
 */
@ApiStatus.NonExtendable
public interface Option<T> extends OptionPropertyExtension {
    static <T> Option.Builder<T> builder(OptionType<T> type, String name) {
        return new OptionImpl.Builder<>(type, name);
    }
    
    OptionType<T> getType();
    
    String getName();
    
    @Nullable
    T getDefaultValue();
    
    default OptionPair<T> withValue(T value) {
        return OptionPair.of(this, value);
    }
    
    default OptionPair<T> withDefaultValue() {
        return withValue(getDefaultValue());
    }
    
    @ApiStatus.NonExtendable
    interface Builder<T> {
        Builder<T> name(String name);
        
        Builder<T> defaultValue(@Nullable Supplier<T> value);
        
        default Builder<T> defaultValue(@Nullable T value) {
            return defaultValue(() -> value);
        }
        
        <S> Builder<T> property(OptionProperty<S> property, S value);
        
        default Builder<T> comments(@Nullable String comments) {
            return property(CommentsOptionProperty.property(), comments);
        }
        
        default Builder<T> nullable(NullableOptionProperty.HandleMode nullable) {
            return property(NullableOptionProperty.property(), nullable);
        }
        
        Option<T> build();
    }
}

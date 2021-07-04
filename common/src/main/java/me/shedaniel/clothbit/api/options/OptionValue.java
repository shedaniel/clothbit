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

import me.shedaniel.clothbit.api.serializers.writer.ValueWriter;
import me.shedaniel.clothbit.impl.options.OptionValueImpl;
import org.jetbrains.annotations.ApiStatus;

/**
 * A compound object holding the type of the option and the value of the option,
 * without the information of the field (like field names).
 *
 * @param <T> the type of option
 * @see OptionPair
 */
@ApiStatus.NonExtendable
public interface OptionValue<T> {
    static <T> OptionValue<T> of(OptionType<T> type, T value) {
        return new OptionValueImpl<>(type, value);
    }
    
    OptionType<T> getType();
    
    T getValue();
    
    default OptionPair<T> promote(Option<T> option) {
        return OptionPair.of(option, getValue());
    }
    
    default void write(ValueWriter writer, OptionTypesContext ctx) {
        getType().write(getValue(), writer, ctx);
    }
}

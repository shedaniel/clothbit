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

import me.shedaniel.clothbit.api.serializers.OptionWriter;
import me.shedaniel.clothbit.api.serializers.ValueWriter;
import me.shedaniel.clothbit.impl.options.OptionPairImpl;
import org.jetbrains.annotations.ApiStatus;

/**
 * A compound object holding the option and the value of the option,
 * with the information of the field (like field names).
 *
 * @param <T> the type of option
 * @see Option
 * @see OptionValue
 */
@ApiStatus.NonExtendable
public interface OptionPair<T> extends OptionValue<T> {
    static <T> OptionPair<T> of(Option<T> option, T value) {
        return new OptionPairImpl<>(option, value);
    }
    
    Option<T> getOption();
    
    default OptionType<T> getType() {
        return getOption().getType();
    }
    
    @Override
    default OptionPair<T> promote(Option<T> option) {
        if (option == getOption()) return this;
        return OptionValue.super.promote(option);
    }
    
    default void write(OptionWriter<? super Option<T>> writer, OptionTypesContext ctx) {
        try (ValueWriter valueWriter = writer.forOption(getOption())) {
            getOption().getType().write(getValue(), valueWriter, ctx);
        }
    }
}

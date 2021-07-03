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

package me.shedaniel.clothbit.impl.options;

import me.shedaniel.clothbit.api.options.Option;
import me.shedaniel.clothbit.api.options.OptionPair;

public class OptionPairImpl<T> implements OptionPair<T> {
    private final Option<T> option;
    private final T value;
    
    public OptionPairImpl(Option<T> option, T value) {
        this.option = option;
        this.value = value;
    }
    
    @Override
    public Option<T> getOption() {
        return option;
    }
    
    @Override
    public T getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return "OptionPairImpl{" +
               "option=" + option +
               ", value=" + value +
               '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OptionPairImpl)) return false;
        OptionPairImpl<?> that = (OptionPairImpl<?>) o;
        
        if (!option.equals(that.option)) return false;
        return value.equals(that.value);
    }
    
    @Override
    public int hashCode() {
        int result = option.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }
}

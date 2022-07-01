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

package me.shedaniel.clothbit.impl.options;

import me.shedaniel.clothbit.api.options.OptionType;
import me.shedaniel.clothbit.api.options.OptionValue;

public class OptionValueImpl<T> implements OptionValue<T> {
    private final OptionType<T> type;
    private final T value;
    
    public OptionValueImpl(OptionType<T> type, T value) {
        this.type = type;
        this.value = value;
    }
    
    @Override
    public OptionType<T> getType() {
        return type;
    }
    
    @Override
    public T getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return "OptionValueImpl{" +
               "type=" + type +
               ", value=" + value +
               '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OptionValueImpl)) return false;
        OptionValueImpl<?> that = (OptionValueImpl<?>) o;
        
        if (!type.equals(that.type)) return false;
        return value.equals(that.value);
    }
    
    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }
}

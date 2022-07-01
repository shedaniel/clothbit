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

package me.shedaniel.clothbit.api.serializers;

import me.shedaniel.clothbit.api.options.OptionType;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class TypedList<T> implements TypedValue<Collection<T>> {
    private final OptionType<Collection<T>> type;
    private List<TypedValue<T>> values;
    
    public TypedList(OptionType<Collection<T>> type, List<TypedValue<T>> values) {
        this.type = type;
        this.values = values;
    }
    
    @Override
    public OptionType<Collection<T>> getType() {
        return type;
    }
    
    @Override
    public Collection<T> getValue() {
        return values.stream().map(value -> value == null ? null : value.getValue()).collect(Collectors.toList());
    }
    
    public List<TypedValue<T>> getValues() {
        return values;
    }
}

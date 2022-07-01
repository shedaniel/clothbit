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

import java.util.LinkedHashMap;
import java.util.Map;

public class TypedMap<T> implements TypedValue<Map<String, T>> {
    private final OptionType<Map<String, T>> type;
    private Map<String, TypedValue<T>> values;
    
    public TypedMap(OptionType<Map<String, T>> type, Map<String, TypedValue<T>> values) {
        this.type = type;
        this.values = values;
    }
    
    @Override
    public OptionType<Map<String, T>> getType() {
        return type;
    }
    
    @Override
    public Map<String, T> getValue() {
        Map<String, T> map = new LinkedHashMap<>();
        for (Map.Entry<String, TypedValue<T>> entry : values.entrySet()) {
            TypedValue<T> value = entry.getValue();
            map.put(entry.getKey(), value == null ? null : value.getValue());
        }
        return map;
    }
    
    public Map<String, TypedValue<T>> getValues() {
        return values;
    }
}

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

package me.shedaniel.clothbit.api.options.type.adapter.extended;

import com.google.gson.reflect.TypeToken;
import me.shedaniel.clothbit.api.options.OptionTypesContext;
import me.shedaniel.clothbit.api.options.OptionType;
import me.shedaniel.clothbit.api.options.OptionTypeAdapter;
import me.shedaniel.clothbit.api.options.type.simple.StringOptionType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EnumOptionTypeAdapter implements OptionTypeAdapter {
    @Override
    public <R> Optional<OptionType<? extends R>> forType(TypeToken<R> typeToken, OptionTypesContext ctx) {
        Class<? super R> rawType = typeToken.getRawType();
        if (Enum.class.isAssignableFrom(rawType)) {
            Object[] enumConstants = rawType.getEnumConstants();
            Map<String, Object> enumMap = new HashMap<>();
            for (Object constant : enumConstants) {
                enumMap.put(((Enum<?>) constant).name(), constant);
            }
            return Optional.of((OptionType<? extends R>) new StringOptionType()
                    .map(enumMap::get, obj -> ((Enum<?>) obj).name()));
        }
        
        return Optional.empty();
    }
}

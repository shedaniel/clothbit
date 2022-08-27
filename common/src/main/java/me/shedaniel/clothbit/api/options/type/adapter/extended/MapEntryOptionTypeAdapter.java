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

import com.google.common.collect.ImmutableMap;
import com.google.gson.reflect.TypeToken;
import me.shedaniel.clothbit.api.options.OptionType;
import me.shedaniel.clothbit.api.options.OptionTypeAdapter;
import me.shedaniel.clothbit.api.options.OptionTypesContext;
import me.shedaniel.clothbit.api.options.type.extended.OptionedMapOptionType;
import me.shedaniel.clothbit.api.options.type.simple.AnyOptionType;

import java.lang.reflect.ParameterizedType;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public class MapEntryOptionTypeAdapter implements OptionTypeAdapter {
    @Override
    public <R> Optional<OptionType<? extends R>> forType(TypeToken<R> typeToken, OptionTypesContext ctx) {
        Class<? super R> rawType = typeToken.getRawType();
        
        if (Map.Entry.class.isAssignableFrom(rawType)) {
            if (typeToken.getType() instanceof ParameterizedType) {
                return Optional.of(new OptionedMapOptionType(Arrays.asList(
                        ctx.resolveType(((ParameterizedType) typeToken.getType()).getActualTypeArguments()[0]).toOption("key").build(),
                        ctx.resolveType(((ParameterizedType) typeToken.getType()).getActualTypeArguments()[1]).toOption("value").build()
                )).<Map.Entry<?, ?>>map(stringMap -> new AbstractMap.SimpleEntry<>(stringMap.get("key"), stringMap.get("value")),
                        entry -> ImmutableMap.of("key", entry.getKey(), "value", entry.getValue())).cast());
            } else {
                return Optional.of(new OptionedMapOptionType(Arrays.asList(
                        AnyOptionType.instance().toOption("key").build(),
                        AnyOptionType.instance().toOption("value").build()
                )).<Map.Entry<?, ?>>map(stringMap -> new AbstractMap.SimpleEntry<>(stringMap.get("key"), stringMap.get("value")),
                        entry -> ImmutableMap.of("key", entry.getKey(), "value", entry.getValue())).cast());
            }
        }
        
        return Optional.empty();
    }
}

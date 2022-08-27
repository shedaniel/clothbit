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
import com.mojang.datafixers.util.Pair;
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

public class PairOptionTypeAdapter implements OptionTypeAdapter {
    @Override
    public <R> Optional<OptionType<? extends R>> forType(TypeToken<R> typeToken, OptionTypesContext ctx) {
        Class<? super R> rawType = typeToken.getRawType();
        
        if (Pair.class.isAssignableFrom(rawType)) {
            if (typeToken.getType() instanceof ParameterizedType) {
                return Optional.of(createOptionedMap(typeToken, ctx)
                        .<Pair<?, ?>>map(stringMap -> new Pair<>(stringMap.get("left"), stringMap.get("right")),
                                pair -> ImmutableMap.of("left", pair.getFirst(), "right", pair.getSecond())).cast());
            } else {
                return Optional.of(createUnoptionedMap(ctx)
                        .<Pair<?, ?>>map(stringMap -> new Pair<>(stringMap.get("left"), stringMap.get("right")),
                                pair -> ImmutableMap.of("left", pair.getFirst(), "right", pair.getSecond())).cast());
            }
        } else if (org.apache.commons.lang3.tuple.Pair.class.isAssignableFrom(rawType)) {
            if (typeToken.getType() instanceof ParameterizedType) {
                return Optional.of(createOptionedMap(typeToken, ctx)
                        .<org.apache.commons.lang3.tuple.Pair<?, ?>>map(stringMap -> org.apache.commons.lang3.tuple.Pair.of(stringMap.get("left"), stringMap.get("right")),
                                pair -> ImmutableMap.of("left", pair.getLeft(), "right", pair.getRight())).cast());
            } else {
                return Optional.of(createUnoptionedMap(ctx)
                        .<org.apache.commons.lang3.tuple.Pair<?, ?>>map(stringMap -> org.apache.commons.lang3.tuple.Pair.of(stringMap.get("left"), stringMap.get("right")),
                                pair -> ImmutableMap.of("left", pair.getLeft(), "right", pair.getRight())).cast());
            }
        }
        
        return Optional.empty();
    }
    
    private static <R> OptionedMapOptionType createOptionedMap(TypeToken<R> typeToken, OptionTypesContext ctx) {
        return new OptionedMapOptionType(Arrays.asList(
                ctx.resolveType(((ParameterizedType) typeToken.getType()).getActualTypeArguments()[0]).toOption("left").build(),
                ctx.resolveType(((ParameterizedType) typeToken.getType()).getActualTypeArguments()[1]).toOption("right").build()
        ));
    }
    
    private static <R> OptionedMapOptionType createUnoptionedMap(OptionTypesContext ctx) {
        return new OptionedMapOptionType(Arrays.asList(
                AnyOptionType.instance().toOption("left").build(),
                AnyOptionType.instance().toOption("right").build()
        ));
    }
}

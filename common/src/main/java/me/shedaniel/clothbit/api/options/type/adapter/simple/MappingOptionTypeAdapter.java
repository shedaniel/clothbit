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

package me.shedaniel.clothbit.api.options.type.adapter.simple;

import com.google.gson.reflect.TypeToken;
import me.shedaniel.clothbit.api.options.OptionType;
import me.shedaniel.clothbit.api.options.OptionTypeAdapter;
import me.shedaniel.clothbit.api.options.OptionTypesContext;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class MappingOptionTypeAdapter<T, R> implements OptionTypeAdapter {
    private final TypeToken<T> from;
    private final TypeToken<R> to;
    private final Function<T, R> forwards;
    private final Function<R, T> backwards;
    
    public MappingOptionTypeAdapter(Class<T> from, Class<R> to, Function<T, R> forwards, Function<R, T> backwards) {
        this(TypeToken.get(from), TypeToken.get(to), forwards, backwards);
    }
    
    public MappingOptionTypeAdapter(TypeToken<T> from, TypeToken<R> to, Function<T, R> forwards, Function<R, T> backwards) {
        this.from = from;
        this.to = to;
        this.forwards = forwards;
        this.backwards = backwards;
    }
    
    @Override
    public <B> Optional<OptionType<? extends B>> forType(TypeToken<B> typeToken, OptionTypesContext ctx) {
        if (Objects.equals(to, typeToken)) {
            return Optional.of((OptionType<? extends B>) ctx.resolveType(from).map(forwards, backwards));
        }
        return Optional.empty();
    }
}

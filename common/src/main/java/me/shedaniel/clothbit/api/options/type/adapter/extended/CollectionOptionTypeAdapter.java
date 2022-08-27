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
import me.shedaniel.clothbit.api.options.OptionType;
import me.shedaniel.clothbit.api.options.OptionTypeAdapter;
import me.shedaniel.clothbit.api.options.OptionTypesContext;
import me.shedaniel.clothbit.api.options.type.extended.CollectionOptionType;
import me.shedaniel.clothbit.api.options.type.extended.ListOptionType;
import me.shedaniel.clothbit.api.options.type.extended.SetOptionType;
import me.shedaniel.clothbit.api.options.type.simple.AnyOptionType;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class CollectionOptionTypeAdapter implements OptionTypeAdapter {
    @Override
    public <R> Optional<OptionType<? extends R>> forType(TypeToken<R> typeToken, OptionTypesContext ctx) {
        Class<? super R> rawType = typeToken.getRawType();
        if (List.class.isAssignableFrom(rawType)) {
            if (typeToken.getType() instanceof ParameterizedType) {
                OptionType<Object> type = inner(typeToken, ctx);
                return Optional.of(new ListOptionType<>(type).cast());
            } else {
                return Optional.of(new ListOptionType<>(AnyOptionType.instance()).cast());
            }
        } else if (Set.class.isAssignableFrom(rawType)) {
            if (typeToken.getType() instanceof ParameterizedType) {
                OptionType<Object> type = inner(typeToken, ctx);
                return Optional.of(new ListOptionType<>(type).cast());
            } else {
                return Optional.of(new SetOptionType<>(AnyOptionType.instance()).cast());
            }
        } else if (Collection.class.isAssignableFrom(rawType)) {
            if (typeToken.getType() instanceof ParameterizedType) {
                OptionType<Object> type = inner(typeToken, ctx);
                return Optional.of(new CollectionOptionType<>(type).cast());
            } else {
                return Optional.of(new CollectionOptionType<>(AnyOptionType.instance()).cast());
            }
        }
        
        return Optional.empty();
    }
    
    @Nullable
    private static <R> OptionType<Object> inner(TypeToken<R> typeToken, OptionTypesContext ctx) {
        return ctx.resolveType(((ParameterizedType) typeToken.getType()).getActualTypeArguments()[0]);
    }
}

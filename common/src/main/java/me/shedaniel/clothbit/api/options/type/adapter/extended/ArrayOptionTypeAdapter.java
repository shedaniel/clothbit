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
import me.shedaniel.clothbit.api.options.type.extended.ArrayOptionType;
import me.shedaniel.clothbit.impl.utils.TypeUtils;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.Optional;

public class ArrayOptionTypeAdapter implements OptionTypeAdapter {
    @Override
    public <R> Optional<OptionType<? extends R>> forType(TypeToken<R> typeToken, OptionTypesContext ctx) {
        Type type = typeToken.getType();
        if (type instanceof GenericArrayType || (type instanceof Class && ((Class<?>) type).isArray())) {
            Type elementType = type instanceof GenericArrayType ? ((GenericArrayType) type).getGenericComponentType() :
                    ((Class<?>) type).getComponentType();
            return Optional.of((OptionType<? extends R>) new ArrayOptionType<>(ctx.resolveType(elementType), TypeUtils.getRawType(elementType)));
        }
        return Optional.empty();
    }
}

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

import com.google.common.collect.ImmutableSet;
import com.google.gson.reflect.TypeToken;
import me.shedaniel.clothbit.api.options.OptionTypesContext;
import me.shedaniel.clothbit.api.options.OptionType;
import me.shedaniel.clothbit.api.options.OptionTypeAdapter;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.Set;

public class SimpleOptionTypeAdapter<T> implements OptionTypeAdapter {
    private OptionTypeConstructor<T> constructor;
    private Set<Type> types;
    
    public SimpleOptionTypeAdapter(OptionTypeConstructor<T> constructor, Type... types) {
        this.constructor = constructor;
        this.types = ImmutableSet.copyOf(types);
    }
    
    @Override
    public <R> Optional<OptionType<? extends R>> forType(TypeToken<R> typeToken, OptionTypesContext ctx) {
        if (types.contains(typeToken.getType())) {
            return Optional.of((OptionType<? extends R>) constructor.construct());
        }
        
        return Optional.empty();
    }
    
    public interface OptionTypeConstructor<T> {
        OptionType<T> construct();
    }
}

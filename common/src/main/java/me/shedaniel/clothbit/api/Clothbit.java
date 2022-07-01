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

package me.shedaniel.clothbit.api;

import com.google.gson.reflect.TypeToken;
import me.shedaniel.clothbit.api.options.OptionTypesContext;
import me.shedaniel.clothbit.api.options.OptionType;
import me.shedaniel.clothbit.api.serializers.format.Serializer;
import me.shedaniel.clothbit.impl.ClothbitImpl;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.Type;

@ApiStatus.NonExtendable
public interface Clothbit {
    static Builder builder() {
        return new ClothbitImpl.Builder();
    }
    
    default <T> OptionType<T> resolveType(Type type) {
        return resolveType((TypeToken<T>) TypeToken.get(type));
    }
    
    default <T> OptionType<T> resolveType(Class<T> type) {
        return resolveType(TypeToken.get(type));
    }
    
    <T> OptionType<T> resolveType(TypeToken<T> type);
    
    @ApiStatus.NonExtendable
    interface Builder {
        Builder format(Serializer serializer);
        
        Serializer getFormat();
        
        Builder constructingContext(OptionTypesContext ctx);
        
        OptionTypesContext getConstructingContext();
        
        Clothbit build();
    }
}

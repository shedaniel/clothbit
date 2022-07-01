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

package me.shedaniel.clothbit.impl;

import com.google.gson.reflect.TypeToken;
import me.shedaniel.clothbit.api.Clothbit;
import me.shedaniel.clothbit.api.options.OptionTypesContext;
import me.shedaniel.clothbit.api.options.OptionType;
import me.shedaniel.clothbit.api.serializers.format.Serializer;

public class ClothbitImpl implements Clothbit {
    private final Serializer serializer;
    private final OptionTypesContext optionTypesContext;
    
    private ClothbitImpl(Serializer serializer, OptionTypesContext optionTypesContext) {
        this.serializer = serializer;
        this.optionTypesContext = optionTypesContext;
    }
    
    @Override
    public <T> OptionType<T> resolveType(TypeToken<T> type) {
        return optionTypesContext.resolveType(type);
    }
    
    public static class Builder implements Clothbit.Builder {
        private Serializer serializer;
        private OptionTypesContext optionTypesContext = OptionTypesContext.defaultContext();
        
        @Override
        public Clothbit.Builder format(Serializer serializer) {
            this.serializer = serializer;
            return this;
        }
        
        @Override
        public Serializer getFormat() {
            return serializer;
        }
        
        @Override
        public Clothbit.Builder constructingContext(OptionTypesContext ctx) {
            this.optionTypesContext = ctx;
            return this;
        }
        
        @Override
        public OptionTypesContext getConstructingContext() {
            return optionTypesContext;
        }
        
        @Override
        public Clothbit build() {
            return new ClothbitImpl(serializer, optionTypesContext);
        }
    }
}

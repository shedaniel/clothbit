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

package me.shedaniel.clothbit.impl.options;

import com.google.common.collect.ImmutableMap;
import me.shedaniel.clothbit.api.options.Option;
import me.shedaniel.clothbit.api.options.OptionProperty;
import me.shedaniel.clothbit.api.options.OptionType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class OptionImpl<T> implements Option<T> {
    private final Map<OptionProperty<?>, Object> properties;
    private final OptionType<T> type;
    private final String name;
    private final Supplier<T> defaultValue;
    
    public OptionImpl(Map<OptionProperty<?>, Object> properties, OptionType<T> type, String name, Supplier<T> defaultValue) {
        this.properties = properties;
        this.type = type;
        this.name = name;
        this.defaultValue = defaultValue;
        for (Map.Entry<OptionProperty<?>, ?> entry : this.properties.entrySet()) {
            ((OptionProperty<Object>) entry.getKey()).validatePropertyValue(this, entry.getValue());
        }
    }
    
    @Override
    public OptionType<T> getType() {
        return type;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public T getDefaultValue() {
        T t = defaultValue == null ? null : defaultValue.get();
        return t == null ? getType().getDefaultValue() : t;
    }
    
    @Override
    public <S> S getProperty(OptionProperty<S> property) {
        return (S) properties.getOrDefault(property, property.getDefaultValue());
    }
    
    public static class Builder<T> implements Option.Builder<T> {
        private final OptionType<T> type;
        private final Map<OptionProperty<?>, ?> properties = new HashMap<>();
        private String name;
        private Supplier<T> defaultValue;
        
        public Builder(OptionType<T> type, String name) {
            this.type = Objects.requireNonNull(type, "type");
            this.name = Objects.requireNonNull(name, "name");
        }
        
        @Override
        public Option.Builder<T> name(String name) {
            this.name = name;
            return this;
        }
        
        @Override
        public Option.Builder<T> defaultValue(@Nullable Supplier<T> value) {
            this.defaultValue = value;
            return this;
        }
        
        @SuppressWarnings("rawtypes")
        @Override
        public <S> Option.Builder<T> property(OptionProperty<S> property, S value) {
            ((Map) properties).put(property, value);
            return this;
        }
        
        @Override
        public Option<T> build() {
            return new OptionImpl<>(ImmutableMap.copyOf(properties), type, name, defaultValue);
        }
    }
}

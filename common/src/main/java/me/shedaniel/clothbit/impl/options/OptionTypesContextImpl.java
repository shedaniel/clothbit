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

import com.google.gson.reflect.TypeToken;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.shedaniel.clothbit.api.options.OptionType;
import me.shedaniel.clothbit.api.options.OptionTypeAdapter;
import me.shedaniel.clothbit.api.options.OptionTypesContext;
import me.shedaniel.clothbit.api.options.type.NullSafetyOptionType;
import me.shedaniel.clothbit.api.options.type.adapter.extended.ArrayOptionTypeAdapter;
import me.shedaniel.clothbit.api.options.type.adapter.extended.EnumOptionTypeAdapter;
import me.shedaniel.clothbit.api.options.type.adapter.extended.MapOptionTypeAdapter;
import me.shedaniel.clothbit.api.options.type.adapter.reflect.ReflectOptionTypeAdapter;
import me.shedaniel.clothbit.api.options.type.adapter.simple.AnyOptionTypeAdapter;
import me.shedaniel.clothbit.api.options.type.adapter.simple.MappingOptionTypeAdapter;
import me.shedaniel.clothbit.api.options.type.adapter.simple.PrimitiveOptionTypeAdapter;
import me.shedaniel.clothbit.api.options.type.adapter.simple.SimpleOptionTypeAdapter;
import me.shedaniel.clothbit.api.options.type.simple.BooleanOptionType;
import me.shedaniel.clothbit.api.options.type.simple.CharacterOptionType;
import me.shedaniel.clothbit.api.options.type.simple.StringOptionType;
import me.shedaniel.clothbit.api.options.type.simple.number.*;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class OptionTypesContextImpl implements OptionTypesContext {
    private final List<OptionTypeAdapter> adapters = new ArrayList<>();
    private final Map<TypeToken<?>, OptionType<?>> cache = new ConcurrentHashMap<>();
    
    public static OptionTypesContext defaultContext() {
        OptionTypesContextImpl context = new OptionTypesContextImpl();
        context.addDefaults();
        return context;
    }
    
    private void addDefaults() {
        this.adapters.add(new NullSafetyAdapter(new SimpleOptionTypeAdapter<>(StringOptionType::new, String.class)));
        this.adapters.add(new NullSafetyAdapter(new PrimitiveOptionTypeAdapter<>(ByteOptionType::new, Byte.TYPE, Byte.class)));
        this.adapters.add(new NullSafetyAdapter(new PrimitiveOptionTypeAdapter<>(ShortOptionType::new, Short.TYPE, Short.class)));
        this.adapters.add(new NullSafetyAdapter(new PrimitiveOptionTypeAdapter<>(IntOptionType::new, Integer.TYPE, Integer.class)));
        this.adapters.add(new NullSafetyAdapter(new PrimitiveOptionTypeAdapter<>(LongOptionType::new, Long.TYPE, Long.class)));
        this.adapters.add(new NullSafetyAdapter(new PrimitiveOptionTypeAdapter<>(FloatOptionType::new, Float.TYPE, Float.class)));
        this.adapters.add(new NullSafetyAdapter(new PrimitiveOptionTypeAdapter<>(DoubleOptionType::new, Double.TYPE, Double.class)));
        this.adapters.add(new NullSafetyAdapter(new PrimitiveOptionTypeAdapter<>(BooleanOptionType::new, Boolean.TYPE, Boolean.class)));
        this.adapters.add(new NullSafetyAdapter(new PrimitiveOptionTypeAdapter<>(CharacterOptionType::new, Character.TYPE, Character.class)));
        this.adapters.add(new NullSafetyAdapter(new SimpleOptionTypeAdapter<>(() -> new NumberOptionType<>(num -> num == null || num instanceof BigInteger ? num : new BigInteger(num.toString())), BigInteger.class)));
        this.adapters.add(new NullSafetyAdapter(new SimpleOptionTypeAdapter<>(() -> new NumberOptionType<>(num -> num == null || num instanceof BigDecimal ? num : new BigDecimal(num.toString())), BigDecimal.class)));
        this.adapters.add(new NullSafetyAdapter(new MappingOptionTypeAdapter<>(String.class, ResourceLocation.class, ResourceLocation::new, Objects::toString)));
        this.adapters.add(new NullSafetyAdapter(new MappingOptionTypeAdapter<>(String.class, Tag.class, s -> {
            try {
                return (Tag) TagParser.parseTag(s);
            } catch (CommandSyntaxException e) {
                throw new RuntimeException(e);
            }
        }, Objects::toString)));
        this.adapters.add(new NullSafetyAdapter(new EnumOptionTypeAdapter()));
        this.adapters.add(new NullSafetyAdapter(new ArrayOptionTypeAdapter()));
        this.adapters.add(new NullSafetyAdapter(new MapOptionTypeAdapter()));
        this.adapters.add(new NullSafetyAdapter(new AnyOptionTypeAdapter()));
        this.adapters.add(new NullSafetyAdapter(new ReflectOptionTypeAdapter()));
    }
    
    @Override
    public <T> OptionType<T> resolveType(TypeToken<T> type) {
        OptionType<?> optionType = cache.get(type);
        if (optionType != null) return (OptionType<T>) optionType;
        for (OptionTypeAdapter adapter : adapters) {
            Optional<OptionType<? extends T>> optional = adapter.forType(type, this);
            if (optional.isPresent()) {
                optionType = optional.get();
                cache.put(type, optionType);
                return (OptionType<T>) optionType;
            }
        }
        return null;
    }
    
    @Override
    public void addAdapter(OptionTypeAdapter adapter) {
        this.adapters.add(0, new NullSafetyAdapter(adapter));
    }
    
    private static class NullSafetyAdapter implements OptionTypeAdapter {
        private final OptionTypeAdapter parent;
        
        public NullSafetyAdapter(OptionTypeAdapter parent) {
            this.parent = parent;
        }
        
        @Override
        public <R> Optional<OptionType<? extends R>> forType(TypeToken<R> typeToken, OptionTypesContext ctx) {
            return this.parent.forType(typeToken, ctx).map(NullSafetyOptionType::new);
        }
    }
}

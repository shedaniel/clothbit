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

package me.shedaniel.clothbit.api.options.type.adapter.reflect;

import com.google.gson.reflect.TypeToken;
import com.mojang.datafixers.util.Pair;
import me.shedaniel.clothbit.api.annotations.*;
import me.shedaniel.clothbit.api.options.Option;
import me.shedaniel.clothbit.api.options.OptionType;
import me.shedaniel.clothbit.api.options.OptionTypeAdapter;
import me.shedaniel.clothbit.api.options.OptionTypesContext;
import me.shedaniel.clothbit.api.options.type.extended.OptionedMapOptionType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class ReflectOptionTypeAdapter implements OptionTypeAdapter {
    @Override
    public <R> Optional<OptionType<? extends R>> forType(TypeToken<R> typeToken, OptionTypesContext ctx) {
        Class<? super R> rawType = typeToken.getRawType();
        Constructor<? super R> constructor;
        try {
            constructor = rawType.getDeclaredConstructor();
        } catch (NoSuchMethodException exception) {
            throw new RuntimeException("Expected constructor with no parameters!", exception);
        }
        try {
            List<Pair<Option<Object>, Field>> options = new ArrayList<>();
            for (Field field : rawType.getDeclaredFields()) {
                if (!field.isSynthetic() && !Modifier.isStatic(field.getModifiers())
                    && !Modifier.isFinal(field.getModifiers()) && !Modifier.isTransient(field.getModifiers()) &&
                    field.getAnnotation(IgnoreField.class) == null) {
                    field.setAccessible(true);
                    TypeToken<?> fieldType = TypeToken.get(field.getType());
                    OptionType<? extends R> optionType = Objects.requireNonNull((OptionType<? extends R>) ctx.resolveType(fieldType), "Failed to resolve option type for " + fieldType.getRawType());
                    String name = field.getName();
                    {
                        FieldName fieldName = field.getAnnotation(FieldName.class);
                        if (fieldName != null) {
                            name = fieldName.value();
                        }
                    }
                    Option.Builder<R> option = (Option.Builder<R>) optionType.toOption(name);
                    {
                        AllowNulls allowNulls = field.getAnnotation(AllowNulls.class);
                        if (allowNulls != null) {
                            option.nullable(allowNulls.value());
                        }
                    }
                    {
                        Comments comments = field.getAnnotation(Comments.class);
                        if (comments != null) {
                            option.comments(Arrays.stream(comments.value()).map(Comment::value)
                                    .collect(Collectors.joining("\n")));
                        }
                    }
                    {
                        option.defaultValue(() -> {
                            try {
                                Object defaultInstance = constructor.newInstance();
                                return (R) field.get(defaultInstance);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }
                    options.add(Pair.of((Option<Object>) option.build(), field));
                }
            }
            List<Option<?>> optionList = options.stream().map(Pair::getFirst).collect(Collectors.toList());
            return Optional.of(new OptionedMapOptionType(optionList).map(values -> {
                try {
                    R instance = (R) constructor.newInstance();
                    for (Pair<Option<Object>, Field> pair : options) {
                        Object value = values.get(pair.getFirst().getName());
                        if (value != null) {
                            pair.getSecond().set(instance, value);
                        }
                    }
                    return instance;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, instance -> {
                Map<String, Object> values = new HashMap<>();
                try {
                    for (Pair<Option<Object>, Field> pair : options) {
                        values.put(pair.getFirst().getName(), pair.getSecond().get(instance));
                    }
                    return values;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }));
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}

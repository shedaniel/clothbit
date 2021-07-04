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

package me.shedaniel.clothbit.test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import me.shedaniel.clothbit.api.annotations.Comment;
import me.shedaniel.clothbit.api.annotations.FieldName;
import me.shedaniel.clothbit.api.annotations.IgnoreField;
import me.shedaniel.clothbit.api.options.OptionType;
import me.shedaniel.clothbit.api.options.OptionTypesContext;
import me.shedaniel.clothbit.api.options.type.extended.MapOptionType;
import me.shedaniel.clothbit.api.serializers.format.FormatFlag;
import me.shedaniel.clothbit.api.serializers.format.Serializer;

import java.io.Reader;
import java.io.Writer;
import java.util.Map;

public class TestOptions {
    public static void main(String[] args) {
        OptionTypesContext optionTypesContext = OptionTypesContext.defaultContext();
        {
            Serializer<Writer, Reader> serializer = Serializer.gson()
                    .flag(FormatFlag.indent("    "));
            MapOptionType root = new MapOptionType(ImmutableList.of(
                    optionTypesContext.resolveType(Integer.TYPE).toOption("epic")
                            .defaultValue(10)
                            .build(),
                    optionTypesContext.resolveType(Boolean.TYPE).toOption("bruh")
                            .defaultValue(false)
                            .build(),
                    optionTypesContext.resolveType(String.class).toOption("idk")
                            .defaultValue("default")
                            .build(),
                    new MapOptionType(ImmutableList.of(
                            optionTypesContext.resolveType(Boolean.TYPE).toOption("yes")
                                    .defaultValue(false)
                                    .build()
                    )).toOption("thing")
                            .build()
            ));
            Map<String, ?> value = ImmutableMap.of(
                    "epic", 20,
                    "bruh", false,
                    "idk", "default",
                    "thing", ImmutableMap.of(
                            "yes", false
                    )
            );
            
            System.out.println(Serializer.serializeString(serializer, optionTypesContext, root.withValue(value)));
        }
        {
            Serializer<Writer, Reader> serializer = Serializer.gson()
                    .flag(FormatFlag.indent("    "));
            OptionType<Apple> type = optionTypesContext.resolveType(Apple.class);
            System.out.println(Serializer.serializeString(serializer, optionTypesContext, type.withValue(new Apple())));
            JsonElement json = Serializer.serializeTo(Serializer.gsonElement(), optionTypesContext, type.withValue(new Apple()));
            json.getAsJsonObject().addProperty("age", 1234);
            Apple newApple = Serializer.deserializeTo(Serializer.gsonElement(), optionTypesContext, type, json);
            System.out.println("adw");
        }
        {
            String data = "{\n" +
                          "    \"age\": 10,\n" +
                          "    \"maybe\": \"adwad\",\n" +
                          "    \"customer\": {\n" +
                          "        \"yes\": \"adwad\",\n" +
                          "        \"klpadjwdoa\": false\n" +
                          "    },\n" +
                          "    \"plsIgnore\": {\n" +
                          "        \"yes\": \"adwad\",\n" +
                          "        \"klpadjwdoa\": false\n" +
                          "    }\n" +
                          "}";
            Serializer<Writer, Reader> serializer = Serializer.gson()
                    .flag(FormatFlag.indent("    "));
            OptionType<Apple> type = optionTypesContext.resolveType(Apple.class);
            Apple apple = Serializer.deserializeString(serializer, optionTypesContext, type, data);
            System.out.println();
        }
    }
    
    public static class Apple {
        @Comment("Lol")
        public int age = 10;
        public transient double potato = 120.9;
        @FieldName("maybe")
        public String yes = "adwad";
        public Customer customer = new Customer();
        public Customer[] plsIgnore = new Customer[]{new Customer()};
    }
    
    public static class Customer {
        @IgnoreField
        public int age = 10;
        public transient double potato = 120.9;
        public String yes = "adwad";
        public boolean klpadjwdoa = false;
    }
}
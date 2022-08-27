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

package me.shedaniel.clothbit.test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.util.Pair;
import me.shedaniel.clothbit.api.annotations.Comment;
import me.shedaniel.clothbit.api.annotations.FieldName;
import me.shedaniel.clothbit.api.annotations.IgnoreField;
import me.shedaniel.clothbit.api.options.OptionType;
import me.shedaniel.clothbit.api.options.OptionTypesContext;
import me.shedaniel.clothbit.api.options.type.extended.OptionedMapOptionType;
import me.shedaniel.clothbit.api.options.type.simple.AnyOptionType;
import me.shedaniel.clothbit.api.serializers.format.FormatFlag;
import me.shedaniel.clothbit.api.serializers.format.Serializer;
import me.shedaniel.clothbit.api.serializers.reader.ValueReader;
import me.shedaniel.clothbit.api.serializers.writer.ValueWriter;

import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.*;

public class TestOptions {
    public static void main(String[] args) {
        OptionTypesContext ctx = OptionTypesContext.defaultContext();
        {
            Serializer<Writer, Reader> serializer = Serializer.gson()
                    .flag(FormatFlag.indent("    "));
            OptionedMapOptionType root = new OptionedMapOptionType(ImmutableList.of(
                    ctx.resolveType(Integer.TYPE).toOption("epic")
                            .defaultValue(10)
                            .build(),
                    ctx.resolveType(Boolean.TYPE).toOption("bruh")
                            .defaultValue(false)
                            .build(),
                    ctx.resolveType(String.class).toOption("idk")
                            .defaultValue("default")
                            .build(),
                    new OptionedMapOptionType(ImmutableList.of(
                            ctx.resolveType(Boolean.TYPE).toOption("yes")
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
            
            System.out.println("Root Type");
            System.out.println(Serializer.serializeString(serializer, ctx, root.withValue(value)));
            System.out.println();
            System.out.println("Any Type");
            System.out.println(Serializer.serializeString(serializer, ctx, AnyOptionType.instance().withValue(value)));
            System.out.println();
        }
        {
            Serializer<Writer, Reader> serializer = Serializer.gson()
                    .flag(FormatFlag.indent("    "));
            OptionType<Apple> type = ctx.resolveType(Apple.class);
            System.out.println("Apple");
            System.out.println(Serializer.serializeString(serializer, ctx, type.withValue(new Apple())));
            System.out.println();
            JsonElement json = Serializer.serializeTo(Serializer.gsonElement(), ctx, type.withValue(new Apple()));
            json.getAsJsonObject().addProperty("age", 1234);
            Apple newApple = Serializer.deserializeTo(Serializer.gsonElement(), ctx, type, json);
        }
        {
            String data = "{\n" +
                          "    \"age\": 10,\n" +
                          "    \"maybe\": \"adwad\",\n" +
                          "    \"customer\": {\n" +
                          "        \"yes\": \"adwad\",\n" +
                          "        \"klpadjwdoa\": false\n" +
                          "    },\n" +
                          "    \"plsIgnore\": [{\n" +
                          "        \"yes\": \"adwad\",\n" +
                          "        \"klpadjwdoa\": false\n" +
                          "    }]\n" +
                          "}";
            Serializer<Writer, Reader> serializer = Serializer.gson()
                    .flag(FormatFlag.indent("    "));
            OptionType<Apple> type = ctx.resolveType(Apple.class);
            Apple apple = Serializer.deserializeString(serializer, ctx, type, data);
            System.out.println();
        }
        {
            String data = "{\n" +
                          "    \"age\": 10,\n" +
                          "    \"maybe\": \"adwad\",\n" +
                          "    \"customer\": {\n" +
                          "        \"yes\": \"adwad\",\n" +
                          "        \"klpadjwdoa\": false\n" +
                          "    },\n" +
                          "    \"plsIgnore\": [{\n" +
                          "        \"yes\": \"adwad\",\n" +
                          "        \"klpadjwdoa\": false\n" +
                          "    }]\n" +
                          "}";
            JsonElement[] element = new JsonElement[1];
            try (ValueReader reader = Serializer.gson().reader(new StringReader(data), ctx);
                 ValueWriter writer = Serializer.gsonElement().writer(e -> element[0] = e, ctx)) {
                reader.writeTo(writer, ctx);
            }
            System.out.println(element[0]);
        }
        {
            Serializer<Writer, Reader> serializer = Serializer.gson()
                    .flag(FormatFlag.indent("    "));
            OptionType<Pair<Apple, Customer>> type = ctx.resolveType(new TypeToken<Pair<Apple, Customer>>() {}.getType());
            System.out.println("Apple");
            System.out.println(Serializer.serializeString(serializer, ctx, type.withValue(new Pair<>(new Apple(), new Customer()))));
            System.out.println();
            JsonElement json = Serializer.serializeTo(Serializer.gsonElement(), ctx, type.withValue(new Pair<>(new Apple(), new Customer())));
            json.getAsJsonObject().get("left").getAsJsonObject().addProperty("age", 1234);
            Pair<Apple, Customer> newPair = Serializer.deserializeTo(Serializer.gsonElement(), ctx, type, json);
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
        public Set<Customer> customers = new LinkedHashSet<>(Arrays.asList(new Customer(), new Customer()));
    }
    
    public static class Customer {
        @IgnoreField
        public int age = 10;
        public transient double potato = 120.9;
        public String yes = "adwad";
        public boolean klpadjwdoa = false;
    }
}

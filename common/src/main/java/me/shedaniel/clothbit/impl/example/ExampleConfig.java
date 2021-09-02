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

package me.shedaniel.clothbit.impl.example;

import me.shedaniel.clothbit.api.config.ConfigManager;
import me.shedaniel.clothbit.api.options.minecraft.MinecraftOptionTypeAdapters;

import java.util.Arrays;

public class ExampleConfig {
    public static void initialize() {
        ConfigManager<ExampleConfigObject> manager = ConfigManager.builder(ExampleConfigObject.class, "clothbit.example")
                .adapters(MinecraftOptionTypeAdapters.all())
                .build();
        System.out.println(Arrays.toString(manager.get().bool));
    }
}

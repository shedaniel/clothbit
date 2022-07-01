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

import me.shedaniel.clothbit.api.annotations.Config;
import me.shedaniel.clothbit.api.config.formats.GsonConfigFormatResolver;
import net.minecraft.resources.ResourceLocation;

@Config(name = "bruh.json", format = GsonConfigFormatResolver.class)
public class ExampleConfigObject {
    public boolean apkjdwa = false;
    public int age = 10;
    public String thing;
    public boolean[] bool = new boolean[]{false, true};
    public Object[] yes = new Object[]{false, true};
    public ResourceLocation id = new ResourceLocation("ues");
    public Nested nested = new Nested();
    public Nested[] nesteds = new Nested[]{new Nested(), new Nested()};
    
    public static class Nested {
        public boolean apkjdwa = false;
        public int age = 10;
        public String thing;
        public boolean[] bool = new boolean[]{false, true};
        public boolean[][] boolMatrix = new boolean[][]{new boolean[]{false, true}, new boolean[]{false, true}};
        public Object[] yes = new Object[]{false, true};
        public ResourceLocation id = new ResourceLocation("ues");
    }
}

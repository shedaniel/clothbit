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

package me.shedaniel.clothbit.api.options.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.shedaniel.clothbit.api.options.OptionTypeAdapter;
import me.shedaniel.clothbit.api.options.type.adapter.simple.MappingOptionTypeAdapter;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public class MinecraftOptionTypeAdapters {
    public static OptionTypeAdapter[] all() {
        return new OptionTypeAdapter[]{
                location(),
                tag()
        };
    }
    
    public static OptionTypeAdapter location() {
        return new MappingOptionTypeAdapter<>(String.class, ResourceLocation.class, ResourceLocation::new, Objects::toString);
    }
    
    public static OptionTypeAdapter tag() {
        return new MappingOptionTypeAdapter<>(String.class, Tag.class, s -> {
            try {
                return new TagParser(new StringReader(s)).readValue();
            } catch (CommandSyntaxException e) {
                throw new RuntimeException(e);
            }
        }, Objects::toString);
    }
}

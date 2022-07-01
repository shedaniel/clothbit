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

package me.shedaniel.clothbit.api.io;

import me.shedaniel.clothbit.api.options.OptionTypesContext;
import me.shedaniel.clothbit.api.options.OptionType;
import me.shedaniel.clothbit.api.options.OptionValue;
import me.shedaniel.clothbit.api.serializers.format.Serializer;
import me.shedaniel.clothbit.impl.io.WriterReaderConfigFormatImpl;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Path;

public interface ConfigFormat {
    static ConfigFormat adapt(Serializer<Writer, Reader> serializer) {
        return new WriterReaderConfigFormatImpl(serializer);
    }
    
    <T> void writeTo(OptionValue<T> value, Path path, OptionTypesContext ctx);
    
    <T> T readFrom(OptionType<T> type, Path path, OptionTypesContext ctx);
}

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

package me.shedaniel.clothbit.impl.io;

import me.shedaniel.clothbit.api.io.ConfigFormat;
import me.shedaniel.clothbit.api.options.OptionTypesContext;
import me.shedaniel.clothbit.api.options.OptionType;
import me.shedaniel.clothbit.api.options.OptionValue;
import me.shedaniel.clothbit.api.serializers.reader.ValueReader;
import me.shedaniel.clothbit.api.serializers.format.Serializer;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class WriterReaderConfigFormatImpl implements ConfigFormat {
    private final Serializer<Writer, Reader> serializer;
    
    public WriterReaderConfigFormatImpl(Serializer<Writer, Reader> serializer) {
        this.serializer = serializer;
    }
    
    private void ensureParent(Path path) throws IOException {
        Path parent = path.getParent();
        if (parent != null) Files.createDirectories(parent);
    }
    
    @Override
    public <T> void writeTo(OptionValue<T> value, Path path, OptionTypesContext ctx) {
        try {
            ensureParent(path);
            String string = Serializer.serializeString(serializer, ctx, value);
            Files.write(path, string.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (Exception e) {
            new RuntimeException("Failed to save value " + value + " to " + path, e).printStackTrace();
        }
    }
    
    @Override
    public <T> T readFrom(OptionType<T> type, Path path, OptionTypesContext ctx) {
        try {
            ensureParent(path);
            if (Files.notExists(path)) return type.getDefaultValue();
            T value;
            try (ValueReader reader = serializer.reader(Files.newBufferedReader(path), ctx)) {
                value = type.read(reader);
            } catch (Exception e) {
                new RuntimeException("Failed to load config " + type + " from " + path + ", applying default options!", e).printStackTrace();
                value = type.getDefaultValue();
            }
            return value;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

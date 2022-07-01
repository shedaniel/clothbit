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

package me.shedaniel.clothbit.impl.serializers.gson;

import com.google.common.base.MoreObjects;
import com.google.gson.stream.JsonWriter;
import me.shedaniel.clothbit.api.options.Option;
import me.shedaniel.clothbit.api.options.OptionType;
import me.shedaniel.clothbit.api.options.OptionTypesContext;
import me.shedaniel.clothbit.api.serializers.writer.OptionWriter;
import me.shedaniel.clothbit.api.serializers.writer.ValueWriter;
import me.shedaniel.clothbit.api.serializers.format.FormatEncoder;
import me.shedaniel.clothbit.api.serializers.format.FormatFlag;
import me.shedaniel.clothbit.api.serializers.format.flags.IndentFlag;
import me.shedaniel.clothbit.impl.utils.EscapingUtils.Task;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedWriter;
import java.io.Writer;
import java.util.function.Consumer;

import static me.shedaniel.clothbit.impl.utils.EscapingUtils.call;

public class GsonFormatEncoder implements FormatEncoder<Writer> {
    @Override
    public <T> ValueWriter writer(Writer writer, OptionTypesContext ctx, FormatFlag... flags) {
        BufferedWriter bufferedWriter = writer instanceof BufferedWriter ? (BufferedWriter) writer
                : new BufferedWriter(writer);
        JsonWriter jsonWriter = new JsonWriter(bufferedWriter);
        jsonWriter.setLenient(true);
        for (FormatFlag flag : flags) {
            if (flag instanceof IndentFlag) {
                jsonWriter.setIndent(MoreObjects.firstNonNull(((IndentFlag) flag).getIndent(), ""));
            }
        }
        return new JsonValueWriter(jsonWriter, jsonWriter::close);
    }
    
    private static class JsonValueWriter implements ValueWriter {
        private final JsonWriter writer;
        @Nullable
        private Task onClose;
        
        public JsonValueWriter(JsonWriter writer, @Nullable Task onClose) {
            this.writer = writer;
            this.onClose = onClose;
        }
        
        @Override
        public void writeNull() {
            call(this.writer::nullValue);
        }
        
        @Override
        public void writeString(String value) {
            call(() -> this.writer.value(value));
        }
        
        @Override
        public void writeBoolean(boolean value) {
            call(() -> this.writer.value(value));
        }
        
        @Override
        public void writeCharacter(char value) {
            writeInt(value);
        }
        
        @Override
        public void writeNumber(Number value) {
            call(() -> this.writer.value(value));
        }
        
        @Override
        public void writeObject(OptionType<?> baseType, OptionTypesContext ctx, Consumer<OptionWriter<Option<?>>> consumer) {
            call(() -> {
                this.writer.beginObject();
                consumer.accept(option -> {
                    call(() -> this.writer.name(option.getName()));
                    return new JsonValueWriter(this.writer, null);
                });
                this.writer.endObject();
            });
        }
        
        @Override
        public void writeArray(OptionType<?> baseType, OptionTypesContext ctx, Consumer<OptionWriter<OptionType<?>>> consumer) {
            call(() -> {
                this.writer.beginArray();
                Task tmp = this.onClose;
                this.onClose = null;
                consumer.accept(type -> this);
                this.onClose = tmp;
                this.writer.endArray();
            });
        }
        
        @Override
        public void close() {
            if (onClose != null) {
                call(onClose);
            }
        }
    }
}

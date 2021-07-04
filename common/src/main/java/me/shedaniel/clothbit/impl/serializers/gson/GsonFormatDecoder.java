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

package me.shedaniel.clothbit.impl.serializers.gson;

import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import me.shedaniel.clothbit.api.options.OptionTypesContext;
import me.shedaniel.clothbit.api.serializers.ReadType;
import me.shedaniel.clothbit.api.serializers.reader.ValueReader;
import me.shedaniel.clothbit.api.serializers.format.FormatDecoder;
import me.shedaniel.clothbit.api.serializers.format.FormatFlag;
import me.shedaniel.clothbit.impl.utils.EscapingUtils.Task;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

import static me.shedaniel.clothbit.impl.utils.EscapingUtils.call;

public class GsonFormatDecoder implements FormatDecoder<Reader> {
    @Override
    public <T> ValueReader reader(Reader reader, OptionTypesContext ctx, FormatFlag... flags) {
        BufferedReader bufferedReader = reader instanceof BufferedReader ? (BufferedReader) reader
                : new BufferedReader(reader);
        JsonReader jsonReader = new JsonReader(bufferedReader);
        jsonReader.setLenient(true);
        return new JsonValueReader(jsonReader, jsonReader::close);
    }
    
    private static class JsonValueReader implements ValueReader {
        private final JsonReader reader;
        @Nullable
        private final Task onClose;
        
        public JsonValueReader(JsonReader reader, @Nullable Task onClose) {
            this.reader = reader;
            this.onClose = onClose;
        }
        
        @Override
        @Nullable
        public <T> T readNull() {
            call(this.reader::nextNull);
            return null;
        }
        
        @Override
        public String readString() {
            return call(this.reader::nextString);
        }
        
        @Override
        public boolean readBoolean() {
            return call(this.reader::nextBoolean);
        }
        
        @Override
        public char readCharacter() {
            return (char) readInt();
        }
        
        @Override
        public byte readByte() {
            return (byte) readInt();
        }
        
        @Override
        public short readShort() {
            return (short) readInt();
        }
        
        @Override
        public int readInt() {
            return call(this.reader::nextInt);
        }
        
        @Override
        public long readLong() {
            return call(this.reader::nextLong);
        }
        
        @Override
        public float readFloat() {
            return (float) readDouble();
        }
        
        @Override
        public double readDouble() {
            return call(this.reader::nextDouble);
        }
        
        @Override
        public Number readNumber() {
            return call(() -> new BigDecimal(this.reader.nextString()));
        }
        
        @Override
        public void readObject(BiPredicate<String, ValueReader> consumer) {
            call(() -> {
                this.reader.beginObject();
                while (this.reader.peek() != JsonToken.END_OBJECT) {
                    String name = this.reader.nextName();
                    if (!consumer.test(name, new JsonValueReader(this.reader, null))) {
                        Streams.parse(this.reader);
                    }
                }
                this.reader.endObject();
            });
        }
        
        @Override
        public void readArray(Consumer<ValueReader> consumer) {
            call(() -> {
                this.reader.beginArray();
                while (this.reader.peek() != JsonToken.END_ARRAY) {
                    consumer.accept(this);
                }
                this.reader.endArray();
            });
        }
        
        @Override
        public ReadType peek() {
            switch (call(this.reader::peek)) {
                case STRING:
                    return ReadType.STRING;
                case NUMBER:
                    return ReadType.NUMBER;
                case BOOLEAN:
                    return ReadType.BOOLEAN;
                case NULL:
                    return ReadType.NULL;
                case BEGIN_ARRAY:
                    return ReadType.ARRAY;
                case BEGIN_OBJECT:
                    return ReadType.OBJECT;
                case END_ARRAY:
                case END_OBJECT:
                case NAME:
                case END_DOCUMENT:
                default:
                    return ReadType.OTHER;
            }
        }
        
        @Override
        public void close() {
            if (onClose != null) {
                call(onClose);
            }
        }
    }
}

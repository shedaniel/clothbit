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

package me.shedaniel.clothbit.impl.serializers.gson.element;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.shedaniel.clothbit.api.serializers.ReadType;
import me.shedaniel.clothbit.api.serializers.ValueReader;
import me.shedaniel.clothbit.api.serializers.format.FormatDecoder;
import me.shedaniel.clothbit.api.serializers.format.FormatFlag;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

public class GsonElementFormatDecoder implements FormatDecoder<JsonElement> {
    @Override
    public <T> ValueReader reader(JsonElement reader, FormatFlag... flags) {
        return new RootGsonElementValueReader(reader);
    }
    
    private abstract static class GsonElementValueReader implements ValueReader {
        public abstract JsonElement next();
        
        @Nullable
        public abstract JsonElement peekNext();
        
        @Override
        @Nullable
        public <T> T readNull() {
            JsonElement next = next();
            Preconditions.checkArgument(next.isJsonNull(), "Expected null but was " + next);
            return null;
        }
        
        @Override
        public String readString() {
            JsonElement next = next();
            Preconditions.checkArgument(next.isJsonPrimitive() && next.getAsJsonPrimitive().isString(), "Expected string but was " + next);
            return next.getAsJsonPrimitive().getAsString();
        }
        
        @Override
        public boolean readBoolean() {
            JsonElement next = next();
            Preconditions.checkArgument(next.isJsonPrimitive() && next.getAsJsonPrimitive().isBoolean(), "Expected boolean but was " + next);
            return next.getAsJsonPrimitive().getAsBoolean();
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
            JsonElement next = next();
            Preconditions.checkArgument(next.isJsonPrimitive() && next.getAsJsonPrimitive().isNumber(), "Expected number but was " + next);
            return next.getAsJsonPrimitive().getAsInt();
        }
        
        @Override
        public long readLong() {
            JsonElement next = next();
            Preconditions.checkArgument(next.isJsonPrimitive() && next.getAsJsonPrimitive().isNumber(), "Expected number but was " + next);
            return next.getAsJsonPrimitive().getAsLong();
        }
        
        @Override
        public float readFloat() {
            JsonElement next = next();
            Preconditions.checkArgument(next.isJsonPrimitive() && next.getAsJsonPrimitive().isNumber(), "Expected number but was " + next);
            return next.getAsJsonPrimitive().getAsFloat();
        }
        
        @Override
        public double readDouble() {
            JsonElement next = next();
            Preconditions.checkArgument(next.isJsonPrimitive() && next.getAsJsonPrimitive().isNumber(), "Expected number but was " + next);
            return next.getAsJsonPrimitive().getAsDouble();
        }
        
        @Override
        public Number readNumber() {
            JsonElement next = next();
            Preconditions.checkArgument(next.isJsonPrimitive() && next.getAsJsonPrimitive().isNumber(), "Expected number but was " + next);
            return next.getAsJsonPrimitive().getAsNumber();
        }
        
        @Override
        public void readObject(BiPredicate<String, ValueReader> consumer) {
            JsonElement next = next();
            Preconditions.checkArgument(next.isJsonObject(), "Expected object but was " + next);
            for (Map.Entry<String, JsonElement> entry : next.getAsJsonObject().entrySet()) {
                consumer.test(entry.getKey(), new RootGsonElementValueReader(entry.getValue()));
            }
        }
        
        @Override
        public void readArray(Consumer<ValueReader> consumer) {
            JsonElement next = next();
            Preconditions.checkArgument(next.isJsonArray(), "Expected array but was " + next);
            for (JsonElement element : next.getAsJsonArray()) {
                consumer.accept(new RootGsonElementValueReader(element));
            }
        }
        
        @Override
        public ReadType peek() {
            JsonElement next = peekNext();
            if (next.isJsonNull()) return ReadType.NULL;
            if (next.isJsonPrimitive()) {
                JsonPrimitive primitive = next.getAsJsonPrimitive();
                if (primitive.isBoolean()) return ReadType.BOOLEAN;
                if (primitive.isNumber()) return ReadType.NUMBER;
                if (primitive.isString()) return ReadType.STRING;
            }
            if (next.isJsonObject()) return ReadType.OBJECT;
            if (next.isJsonArray()) return ReadType.ARRAY;
            return ReadType.OTHER;
        }
        
        @Override
        public void close() {}
    }
    
    private static class RootGsonElementValueReader extends GsonElementValueReader {
        private JsonElement current;
        
        public RootGsonElementValueReader(JsonElement current) {
            this.current = current;
        }
        
        @Override
        public JsonElement next() {
            return current;
        }
        
        @Override
        @Nullable
        public JsonElement peekNext() {
            return current;
        }
    }
}

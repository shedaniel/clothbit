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

import com.google.gson.*;
import me.shedaniel.clothbit.api.options.Option;
import me.shedaniel.clothbit.api.serializers.OptionWriter;
import me.shedaniel.clothbit.api.serializers.ValueWriter;
import me.shedaniel.clothbit.api.serializers.format.FormatEncoder;
import me.shedaniel.clothbit.api.serializers.format.FormatFlag;

import java.util.function.Consumer;

public class GsonElementFormatEncoder implements FormatEncoder<Consumer<JsonElement>> {
    @Override
    public <T> ValueWriter writer(Consumer<JsonElement> writer, FormatFlag... flags) {
        return new RootGsonElementValueWriter() {
            @Override
            public void close() {
                writer.accept(getCurrent());
            }
        };
    }
    
    private abstract static class GsonElementValueWriter implements ValueWriter {
        protected abstract void accept(JsonElement element);
        
        @Override
        public void writeNull() {
            accept(JsonNull.INSTANCE);
        }
        
        @Override
        public void writeString(String value) {
            accept(new JsonPrimitive(value));
        }
        
        @Override
        public void writeBoolean(boolean value) {
            accept(new JsonPrimitive(value));
        }
        
        @Override
        public void writeCharacter(char value) {
            writeInt(value);
        }
        
        @Override
        public void writeNumber(Number value) {
            accept(new JsonPrimitive(value));
        }
        
        @Override
        public void writeObject(Consumer<OptionWriter<Option<?>>> consumer) {
            JsonObject value = new JsonObject();
            consumer.accept(option -> {
                return new ObjectGsonElementValueWriter(option.getName(), value);
            });
            accept(value);
        }
        
        @Override
        public void writeArray(Consumer<ValueWriter> consumer) {
            JsonArray value = new JsonArray();
            consumer.accept(new ArrayGsonElementValueWriter(value));
            accept(value);
        }
        
        @Override
        public void close() {}
    }
    
    private static class RootGsonElementValueWriter extends GsonElementValueWriter {
        private JsonElement current;
        
        @Override
        protected void accept(JsonElement element) {
            current = element;
        }
        
        public JsonElement getCurrent() {
            return current;
        }
    }
    
    private static class ObjectGsonElementValueWriter extends GsonElementValueWriter {
        private String name;
        private JsonObject current;
        
        public ObjectGsonElementValueWriter(String name, JsonObject current) {
            this.name = name;
            this.current = current;
        }
        
        @Override
        protected void accept(JsonElement element) {
            current.add(name, element);
        }
    }
    
    private static class ArrayGsonElementValueWriter extends GsonElementValueWriter {
        private JsonArray current;
        
        public ArrayGsonElementValueWriter(JsonArray current) {
            this.current = current;
        }
        
        @Override
        protected void accept(JsonElement element) {
            current.add(element);
        }
    }
}

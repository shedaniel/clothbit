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

package me.shedaniel.clothbit.api.serializers;

import me.shedaniel.clothbit.api.options.Option;
import me.shedaniel.clothbit.api.options.OptionType;
import me.shedaniel.clothbit.api.options.OptionTypesContext;
import me.shedaniel.clothbit.api.serializers.reader.ValueReader;
import me.shedaniel.clothbit.api.serializers.writer.NonClosingValueWriter;
import me.shedaniel.clothbit.api.serializers.writer.OptionWriter;
import me.shedaniel.clothbit.api.serializers.writer.ValueWriter;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

public class ValueBuffer implements ValueReader, ValueWriter {
    private Stack<Object> stack = new Stack<>();
    
    public ValueBuffer() {
    }
    
    public ValueBuffer(ValueReader reader, OptionTypesContext ctx) {
        writeFrom(reader, ctx);
    }
    
    @Override
    public void writeTo(ValueWriter writer, OptionTypesContext ctx) {
        writer.writeAny(pop(), ctx);
    }
    
    public Object pop() {
        return stack.pop();
    }
    
    public Object peekObj() {
        return stack.peek();
    }
    
    @Override
    @Nullable
    public <T> T readNull() {
        Object pop = pop();
        if (pop != null) throw new IllegalStateException("Could not read null when the next object is " + pop);
        return null;
    }
    
    @Override
    public String readString() {
        Object pop = pop();
        if (!(pop instanceof String)) throw new IllegalStateException("Could not read string when the next object is " + pop);
        return (String) pop;
    }
    
    @Override
    public boolean readBoolean() {
        Object pop = pop();
        if (!(pop instanceof Boolean)) throw new IllegalStateException("Could not read boolean when the next object is " + pop);
        return (Boolean) pop;
    }
    
    @Override
    public char readCharacter() {
        Object pop = pop();
        if (!(pop instanceof Character)) throw new IllegalStateException("Could not read character when the next object is " + pop);
        return (Character) pop;
    }
    
    @Override
    public byte readByte() {
        Object pop = pop();
        if (!(pop instanceof Byte)) throw new IllegalStateException("Could not read byte when the next object is " + pop);
        return (Byte) pop;
    }
    
    @Override
    public short readShort() {
        Object pop = pop();
        if (!(pop instanceof Short)) throw new IllegalStateException("Could not read short when the next object is " + pop);
        return (Short) pop;
    }
    
    @Override
    public int readInt() {
        Object pop = pop();
        if (!(pop instanceof Integer)) throw new IllegalStateException("Could not read int when the next object is " + pop);
        return (Integer) pop;
    }
    
    @Override
    public long readLong() {
        Object pop = pop();
        if (!(pop instanceof Long)) throw new IllegalStateException("Could not read long when the next object is " + pop);
        return (Long) pop;
    }
    
    @Override
    public float readFloat() {
        Object pop = pop();
        if (!(pop instanceof Float)) throw new IllegalStateException("Could not read float when the next object is " + pop);
        return (Float) pop;
    }
    
    @Override
    public double readDouble() {
        Object pop = pop();
        if (!(pop instanceof Double)) throw new IllegalStateException("Could not read double when the next object is " + pop);
        return (Double) pop;
    }
    
    @Override
    public Number readNumber() {
        Object pop = pop();
        if (!(pop instanceof Number)) throw new IllegalStateException("Could not read number when the next object is " + pop);
        return (Number) pop;
    }
    
    @Override
    public void readObject(BiPredicate<String, ValueReader> consumer) {
        Object pop = pop();
        if (!(pop instanceof Map)) throw new IllegalStateException("Could not read object when the next object is " + pop);
        ValueBuffer buffer = new ValueBuffer();
        Map<String, Object> data = (Map<String, Object>) pop;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            buffer.stack.push(entry.getValue());
            consumer.test(entry.getKey(), buffer);
            buffer.clear();
        }
    }
    
    @Override
    public void readArray(Consumer<ValueReader> consumer) {
        Object pop = pop();
        if (!(pop instanceof List)) throw new IllegalStateException("Could not read array when the next object is " + pop);
        ValueBuffer buffer = new ValueBuffer();
        List<Object> data = (List<Object>) pop;
        for (Object entry : data) {
            buffer.stack.push(entry);
            consumer.accept(buffer);
            buffer.clear();
        }
    }
    
    @Override
    public Object readAny() {
        return pop();
    }
    
    @Override
    public ReadType peek() {
        if (stack.isEmpty()) return ReadType.OTHER;
        Object peek = peekObj();
        if (peek == null) return ReadType.NULL;
        if (peek instanceof Short) return ReadType.SHORT;
        if (peek instanceof Byte) return ReadType.BYTE;
        if (peek instanceof Integer) return ReadType.INT;
        if (peek instanceof Long) return ReadType.LONG;
        if (peek instanceof Float) return ReadType.FLOAT;
        if (peek instanceof Double) return ReadType.DOUBLE;
        if (peek instanceof Character) return ReadType.CHARACTER;
        if (peek instanceof Number) return ReadType.NUMBER;
        if (peek instanceof String) return ReadType.STRING;
        if (peek instanceof Boolean) return ReadType.BOOLEAN;
        if (peek instanceof Map) return ReadType.OBJECT;
        if (peek instanceof List) return ReadType.ARRAY;
        return ReadType.OTHER;
    }
    
    @Override
    public void writeNull() {
        stack.push(null);
    }
    
    @Override
    public void writeString(String value) {
        stack.push(value);
    }
    
    @Override
    public void writeBoolean(boolean value) {
        stack.push(value);
    }
    
    @Override
    public void writeCharacter(char value) {
        stack.push(value);
    }
    
    @Override
    public void writeNumber(Number value) {
        stack.push(value);
    }
    
    @Override
    public void writeObject(Consumer<OptionWriter<Option<?>>> consumer) {
        Map<String, Object> data = new HashMap<>();
        List<String> keys = new ArrayList<>();
        ValueBuffer buffer = new ValueBuffer();
        consumer.accept(option -> {
            keys.add(option.getName());
            return buffer;
        });
        for (String key : keys) {
            data.put(key, buffer.pop());
        }
        buffer.close();
        stack.push(data);
    }
    
    @Override
    public void writeArray(Consumer<OptionWriter<OptionType<?>>> consumer) {
        ValueBuffer buffer = new ValueBuffer();
        NonClosingValueWriter valueWriter = new NonClosingValueWriter(buffer);
        consumer.accept(type -> valueWriter);
        List<Object> data = new ArrayList<>(buffer.stack);
        buffer.close();
        stack.push(data);
    }
    
    @Override
    public void writeAny(Object value, OptionTypesContext ctx) {
        stack.push(copy(value));
    }
    
    @Override
    public void close() {
        this.stack.clear();
    }
    
    public void clear() {
        this.stack.clear();
    }
    
    public ValueBuffer copy() {
        ValueBuffer copy = new ValueBuffer();
        for (Object o : this.stack) {
            copy.stack.add(copy(o));
        }
        return copy;
    }
    
    private Object copy(Object o) {
        if (o instanceof Map) {
            Map<String, Object> newMap = new HashMap<>();
            for (Map.Entry<String, Object> entry : ((Map<String, Object>) o).entrySet()) {
                newMap.put(entry.getKey(), copy(entry.getValue()));
            }
            return newMap;
        }
        if (o instanceof Iterable) {
            List<Object> newList = new ArrayList<>();
            for (Object entry : ((Iterable<?>) o)) {
                newList.add(copy(entry));
            }
            return newList;
        }
        return o;
    }
}

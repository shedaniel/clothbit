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

import com.google.common.base.MoreObjects;
import me.shedaniel.clothbit.api.options.Option;
import me.shedaniel.clothbit.api.options.OptionType;
import me.shedaniel.clothbit.api.options.OptionTypesContext;
import me.shedaniel.clothbit.api.options.type.extended.ArrayOptionType;
import me.shedaniel.clothbit.api.options.type.extended.OptionedMapOptionType;
import me.shedaniel.clothbit.api.options.type.simple.AnyOptionType;
import me.shedaniel.clothbit.api.options.type.simple.BooleanOptionType;
import me.shedaniel.clothbit.api.options.type.simple.CharacterOptionType;
import me.shedaniel.clothbit.api.options.type.simple.StringOptionType;
import me.shedaniel.clothbit.api.options.type.simple.number.*;
import me.shedaniel.clothbit.api.serializers.reader.ValueReader;
import me.shedaniel.clothbit.api.serializers.writer.OptionWriter;
import me.shedaniel.clothbit.api.serializers.writer.ValueWriter;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

public class ValueBuffer implements ValueReader, ValueWriter {
    protected Stack<TypedValue<?>> stack = new Stack<>();
    
    public ValueBuffer() {
    }
    
    public ValueBuffer(ValueReader reader, OptionTypesContext ctx) {
        writeFrom(reader, ctx);
    }
    
    @Override
    public void writeTo(ValueWriter writer, OptionTypesContext ctx) {
        writer.writeAny(readAny(), ctx);
    }
    
    public Object peekObj() {
        TypedValue<?> peek = peekObjTyped();
        return peek == null ? null : peek.getValue();
    }
    
    @Nullable
    public TypedValue<?> peekObjTyped() {
        return stack.peek();
    }
    
    @Override
    @Nullable
    public <T> T readNull() {
        Object pop = readAny();
        if (pop != null) throw new IllegalStateException("Could not read null when the next object is " + pop);
        return null;
    }
    
    @Override
    public String readString() {
        Object pop = readAny();
        if (!(pop instanceof String)) throw new IllegalStateException("Could not read string when the next object is " + pop);
        return (String) pop;
    }
    
    @Override
    public boolean readBoolean() {
        Object pop = readAny();
        if (!(pop instanceof Boolean)) throw new IllegalStateException("Could not read boolean when the next object is " + pop);
        return (Boolean) pop;
    }
    
    @Override
    public char readCharacter() {
        Object pop = readAny();
        if (!(pop instanceof Character)) throw new IllegalStateException("Could not read character when the next object is " + pop);
        return (Character) pop;
    }
    
    @Override
    public byte readByte() {
        Object pop = readAny();
        if (!(pop instanceof Byte)) throw new IllegalStateException("Could not read byte when the next object is " + pop);
        return (Byte) pop;
    }
    
    @Override
    public short readShort() {
        Object pop = readAny();
        if (!(pop instanceof Short)) throw new IllegalStateException("Could not read short when the next object is " + pop);
        return (Short) pop;
    }
    
    @Override
    public int readInt() {
        Object pop = readAny();
        if (!(pop instanceof Integer)) throw new IllegalStateException("Could not read int when the next object is " + pop);
        return (Integer) pop;
    }
    
    @Override
    public long readLong() {
        Object pop = readAny();
        if (!(pop instanceof Long)) throw new IllegalStateException("Could not read long when the next object is " + pop);
        return (Long) pop;
    }
    
    @Override
    public float readFloat() {
        Object pop = readAny();
        if (!(pop instanceof Float)) throw new IllegalStateException("Could not read float when the next object is " + pop);
        return (Float) pop;
    }
    
    @Override
    public double readDouble() {
        Object pop = readAny();
        if (!(pop instanceof Double)) throw new IllegalStateException("Could not read double when the next object is " + pop);
        return (Double) pop;
    }
    
    @Override
    public Number readNumber() {
        Object pop = readAny();
        if (!(pop instanceof Number)) throw new IllegalStateException("Could not read number when the next object is " + pop);
        return (Number) pop;
    }
    
    @Override
    public void readObject(BiPredicate<String, ValueReader> consumer) {
        TypedValue<?> pop = readAnyTyped();
        if (!(pop instanceof TypedMap)) {
            Object popValue = pop == null ? null : pop.getValue();
            throw new IllegalStateException("Could not read object when the next object is " + popValue);
        }
        ValueBuffer buffer = new ValueBuffer();
        TypedMap<Object> data = (TypedMap<Object>) pop;
        for (Map.Entry<String, TypedValue<Object>> entry : data.getValues().entrySet()) {
            buffer.stack.push(entry.getValue());
            consumer.test(entry.getKey(), buffer);
            buffer.clear();
        }
    }
    
    @Override
    public void readArray(Consumer<ValueReader> consumer) {
        TypedValue<?> pop = readAnyTyped();
        if (pop instanceof TypedList) {
            ValueBuffer buffer = new ValueBuffer();
            TypedList<Object> data = (TypedList<Object>) pop;
            for (TypedValue<Object> entry : data.getValues()) {
                buffer.stack.push(entry);
                consumer.accept(buffer);
                buffer.clear();
            }
        } else if (pop instanceof TypedArray) {
            ValueBuffer buffer = new ValueBuffer();
            TypedArray<Object> data = (TypedArray<Object>) pop;
            for (TypedValue<Object> entry : data.getValues()) {
                buffer.stack.push(entry);
                consumer.accept(buffer);
                buffer.clear();
            }
        } else {
            Object popValue = pop == null ? null : pop.getValue();
            throw new IllegalStateException("Could not read array when the next object is " + popValue);
        }
    }
    
    @Override
    public Object readAny() {
        TypedValue<?> pop = readAnyTyped();
        return pop == null ? null : pop.getValue();
    }
    
    @Nullable
    public TypedValue<?> readAnyTyped() {
        return stack.pop();
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
        stack.push(new SingleTypedValue<>(StringOptionType.instance(), value));
    }
    
    @Override
    public void writeBoolean(boolean value) {
        stack.push(new SingleTypedValue<>(BooleanOptionType.primitive(), value));
    }
    
    @Override
    public void writeCharacter(char value) {
        stack.push(new SingleTypedValue<>(CharacterOptionType.primitive(), value));
    }
    
    @Override
    public void writeNumber(Number value) {
        if (value instanceof Byte) {
            stack.push(new SingleTypedValue<>(ByteOptionType.boxed(), (Byte) value));
        } else if (value instanceof Short) {
            stack.push(new SingleTypedValue<>(ShortOptionType.boxed(), (Short) value));
        } else if (value instanceof Integer) {
            stack.push(new SingleTypedValue<>(IntOptionType.boxed(), (Integer) value));
        } else if (value instanceof Long) {
            stack.push(new SingleTypedValue<>(LongOptionType.boxed(), (Long) value));
        } else if (value instanceof Float) {
            stack.push(new SingleTypedValue<>(FloatOptionType.boxed(), (Float) value));
        } else if (value instanceof Double) {
            stack.push(new SingleTypedValue<>(DoubleOptionType.boxed(), (Double) value));
        } else {
            stack.push(new SingleTypedValue<>(AnyOptionType.instance(), value));
        }
    }
    
    @Override
    public void writeByte(byte value) {
        stack.push(new SingleTypedValue<>(ByteOptionType.primitive(), value));
    }
    
    @Override
    public void writeShort(short value) {
        stack.push(new SingleTypedValue<>(ShortOptionType.primitive(), value));
    }
    
    @Override
    public void writeInt(int value) {
        stack.push(new SingleTypedValue<>(IntOptionType.primitive(), value));
    }
    
    @Override
    public void writeLong(long value) {
        stack.push(new SingleTypedValue<>(LongOptionType.primitive(), value));
    }
    
    @Override
    public void writeFloat(float value) {
        stack.push(new SingleTypedValue<>(FloatOptionType.primitive(), value));
    }
    
    @Override
    public void writeDouble(double value) {
        stack.push(new SingleTypedValue<>(DoubleOptionType.primitive(), value));
    }
    
    @Override
    public void writeObject(OptionType<?> baseType, OptionTypesContext ctx, Consumer<OptionWriter<Option<?>>> consumer) {
        Map<String, TypedValue<Object>> data = new LinkedHashMap<>();
        List<Option<Object>> options = new ArrayList<>();
        ValueBuffer buffer = new ValueBuffer();
        consumer.accept(option -> {
            options.add((Option<Object>) option);
            return new ValueBuffer() {
                @Override
                public void close() {
                    buffer.stack.addAll(0, this.stack);
                    super.close();
                }
            };
        });
        // reverse buffer.stack
        for (Option<Object> option : options) {
            data.put(option.getName(), (TypedValue<Object>) copy(option.getType(), buffer.readAny(), ctx));
        }
        buffer.close();
        stack.push(new TypedMap<>(new OptionedMapOptionType<>(options), data));
    }
    
    @Override
    public void writeArray(OptionType<?> baseType, OptionTypesContext ctx, Consumer<OptionWriter<OptionType<?>>> consumer) {
        List<TypedValue<Object>> data = new ArrayList<>();
        consumer.accept(type -> {
            return new ValueBuffer() {
                @Override
                public void close() {
                    data.add((TypedValue<Object>) this.readAnyTyped());
                    super.close();
                }
            };
        });
        stack.push(new TypedList<>(AnyOptionType.instance(), data));
    }
    
    @Override
    public void writeAny(Object value, OptionTypesContext ctx) {
        stack.push(copy(value, ctx));
    }
    
    @Override
    public void close() {
        this.stack.clear();
    }
    
    public void clear() {
        this.stack.clear();
    }
    
    public ValueBuffer copy(OptionTypesContext ctx) {
        ValueBuffer copy = new ValueBuffer();
        for (TypedValue<?> o : this.stack) {
            copy.stack.add(copy(o, ctx));
        }
        return copy;
    }
    
    private TypedValue<?> copy(Object o, OptionTypesContext ctx) {
        return copy(null, o, ctx);
    }
    
    private TypedValue<?> copy(@Nullable OptionType<?> type, Object o, OptionTypesContext ctx) {
        if (o == null) return null;
        if (o instanceof Map) {
            Map<String, TypedValue<Object>> newMap = new LinkedHashMap<>();
            for (Map.Entry<String, Object> entry : ((Map<String, Object>) o).entrySet()) {
                newMap.put(entry.getKey(), (TypedValue<Object>) copy(entry.getValue(), ctx));
            }
            return new TypedMap<>(MoreObjects.firstNonNull(type, AnyOptionType.instance()).cast(), newMap);
        }
        if (o instanceof TypedMap) {
            Map<String, TypedValue<Object>> newMap = new LinkedHashMap<>();
            for (Map.Entry<String, TypedValue<Object>> entry : ((TypedMap<Object>) o).getValues().entrySet()) {
                newMap.put(entry.getKey(), (TypedValue<Object>) copy(entry.getValue(), ctx));
            }
            return new TypedMap<>(MoreObjects.firstNonNull(type, ((TypedMap<Object>) o).getType()).cast(), newMap);
        }
        if (o instanceof Iterable) {
            List<TypedValue<Object>> newList = new ArrayList<>();
            for (Object entry : ((Iterable<?>) o)) {
                newList.add((TypedValue<Object>) copy(entry, ctx));
            }
            return new TypedList<>(MoreObjects.firstNonNull(type, AnyOptionType.instance()).cast(), newList);
        }
        if (o instanceof TypedList) {
            List<TypedValue<Object>> newList = new ArrayList<>();
            for (TypedValue<Object> entry : ((TypedList<Object>) o).getValues()) {
                newList.add((TypedValue<Object>) copy(entry, ctx));
            }
            return new TypedList<>(MoreObjects.firstNonNull(type, ((TypedList<Object>) o).getType()).cast(), newList);
        }
        if (o instanceof TypedArray) {
            List<TypedValue<Object>> newList = new ArrayList<>();
            for (TypedValue<Object> entry : ((TypedArray<Object>) o).getValues()) {
                newList.add((TypedValue<Object>) copy(entry, ctx));
            }
            return new TypedArray<>(MoreObjects.firstNonNull(type, ((TypedArray<Object>) o).getType()).cast(), newList);
        }
        if (o.getClass().isArray()) {
            List<TypedValue<Object>> newList = new ArrayList<>();
            for (int i = 0; i < Array.getLength(o); i++) {
                newList.add((TypedValue<Object>) copy(Array.get(o, i), ctx));
            }
            Class<Object> componentType = (Class<Object>) o.getClass().getComponentType();
            OptionType<Object> newType = MoreObjects.firstNonNull(type, ctx.resolveType(componentType)).cast();
            return new TypedArray<>(new ArrayOptionType<>(newType, componentType), newList);
        }
        if (o instanceof TypedValue) {
            return new SingleTypedValue<>(((TypedValue<Object>) o).getType(), copy(((TypedValue<Object>) o).getValue(), ctx).getValue());
        }
        OptionType<Object> newType = MoreObjects.firstNonNull(type, ctx.resolveType((Class<Object>) o.getClass())).cast();
        return new SingleTypedValue<>(newType, o);
    }
}

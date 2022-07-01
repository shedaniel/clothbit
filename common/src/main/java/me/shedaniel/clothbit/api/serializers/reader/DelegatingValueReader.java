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

package me.shedaniel.clothbit.api.serializers.reader;

import me.shedaniel.clothbit.api.serializers.ReadType;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

public abstract class DelegatingValueReader implements ValueReader {
    protected final ValueReader parent;
    
    public DelegatingValueReader(ValueReader parent) {
        this.parent = Objects.requireNonNull(parent);
    }
    
    @Override
    @Nullable
    public <T> T readNull() {
        return this.parent.readNull();
    }
    
    @Override
    public String readString() {
        return this.parent.readString();
    }
    
    @Override
    public boolean readBoolean() {
        return this.parent.readBoolean();
    }
    
    @Override
    public char readCharacter() {
        return this.parent.readCharacter();
    }
    
    @Override
    public byte readByte() {
        return this.parent.readByte();
    }
    
    @Override
    public short readShort() {
        return this.parent.readShort();
    }
    
    @Override
    public int readInt() {
        return this.parent.readInt();
    }
    
    @Override
    public long readLong() {
        return this.parent.readLong();
    }
    
    @Override
    public float readFloat() {
        return this.parent.readFloat();
    }
    
    @Override
    public double readDouble() {
        return this.parent.readDouble();
    }
    
    @Override
    public Number readNumber() {
        return this.parent.readNumber();
    }
    
    @Override
    public void readObject(BiPredicate<String, ValueReader> consumer) {
        this.parent.readObject(consumer);
    }
    
    @Override
    public void readArray(Consumer<ValueReader> consumer) {
        this.parent.readArray(consumer);
    }
    
    @Override
    public ReadType peek() {
        return this.parent.peek();
    }
    
    @Override
    public void close() {
        this.parent.close();
    }
}

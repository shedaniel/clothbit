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

package me.shedaniel.clothbit.api.serializers.writer;

import me.shedaniel.clothbit.api.options.Option;
import me.shedaniel.clothbit.api.options.OptionType;
import me.shedaniel.clothbit.api.options.OptionTypesContext;

import java.util.Objects;
import java.util.function.Consumer;

public abstract class DelegatingValueWriter implements ValueWriter {
    protected final ValueWriter parent;
    
    public DelegatingValueWriter(ValueWriter parent) {
        this.parent = Objects.requireNonNull(parent);
    }
    
    @Override
    public void writeNull() {
        this.parent.writeNull();
    }
    
    @Override
    public void writeString(String value) {
        this.parent.writeString(value);
    }
    
    @Override
    public void writeBoolean(boolean value) {
        this.parent.writeBoolean(value);
    }
    
    @Override
    public void writeCharacter(char value) {
        this.parent.writeCharacter(value);
    }
    
    @Override
    public void writeNumber(Number value) {
        this.parent.writeNumber(value);
    }
    
    @Override
    public void writeByte(byte value) {
        this.parent.writeByte(value);
    }
    
    @Override
    public void writeShort(short value) {
        this.parent.writeShort(value);
    }
    
    @Override
    public void writeInt(int value) {
        this.parent.writeInt(value);
    }
    
    @Override
    public void writeLong(long value) {
        this.parent.writeLong(value);
    }
    
    @Override
    public void writeFloat(float value) {
        this.parent.writeFloat(value);
    }
    
    @Override
    public void writeDouble(double value) {
        this.parent.writeDouble(value);
    }
    
    @Override
    public void writeObject(OptionType<?> baseType, OptionTypesContext ctx, Consumer<OptionWriter<Option<?>>> consumer) {
        this.parent.writeObject(baseType, ctx, consumer);
    }
    
    @Override
    public void writeArray(OptionType<?> baseType, OptionTypesContext ctx, Consumer<OptionWriter<OptionType<?>>> consumer) {
        this.parent.writeArray(baseType, ctx, consumer);
    }
    
    @Override
    public void close() {
        this.parent.close();
    }
}

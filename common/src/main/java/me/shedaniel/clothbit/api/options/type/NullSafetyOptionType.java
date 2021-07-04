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

package me.shedaniel.clothbit.api.options.type;

import me.shedaniel.clothbit.api.options.OptionType;
import me.shedaniel.clothbit.api.options.OptionTypesContext;
import me.shedaniel.clothbit.api.serializers.reader.ValueReader;
import me.shedaniel.clothbit.api.serializers.writer.ValueWriter;

public class NullSafetyOptionType<T> extends DelegatingOptionType<T> {
    public NullSafetyOptionType(OptionType<T> parent) {
        super(parent);
    }
    
    @Override
    public void write(T value, ValueWriter writer, OptionTypesContext ctx) {
        if (value == null) {
            if (!isNullable()) {
                throw new NullPointerException("Option Type " + parent + " is not nullable but a nullable value is received!");
            }
        }
        super.write(value, writer, ctx);
    }
    
    @Override
    public T read(ValueReader reader) {
        T value = super.read(reader);
        if (value == null) {
            if (!isNullable()) {
                throw new NullPointerException("Option Type " + parent + " is not nullable but a nullable value is read!");
            }
        }
        return value;
    }
    
    @Override
    public String toString() {
        return "NullSafetyOptionType[" + parent + "]";
    }
}

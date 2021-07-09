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

package me.shedaniel.clothbit.api.options.type.extended;

import me.shedaniel.clothbit.api.options.OptionType;
import me.shedaniel.clothbit.api.options.OptionTypesContext;
import me.shedaniel.clothbit.api.serializers.reader.ValueReader;
import me.shedaniel.clothbit.api.serializers.writer.ValueWriter;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CollectionOptionType<T> implements OptionType<Collection<T>> {
    private OptionType<T> parent;
    
    public CollectionOptionType(OptionType<T> parent) {
        this.parent = parent;
    }
    
    @Override
    public void write(Collection<T> value, ValueWriter writer, OptionTypesContext ctx) {
        if (value == null) {
            writer.writeNull();
        } else {
            writer.writeArray(arrayWriter -> {
                for (T child : value) {
                    this.parent.withValue(child).writeWithType(arrayWriter, ctx);
                }
            });
        }
    }
    
    @Override
    public Collection<T> read(ValueReader reader) {
        List<T> values = new ArrayList<>();
        reader.readArray(arrayReader -> {
            values.add(this.parent.read(arrayReader));
        });
        return values;
    }
    
    @Override
    public Collection<T> copy(Collection<T> values, OptionTypesContext ctx) {
        List<T> newValues = new ArrayList<>();
        for (T value : values) {
            newValues.add(this.parent.copy(value, ctx));
        }
        return newValues;
    }
    
    @Override
    public boolean isNullable() {
        return true;
    }
    
    @Override
    @Nullable
    public Collection<T> getDefaultValue() {
        return new ArrayList<>();
    }
}

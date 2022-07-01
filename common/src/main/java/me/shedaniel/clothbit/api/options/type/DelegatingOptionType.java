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

package me.shedaniel.clothbit.api.options.type;

import me.shedaniel.clothbit.api.options.OptionType;
import me.shedaniel.clothbit.api.options.OptionTypesContext;
import me.shedaniel.clothbit.api.serializers.reader.ValueReader;
import me.shedaniel.clothbit.api.serializers.writer.ValueWriter;
import org.jetbrains.annotations.Nullable;

public abstract class DelegatingOptionType<T> implements OptionType<T> {
    protected final OptionType<T> parent;
    
    protected DelegatingOptionType(OptionType<T> parent) {
        this.parent = parent;
    }
    
    @Override
    public void write(T value, ValueWriter writer, OptionTypesContext ctx) {
        this.parent.write(value, writer, ctx);
    }
    
    @Override
    public T read(ValueReader reader) {
        return this.parent.read(reader);
    }
    
    @Override
    public boolean isNullable() {
        return this.parent.isNullable();
    }
    
    @Override
    @Nullable
    public T getDefaultValue() {
        return this.parent.getDefaultValue();
    }
}

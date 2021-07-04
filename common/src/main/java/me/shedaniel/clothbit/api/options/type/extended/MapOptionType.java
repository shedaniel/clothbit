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

import me.shedaniel.clothbit.api.options.Option;
import me.shedaniel.clothbit.api.options.OptionType;
import me.shedaniel.clothbit.api.options.OptionTypesContext;
import me.shedaniel.clothbit.api.serializers.reader.ValueReader;
import me.shedaniel.clothbit.api.serializers.writer.ValueWriter;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapOptionType implements OptionType<Map<String, ?>> {
    private List<Option<?>> options;
    private Map<String, Option<?>> optionsByName;
    
    public MapOptionType(List<Option<?>> options) {
        this.options = options;
        this.optionsByName = new HashMap<>();
        for (Option<?> option : this.options) {
            this.optionsByName.put(option.getName(), option);
        }
    }
    
    @Override
    public void write(Map<String, ?> value, ValueWriter writer, OptionTypesContext ctx) {
        writer.writeObject(objectWriter -> {
            for (Option<Object> child : (List<Option<Object>>) (List<? extends Option<?>>) this.options) {
                child.withValue(((Map<String, Object>) value).getOrDefault(child.getName(), child.getDefaultValue()))
                        .write(objectWriter, ctx);
            }
        });
    }
    
    @Override
    public Map<String, ?> read(ValueReader reader) {
        Map<String, Object> value = new HashMap<>();
        reader.readObject((key, objectReader) -> {
            Option<?> option = optionsByName.get(key);
            if (option != null) {
                value.put(key, option.getType().read(objectReader));
                return true;
            }
            return false;
        });
        for (Map.Entry<String, Option<?>> entry : optionsByName.entrySet()) {
            if (!value.containsKey(entry.getKey())) {
                value.put(entry.getKey(), entry.getValue().getDefaultValue());
            }
        }
        return value;
    }
    
    @Override
    public Map<String, ?> copy(Map<String, ?> value, OptionTypesContext ctx) {
        Map<String, Object> newValue = new HashMap<>();
        for (Map.Entry<String, ?> entry : value.entrySet()) {
            Option<?> option = optionsByName.get(entry.getKey());
            if (option != null) {
                newValue.put(entry.getKey(), ((OptionType<Object>) option.getType()).copy(entry.getValue(), ctx));
            } else {
                newValue.put(entry.getKey(), entry.getValue());
            }
        }
        return newValue;
    }
    
    @Override
    public boolean isNullable() {
        return true;
    }
    
    @Override
    @Nullable
    public Map<String, ?> getDefaultValue() {
        return new HashMap<>();
    }
}

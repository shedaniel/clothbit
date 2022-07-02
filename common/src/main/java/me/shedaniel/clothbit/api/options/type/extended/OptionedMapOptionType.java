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

package me.shedaniel.clothbit.api.options.type.extended;

import me.shedaniel.clothbit.api.options.Option;
import me.shedaniel.clothbit.api.options.OptionType;
import me.shedaniel.clothbit.api.options.OptionTypesContext;
import me.shedaniel.clothbit.api.options.type.simple.AnyOptionType;
import me.shedaniel.clothbit.api.serializers.reader.ValueReader;
import me.shedaniel.clothbit.api.serializers.writer.ValueWriter;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OptionedMapOptionType implements OptionType<Map<String, ?>> {
    private List<Option<?>> options;
    private Map<String, Option<?>> optionsByName;
    
    public OptionedMapOptionType(List<Option<?>> options) {
        this.options = options;
        this.optionsByName = new LinkedHashMap<>();
        for (Option<?> option : this.options) {
            this.optionsByName.put(option.getName(), option);
        }
    }
    
    @Override
    public void write(Map<String, ?> value, ValueWriter writer, OptionTypesContext ctx) {
        writer.writeObject(AnyOptionType.instance(), ctx, objectWriter -> {
            for (Option<Object> child : (List<Option<Object>>) (List<? extends Option<?>>) this.options) {
                child.withValue(((Map<String, Object>) value).getOrDefault(child.getName(), child.getDefaultValue()))
                        .write(objectWriter, ctx);
            }
        });
    }
    
    @Override
    public Map<String, ?> read(ValueReader reader) {
        Map<String, Object> value = new LinkedHashMap<>();
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
    public boolean isNullable() {
        return true;
    }
    
    @Override
    @Nullable
    public Map<String, ?> getDefaultValue() {
        return new LinkedHashMap<>();
    }
}

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

package me.shedaniel.clothbit.impl.serializers;

import me.shedaniel.clothbit.api.options.Option;
import me.shedaniel.clothbit.api.options.OptionType;
import me.shedaniel.clothbit.api.options.OptionTypesContext;
import me.shedaniel.clothbit.api.options.property.NullableOptionProperty;
import me.shedaniel.clothbit.api.serializers.format.FormatEncoder;
import me.shedaniel.clothbit.api.serializers.format.FormatFlag;
import me.shedaniel.clothbit.api.serializers.writer.DelegatingValueWriter;
import me.shedaniel.clothbit.api.serializers.writer.OptionWriter;
import me.shedaniel.clothbit.api.serializers.writer.ValueWriter;

import java.util.Objects;
import java.util.function.Consumer;

public class NullSafetyFormatEncoder<W> implements FormatEncoder<W> {
    private final FormatEncoder<W> parent;
    
    public NullSafetyFormatEncoder(FormatEncoder<W> parent) {
        this.parent = Objects.requireNonNull(parent);
    }
    
    @Override
    public <T> ValueWriter writer(W writer, OptionTypesContext ctx, FormatFlag... flags) {
        return new DelegatingValueWriter(this.parent.writer(writer, ctx, flags)) {
            @Override
            public void writeObject(OptionType<?> baseType, OptionTypesContext ctx, Consumer<OptionWriter<Option<?>>> consumer) {
                super.writeObject(baseType, ctx, writer -> {
                    consumer.accept(option -> {
                        ValueWriter valueWriter = writer.forOption(option);
                        NullableOptionProperty.HandleMode nullable = option.getNullable();
                        if (nullable == NullableOptionProperty.HandleMode.ALLOW_NULL) {
                            return valueWriter;
                        } else {
                            return new NotNullValueWriter(option, ctx, valueWriter, nullable);
                        }
                    });
                });
            }
        };
    }
    
    private static class NotNullValueWriter extends DelegatingValueWriter {
        private final Option<?> option;
        private final OptionTypesContext ctx;
        private final NullableOptionProperty.HandleMode nullable;
        
        public NotNullValueWriter(Option<?> option, OptionTypesContext ctx, ValueWriter parent, NullableOptionProperty.HandleMode nullable) {
            super(parent);
            this.option = option;
            this.ctx = ctx;
            this.nullable = nullable;
        }
        
        @Override
        public void writeNull() {
            if (nullable == NullableOptionProperty.HandleMode.FILL_DEFAULT) {
                option.withDefaultValue().write(this.parent, ctx);
            } else {
                throw new NullPointerException("Null not accepted for option " + option + ", perhaps you forgot to add @AcceptNulls?");
            }
        }
    }
}

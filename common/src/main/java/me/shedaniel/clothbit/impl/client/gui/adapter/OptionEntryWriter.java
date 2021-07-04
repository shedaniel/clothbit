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

package me.shedaniel.clothbit.impl.client.gui.adapter;

import me.shedaniel.clothbit.api.options.Option;
import me.shedaniel.clothbit.api.options.OptionTypesContext;
import me.shedaniel.clothbit.api.serializers.writer.OptionWriter;
import me.shedaniel.clothbit.api.serializers.writer.RootValueWriter;
import me.shedaniel.clothbit.api.serializers.writer.ValueWriter;
import me.shedaniel.clothbit.impl.client.gui.entry.BaseOptionEntry;
import me.shedaniel.clothbit.impl.client.gui.entry.component.value.TextFieldEntryComponent;
import me.shedaniel.clothbit.impl.client.gui.widgets.ListWidget;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class OptionEntryWriter implements OptionWriter<Option<?>> {
    private final String id;
    private final Consumer<ListWidget.Entry<?>> consumer;
    private final OptionTypesContext ctx;
    private final Option<?>[] parents;
    
    public OptionEntryWriter(String id, Consumer<ListWidget.Entry<?>> consumer, OptionTypesContext ctx, Option<?>[] parents) {
        this.id = id;
        this.consumer = consumer;
        this.ctx = ctx;
        this.parents = parents;
    }
    
    @Override
    public ValueWriter forOption(Option<?> option) {
        return new OptionValueWriter<>(id, consumer, ctx, option, parents);
    }
    
    public static class RootOptionValueWriter implements RootValueWriter {
        private final String id;
        private final Consumer<ListWidget.Entry<?>> consumer;
        private final OptionTypesContext ctx;
        
        public RootOptionValueWriter(String id, Consumer<ListWidget.Entry<?>> consumer, OptionTypesContext ctx) {
            this.id = id;
            this.consumer = consumer;
            this.ctx = ctx;
        }
        
        @Override
        public void writeObject(Consumer<OptionWriter<Option<?>>> consumer) {
            consumer.accept(new OptionEntryWriter(this.id, this.consumer, this.ctx, new Option[0]));
        }
        
        @Override
        public void close() {
            
        }
    }
    
    public static class OptionValueWriter<T> implements ValueWriter {
        private final String id;
        private final Consumer<ListWidget.Entry<?>> consumer;
        private final OptionTypesContext ctx;
        private final Option<T> option;
        private final Option<?>[] parents;
        
        public OptionValueWriter(String id, Consumer<ListWidget.Entry<?>> consumer, OptionTypesContext ctx, Option<T> option, Option<?>[] parents) {
            this.id = id;
            this.consumer = consumer;
            this.ctx = ctx;
            this.option = option;
            this.parents = parents;
        }
        
        public <R> void entry(R value, Consumer<BaseOptionEntry<R>> action) {
            BaseOptionEntry<R> entry = new BaseOptionEntry<>(id, (Option<R>) option, value, ctx, parents);
            action.accept(entry);
            consumer.accept(entry);
        }
        
        @Override
        public void writeNull() {
            writeString(null);
        }
        
        public <R> void primitiveEntry(R value, Consumer<BaseOptionEntry<R>> action) {
            entry(value, entry -> {
                entry.addFieldName();
                entry.addResetButton();
                action.accept(entry);
            });
        }
        
        @Override
        public void writeString(@Nullable String value) {
            primitiveEntry(value, entry -> {
                entry.addComponent(new TextFieldEntryComponent<>(entry, Function.identity(), Function.identity()));
            });
        }
        
        @Override
        public void writeBoolean(boolean value) {
            primitiveEntry(value, entry -> {
                entry.addComponent(new TextFieldEntryComponent<>(entry, String::valueOf, str -> {
                    if (str.equalsIgnoreCase("true")) return true;
                    if (str.equalsIgnoreCase("false")) return false;
                    throw new IllegalArgumentException("Invalid boolean: " + str);
                }));
            });
        }
        
        @Override
        public void writeCharacter(char value) {
            primitiveEntry(value, entry -> {});
        }
        
        @Override
        public void writeNumber(Number value) {
            primitiveEntry(value, entry -> {
                entry.addComponent(new TextFieldEntryComponent<>(entry, String::valueOf, BigDecimal::new));
            });
        }
        
        @Override
        public void writeByte(byte value) {
            primitiveEntry(value, entry -> {
                entry.addComponent(new TextFieldEntryComponent<>(entry, String::valueOf, Byte::valueOf));
            });
        }
        
        @Override
        public void writeShort(short value) {
            primitiveEntry(value, entry -> {
                entry.addComponent(new TextFieldEntryComponent<>(entry, String::valueOf, Short::valueOf));
            });
        }
        
        @Override
        public void writeInt(int value) {
            primitiveEntry(value, entry -> {
                entry.addComponent(new TextFieldEntryComponent<>(entry, String::valueOf, Integer::valueOf));
            });
        }
        
        @Override
        public void writeLong(long value) {
            primitiveEntry(value, entry -> {
                entry.addComponent(new TextFieldEntryComponent<>(entry, String::valueOf, Long::valueOf));
            });
        }
        
        @Override
        public void writeFloat(float value) {
            primitiveEntry(value, entry -> {
                entry.addComponent(new TextFieldEntryComponent<>(entry, String::valueOf, Float::valueOf));
            });
        }
        
        @Override
        public void writeDouble(double value) {
            primitiveEntry(value, entry -> {
                entry.addComponent(new TextFieldEntryComponent<>(entry, String::valueOf, Double::valueOf));
            });
        }
        
        @Override
        public void writeObject(Consumer<OptionWriter<Option<?>>> consumer) {
            List<Option<?>> parents = new ArrayList<>();
            Collections.addAll(parents, this.parents);
            parents.add(option);
            // TODO change consumer to a sub category
            consumer.accept(new OptionEntryWriter(this.id, this.consumer, this.ctx, parents.toArray(new Option[0])));
        }
        
        @Override
        public void writeArray(Consumer<ValueWriter> consumer) {
            
        }
        
        @Override
        public void close() {
            
        }
    }
}

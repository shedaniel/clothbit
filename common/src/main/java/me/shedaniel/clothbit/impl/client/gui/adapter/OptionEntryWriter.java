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

import com.mojang.datafixers.util.Pair;
import me.shedaniel.clothbit.api.options.Option;
import me.shedaniel.clothbit.api.options.OptionType;
import me.shedaniel.clothbit.api.options.OptionTypesContext;
import me.shedaniel.clothbit.api.options.type.simple.AnyOptionType;
import me.shedaniel.clothbit.api.serializers.ValueBuffer;
import me.shedaniel.clothbit.api.serializers.writer.NothingValueWriter;
import me.shedaniel.clothbit.api.serializers.writer.OptionWriter;
import me.shedaniel.clothbit.api.serializers.writer.RootValueWriter;
import me.shedaniel.clothbit.api.serializers.writer.ValueWriter;
import me.shedaniel.clothbit.impl.client.gui.entry.BaseOptionEntry;
import me.shedaniel.clothbit.impl.client.gui.entry.component.AddIconComponent;
import me.shedaniel.clothbit.impl.client.gui.entry.component.DeleteIconComponent;
import me.shedaniel.clothbit.impl.client.gui.entry.component.value.ArrayValueEntryComponent;
import me.shedaniel.clothbit.impl.client.gui.entry.component.value.TextFieldValueEntryComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class OptionEntryWriter implements OptionWriter<Option<?>> {
    private final String id;
    private final Consumer<BaseOptionEntry<?>> consumer;
    private final OptionTypesContext ctx;
    private final Option<?>[] parents;
    
    public OptionEntryWriter(String id, Consumer<BaseOptionEntry<?>> consumer, OptionTypesContext ctx, Option<?>[] parents) {
        this.id = id;
        this.consumer = consumer;
        this.ctx = ctx;
        this.parents = parents;
    }
    
    @Override
    public ValueWriter forOption(Option<?> option) {
        return new OptionValueWriter(id, consumer, ctx,
                option, option.getType(), parents);
    }
    
    public static class RootOptionValueWriter implements RootValueWriter {
        private final String id;
        private final Consumer<BaseOptionEntry<?>> consumer;
        private final OptionTypesContext ctx;
        
        public RootOptionValueWriter(String id, Consumer<BaseOptionEntry<?>> consumer, OptionTypesContext ctx) {
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
        private final Consumer<BaseOptionEntry<T>> consumer;
        private final OptionTypesContext ctx;
        @Nullable
        public final Option<T> option;
        public final OptionType<T> type;
        private final Option<?>[] parents;
        
        public OptionValueWriter(String id, Consumer<BaseOptionEntry<T>> consumer, OptionTypesContext ctx, @Nullable Option<T> option,
                OptionType<T> type, Option<?>[] parents) {
            this.id = id;
            this.consumer = consumer;
            this.ctx = ctx;
            this.option = option;
            this.type = type;
            this.parents = parents;
        }
        
        public <R> void entry(R value, Consumer<BaseOptionEntry<R>> action) {
            BaseOptionEntry<R> entry = new BaseOptionEntry<>(id, option, (OptionType<R>) type, value, ctx, parents);
            action.accept(entry);
            consumer.accept((BaseOptionEntry<T>) entry);
        }
        
        public boolean isRoot() {
            return this.option != null;
        }
        
        @Override
        public void writeNull() {
            writeString(null);
        }
        
        public <R> void primitiveEntry(R value, Consumer<BaseOptionEntry<R>> action) {
            entry(value, entry -> {
                if (isRoot()) {
                    entry.addSandwich();
                    entry.addFieldName((Option<R>) this.option);
                }
                entry.addResetButton();
                action.accept(entry);
            });
        }
        
        @Override
        public void writeString(@Nullable String value) {
            primitiveEntry(value, entry -> {
                entry.addComponent(new TextFieldValueEntryComponent<>(entry, Objects::toString,
                        str -> option.isNullable() && str.equals("null") ? null : str));
            });
        }
        
        @Override
        public void writeBoolean(boolean value) {
            primitiveEntry(value, entry -> {
                entry.addComponent(new TextFieldValueEntryComponent<>(entry, String::valueOf, str -> {
                    if (str.equalsIgnoreCase("true")) return true;
                    if (str.equalsIgnoreCase("false")) return false;
                    throw new IllegalArgumentException("Invalid boolean: " + str);
                }));
            });
        }
        
        @Override
        public void writeCharacter(char value) {
            primitiveEntry(value, entry -> {
                entry.addComponent(new TextFieldValueEntryComponent<>(entry, String::valueOf, s -> s.charAt(0)));
            });
        }
        
        @Override
        public void writeNumber(Number value) {
            primitiveEntry(value, entry -> {
                entry.addComponent(new TextFieldValueEntryComponent<>(entry, String::valueOf, BigDecimal::new));
            });
        }
        
        @Override
        public void writeByte(byte value) {
            primitiveEntry(value, entry -> {
                entry.addComponent(new TextFieldValueEntryComponent<>(entry, String::valueOf, Byte::valueOf));
            });
        }
        
        @Override
        public void writeShort(short value) {
            primitiveEntry(value, entry -> {
                entry.addComponent(new TextFieldValueEntryComponent<>(entry, String::valueOf, Short::valueOf));
            });
        }
        
        @Override
        public void writeInt(int value) {
            primitiveEntry(value, entry -> {
                entry.addComponent(new TextFieldValueEntryComponent<>(entry, String::valueOf, Integer::valueOf));
            });
        }
        
        @Override
        public void writeLong(long value) {
            primitiveEntry(value, entry -> {
                entry.addComponent(new TextFieldValueEntryComponent<>(entry, String::valueOf, Long::valueOf));
            });
        }
        
        @Override
        public void writeFloat(float value) {
            primitiveEntry(value, entry -> {
                entry.addComponent(new TextFieldValueEntryComponent<>(entry, String::valueOf, Float::valueOf));
            });
        }
        
        @Override
        public void writeDouble(double value) {
            primitiveEntry(value, entry -> {
                entry.addComponent(new TextFieldValueEntryComponent<>(entry, String::valueOf, Double::valueOf));
            });
        }
        
        @Override
        public void writeObject(Consumer<OptionWriter<Option<?>>> consumer) {
            List<Option<?>> parents = new ArrayList<>();
            Collections.addAll(parents, this.parents);
            parents.add(option);
            // TODO change consumer to a sub category
            consumer.accept(new OptionEntryWriter(this.id, entry -> {}, this.ctx, parents.toArray(new Option[0])));
        }
        
        @Override
        public void writeArray(Consumer<OptionWriter<OptionType<?>>> consumer) {
            // Buffer all the values to get the value in object form
            ValueBuffer buffer = new ValueBuffer();
            buffer.writeArray(consumer);
            T values = this.type.read(buffer.copy());
            
            // Write the values into gui entries
            List<BaseOptionEntry<T>> entries = new ArrayList<>();
            OptionValueWriter<T> valueWriter = new OptionValueWriter<>(this.id, entries::add, this.ctx, null,
                    AnyOptionType.getInstance(), this.parents);
            buffer.writeTo(new NothingValueWriter() {
                @Override
                public void writeArray(Consumer<OptionWriter<OptionType<?>>> consumer) {
                    consumer.accept(elementType -> valueWriter);
                }
            }, ctx);
            
            Consumer<BaseOptionEntry<T>>[] delete = new Consumer[1];
            Function<BaseOptionEntry<T>, Integer>[] indexGetter = new Function[1];
            
            // Apply field names
            for (BaseOptionEntry<T> entry : entries) {
                entry.addComponent(new DeleteIconComponent<T>(entry) {
                    @Override
                    protected void onClicked() {
                        delete[0].accept(entry);
                    }
                });
                Pair<Integer, Component>[] last = new Pair[]{new Pair<Integer, Component>(-1, null)};
                entry.addFieldName(() -> {
                    int index = indexGetter[0].apply(entry);
                    if (last[0].getFirst().equals(index)) {
                        return last[0].getSecond();
                    }
                    last[0] = new Pair<>(index, new TranslatableComponent("text.clothbit.array.index", String.valueOf(index + 1)));
                    return last[0].getSecond();
                });
            }
            
            // Add array entry
            primitiveEntry(values, entry -> {
                ArrayValueEntryComponent<T> component = new ArrayValueEntryComponent<>(entry, entries);
                entry.addComponent(component);
                entry.addComponent(new AddIconComponent<T>(entry) {
                    @Override
                    protected void onClicked() {
//                        BaseOptionEntry<T>[] newEntry = new BaseOptionEntry[]{null};
//                        OptionValueWriter<T> valueWriter = new OptionValueWriter<>(id, e -> newEntry[0] = e, ctx, null,
//                                AnyOptionType.getInstance(), parents);
//                        type.write(type.getDefaultValue(), valueWriter);
                    }
                });
                delete[0] = valueEntry -> {
                    component.children.remove(valueEntry);
                    component.drawables.remove(valueEntry);
                    component.entries.remove(valueEntry);
                };
                indexGetter[0] = component.entries::indexOf;
            });
        }
        
        @Override
        public void close() {
            
        }
    }
}

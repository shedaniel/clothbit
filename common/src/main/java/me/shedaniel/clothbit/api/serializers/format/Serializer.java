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

package me.shedaniel.clothbit.api.serializers.format;

import com.google.gson.JsonElement;
import me.shedaniel.clothbit.api.options.OptionTypesContext;
import me.shedaniel.clothbit.api.options.OptionType;
import me.shedaniel.clothbit.api.options.OptionValue;
import me.shedaniel.clothbit.api.serializers.ValueReader;
import me.shedaniel.clothbit.api.serializers.ValueWriter;
import me.shedaniel.clothbit.impl.serializers.SerializerImpl;
import me.shedaniel.clothbit.impl.serializers.gson.GsonFormatDecoder;
import me.shedaniel.clothbit.impl.serializers.gson.GsonFormatEncoder;
import me.shedaniel.clothbit.impl.serializers.gson.element.GsonElementFormatDecoder;
import me.shedaniel.clothbit.impl.serializers.gson.element.GsonElementFormatEncoder;
import me.shedaniel.clothbit.impl.serializers.json5.Json5FormatEncoder;
import me.shedaniel.clothbit.impl.serializers.packet.PacketFormatDecoder;
import me.shedaniel.clothbit.impl.serializers.packet.PacketFormatEncoder;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.ApiStatus;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@ApiStatus.NonExtendable
public interface Serializer<W, R> {
    static <W, R> Serializer<W, R> of(FormatEncoder<W> encoder, FormatDecoder<R> decoder) {
        return new SerializerImpl<>(encoder, decoder);
    }
    
    /**
     * A serializer that uses {@link com.google.gson.stream.JsonWriter} and {@link com.google.gson.stream.JsonReader}
     * to serialize from/to json.
     * <p>
     * Accepted flags: {@link FormatFlag#indent(String)}
     */
    static Serializer<Writer, Reader> gson() {
        return of(new GsonFormatEncoder(), new GsonFormatDecoder());
    }
    
    /**
     * A serializer that serializes from/to {@link com.google.gson.JsonElement}.
     */
    static Serializer<Consumer<JsonElement>, JsonElement> gsonElement() {
        return of(new GsonElementFormatEncoder(), new GsonElementFormatDecoder());
    }
    
    /**
     * A serializer that serializes from/to json5.
     * <p>
     * Accepted flags: {@link FormatFlag#indent(String)}, {@link FormatFlag#comment(boolean)}, {@link FormatFlag#quoteKeys(boolean)}
     */
    static Serializer<Writer, Reader> json5() {
        return of(new Json5FormatEncoder(), new GsonFormatDecoder());
    }
    
    static Serializer<FriendlyByteBuf, FriendlyByteBuf> packet() {
        return of(new PacketFormatEncoder(), new PacketFormatDecoder());
    }
    
    static <T> String serializeString(Serializer<Writer, ?> serializer, OptionTypesContext ctx, OptionValue<T> value) {
        StringWriter stringWriter = new StringWriter();
        try (ValueWriter writer = serializer.writer(stringWriter)) {
            value.write(writer, ctx);
        }
        return stringWriter.toString();
    }
    
    static <T> T deserializeString(Serializer<?, Reader> serializer, OptionType<T> type, String string) {
        StringReader stringReader = new StringReader(string);
        T value;
        try (ValueReader reader = serializer.reader(stringReader)) {
            value = type.read(reader);
        }
        return value;
    }
    
    static <A, T> A serializeTo(Serializer<Consumer<A>, ?> serializer, OptionTypesContext ctx, OptionValue<T> value) {
        AtomicReference<A> ref = new AtomicReference<>();
        try (ValueWriter writer = serializer.writer(ref::set)) {
            value.write(writer, ctx);
        }
        return ref.get();
    }
    
    static <A, T> T deserializeTo(Serializer<?, A> serializer, OptionType<T> type, A data) {
        T value;
        try (ValueReader reader = serializer.reader(data)) {
            value = type.read(reader);
        }
        return value;
    }
    
    Serializer<W, R> flag(FormatFlag... flags);
    
    <T> ValueWriter writer(W writer);
    
    <T> ValueReader reader(R reader);
}

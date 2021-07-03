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

import me.shedaniel.clothbit.api.serializers.*;
import me.shedaniel.clothbit.api.serializers.format.Serializer;
import me.shedaniel.clothbit.api.serializers.format.FormatDecoder;
import me.shedaniel.clothbit.api.serializers.format.FormatFlag;
import me.shedaniel.clothbit.api.serializers.format.FormatEncoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SerializerImpl<W, R> implements Serializer<W, R> {
    private final FormatEncoder<W> encoder;
    private final FormatDecoder<R> decoder;
    private List<FormatFlag> flags = new ArrayList<>();
    
    public SerializerImpl(FormatEncoder<W> encoder, FormatDecoder<R> decoder) {
        this.encoder = Objects.requireNonNull(encoder);
        this.decoder = Objects.requireNonNull(decoder);
    }
    
    @Override
    public Serializer<W, R> flag(FormatFlag... flags) {
        Collections.addAll(this.flags, flags);
        return this;
    }
    
    @Override
    public <T> ValueWriter writer(W writer) {
        return this.encoder.writer(writer, this.flags.toArray(new FormatFlag[0]));
    }
    
    @Override
    public <T> ValueReader reader(R reader) {
        return this.decoder.reader(reader, this.flags.toArray(new FormatFlag[0]));
    }
}

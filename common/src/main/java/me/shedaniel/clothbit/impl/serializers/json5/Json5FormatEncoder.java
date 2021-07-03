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

package me.shedaniel.clothbit.impl.serializers.json5;

import me.shedaniel.clothbit.api.options.Option;
import me.shedaniel.clothbit.api.serializers.OptionWriter;
import me.shedaniel.clothbit.api.serializers.ValueWriter;
import me.shedaniel.clothbit.api.serializers.format.FormatFlag;
import me.shedaniel.clothbit.api.serializers.format.FormatEncoder;
import me.shedaniel.clothbit.api.serializers.format.flags.CommentFlag;
import me.shedaniel.clothbit.api.serializers.format.flags.IndentFlag;
import me.shedaniel.clothbit.api.serializers.format.flags.QuoteKeysFlag;
import me.shedaniel.clothbit.impl.utils.EscapingUtils.Task;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedWriter;
import java.io.Writer;
import java.util.function.Consumer;

import static me.shedaniel.clothbit.impl.utils.EscapingUtils.call;

public class Json5FormatEncoder implements FormatEncoder<Writer> {
    @Override
    public <T> ValueWriter writer(Writer writer, FormatFlag... flags) {
        BufferedWriter bufferedWriter = writer instanceof BufferedWriter ? (BufferedWriter) writer
                : new BufferedWriter(writer);
        Json5ValueWriter valueWriter = new Json5ValueWriter(bufferedWriter, bufferedWriter::close);
        for (FormatFlag flag : flags) {
            if (flag instanceof IndentFlag) {
                valueWriter.setIndent(((IndentFlag) flag).getIndent());
            } else if (flag instanceof CommentFlag) {
                valueWriter.setCommentsEnabled(((CommentFlag) flag).isEnabled());
            } else if (flag instanceof QuoteKeysFlag) {
                valueWriter.setQuoteKeys(!((QuoteKeysFlag) flag).isOnlyWhenNeeded());
            }
        }
        return valueWriter;
    }
    
    private static class Json5ValueWriter implements ValueWriter {
        private final BufferedWriter writer;
        @Nullable
        private final Task onClose;
        @Nullable
        private String indent = null;
        private boolean commentsEnabled = true;
        private boolean quoteKeys = true;
        private Json5TokenType expectType;
        
        public Json5ValueWriter(BufferedWriter writer, @Nullable Task onClose) {
            this.writer = writer;
            this.onClose = onClose;
        }
        
        public void setIndent(@Nullable String indent) {
            this.indent = indent;
        }
        
        public void setCommentsEnabled(boolean commentsEnabled) {
            this.commentsEnabled = commentsEnabled;
        }
    
        public void setQuoteKeys(boolean quoteKeys) {
            this.quoteKeys = quoteKeys;
        }
    
        @Override
        public void writeNull() {
            
        }
        
        @Override
        public void writeString(String value) {
            
        }
        
        @Override
        public void writeBoolean(boolean value) {
            
        }
    
        @Override
        public void writeCharacter(char value) {
        
        }
    
        @Override
        public void writeNumber(Number value) {
            
        }
        
        @Override
        public void writeObject(Consumer<OptionWriter<Option<?>>> consumer) {
            
        }
        
        @Override
        public void writeArray(Consumer<ValueWriter> consumer) {
            
        }
        
        @Override
        public void close() {
            if (onClose != null) {
                call(onClose);
            }
        }
    }
}

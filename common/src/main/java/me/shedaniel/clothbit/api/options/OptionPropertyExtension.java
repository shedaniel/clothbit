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

package me.shedaniel.clothbit.api.options;

import me.shedaniel.clothbit.api.options.property.CommentsOptionProperty;
import me.shedaniel.clothbit.api.options.property.NullableOptionProperty;
import org.jetbrains.annotations.Nullable;

public interface OptionPropertyExtension extends OptionPropertyProvider {
    @Nullable
    default String getComments() {
        return getProperty(CommentsOptionProperty.property());
    }
    
    default NullableOptionProperty.HandleMode getNullable() {
        return getProperty(NullableOptionProperty.property());
    }
    
    default boolean isNullable() {
        return getNullable() != NullableOptionProperty.HandleMode.DENY_NULL;
    }
}

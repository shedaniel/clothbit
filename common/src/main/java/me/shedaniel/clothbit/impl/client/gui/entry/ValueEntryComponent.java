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

package me.shedaniel.clothbit.impl.client.gui.entry;

import java.lang.reflect.Array;
import java.util.Objects;

public abstract class ValueEntryComponent<T> extends EntryComponent<T> {
    public boolean isEdited;
    public boolean hasErrors;
    
    public ValueEntryComponent(BaseOptionEntry<T> parent) {
        super(parent);
        listenValue(() -> isEdited = !equals(parent.originalValue, parent.value.get()));
    }
    
    private static boolean equals(Object v1, Object v2) {
        if (Objects.equals(v1, v2)) {
            return true;
        }
        if (v1 == null || v2 == null) {
            return false;
        }
        if (v1.getClass().isArray() && v2.getClass().isArray()) {
            if (Array.getLength(v1) != Array.getLength(v2)) {
                return false;
            }
            
            for (int i = 0; i < Array.getLength(v1); i++) {
                if (!equals(Array.get(v1, i), Array.get(v2, i))) {
                    return false;
                }
            }
            
            return true;
        }
        
        return false;
    }
}

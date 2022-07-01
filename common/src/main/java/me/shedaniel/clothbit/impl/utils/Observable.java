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

package me.shedaniel.clothbit.impl.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class Observable<T> {
    private T value;
    private int lastHash;
    private List<Runnable> listeners = new ArrayList<>();
    
    public Observable(T value) {
        this.value = value;
        this.lastHash = Objects.hashCode(this.value);
    }
    
    public void addListener(Runnable listener) {
        this.listeners.add(listener);
        listener.run();
    }
    
    public void addListener(Consumer<T> listener) {
        addListener(() -> listener.accept(get()));
    }
    
    public T get() {
        return value;
    }
    
    public void set(T value) {
        this.value = value;
    }
    
    public void setHidden(T value) {
        this.value = value;
        this.lastHash = Objects.hashCode(this.value);
    }
    
    public void update() {
        int hash = Objects.hashCode(this.value);
        if (this.lastHash != hash) {
            this.lastHash = hash;
            for (Runnable listener : this.listeners) {
                listener.run();
            }
        }
    }
}

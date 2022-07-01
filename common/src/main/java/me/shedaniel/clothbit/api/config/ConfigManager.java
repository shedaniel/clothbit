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

package me.shedaniel.clothbit.api.config;

import me.shedaniel.clothbit.api.options.OptionTypeAdapter;
import me.shedaniel.clothbit.impl.config.ConfigManagerImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface ConfigManager<T> {
    static <T> ConfigManager.Builder<T> builder(Class<T> type, String id) {
        return new ConfigManagerImpl.BuilderImpl<>(type, id);
    }
    
    static <T> ConfigManager<T> get(Class<T> type) {
        return ConfigManagerImpl.get(type);
    }
    
    void load();
    
    void save();
    
    T get();
    
    @Environment(EnvType.CLIENT)
    Screen getOptionsScreen(Screen parent);
    
    @ApiStatus.NonExtendable
    interface Builder<T> {
        Builder<T> adapter(OptionTypeAdapter adapter);
        
        Builder<T> adapters(OptionTypeAdapter... adapters);
        
        String getId();
        
        ConfigManager<T> build();
    }
}

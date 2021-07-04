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

package me.shedaniel.clothbit.api.config;

import me.shedaniel.clothbit.api.options.OptionTypeAdapter;
import me.shedaniel.clothbit.api.options.OptionTypesContext;
import me.shedaniel.clothbit.impl.config.ConfigManagerImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface ConfigManager<T> {
    static <T> ConfigManager<T> register(Class<T> type, Properties<T> properties) {
        return ConfigManagerImpl.register(type, properties);
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
    interface Properties<T> {
        static <T> Properties<T> of(String id) {
            return new ConfigManagerImpl.PropertiesImpl<>(id);
        }
        
        Properties<T> adapter(OptionTypeAdapter adapter);
    
        String getId();
    
        OptionTypesContext getConstructingContext();
    }
}

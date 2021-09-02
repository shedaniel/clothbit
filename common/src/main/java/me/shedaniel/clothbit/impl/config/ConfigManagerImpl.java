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

package me.shedaniel.clothbit.impl.config;

import me.shedaniel.clothbit.api.annotations.Config;
import me.shedaniel.clothbit.api.client.gui.OptionsScreen;
import me.shedaniel.clothbit.api.config.ConfigManager;
import me.shedaniel.clothbit.api.io.ConfigFormat;
import me.shedaniel.clothbit.api.options.OptionType;
import me.shedaniel.clothbit.api.options.OptionTypeAdapter;
import me.shedaniel.clothbit.api.options.OptionTypesContext;
import me.shedaniel.clothbit.impl.utils.ConfigFolderImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ConfigManagerImpl<T> implements ConfigManager<T> {
    private static final Map<Class<?>, ConfigManagerImpl<?>> INSTANCES = new HashMap<>();
    private final Class<T> type;
    private final String id;
    private final Config configAnnotation;
    private final ConfigFormat format;
    private final OptionType<T> optionType;
    private final OptionTypesContext optionTypesContext;
    private T value;
    
    public ConfigManagerImpl(Class<T> type, BuilderImpl<T> builder) {
        this.type = type;
        this.id = builder.getId();
        this.configAnnotation = Objects.requireNonNull(type.getAnnotation(Config.class),
                "Expected @Config annotation on " + type);
        try {
            this.format = configAnnotation.format().getDeclaredConstructor().newInstance().resolve();
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        this.optionTypesContext = builder.getConstructingContext();
        this.optionType = this.optionTypesContext.resolveType(this.type);
        load();
        save();
    }
    
    public static <T> ConfigManagerImpl<T> get(Class<T> type) {
        return (ConfigManagerImpl<T>) Objects.requireNonNull(INSTANCES.get(type), "Config manager for type " + type + " does not exist!");
    }
    
    public Path getPath() {
        return ConfigFolderImpl.getConfigFolder().resolve(configAnnotation.name());
    }
    
    @Override
    public void load() {
        this.value = this.format.readFrom(this.optionType, getPath(), this.optionTypesContext);
    }
    
    @Override
    public void save() {
        this.format.writeTo(this.optionType.withValue(value), getPath(), this.optionTypesContext);
    }
    
    @Override
    public T get() {
        return this.value;
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    public Screen getOptionsScreen(Screen parent) {
        OptionsScreen screen = OptionsScreen.of(parent, id, optionTypesContext);
        screen.add(this.optionType.withValue(value));
        return screen.get();
    }
    
    public static class BuilderImpl<T> implements Builder<T> {
        private final Class<T> type;
        private final String id;
        private final OptionTypesContext optionTypesContext = OptionTypesContext.defaultContext();
        
        public BuilderImpl(Class<T> type, String id) {
            this.type = type;
            this.id = id;
        }
        
        @Override
        public Builder<T> adapter(OptionTypeAdapter adapter) {
            this.optionTypesContext.addAdapter(adapter);
            return this;
        }
        
        @Override
        public Builder<T> adapters(OptionTypeAdapter... adapters) {
            for (OptionTypeAdapter adapter : adapters) {
                this.optionTypesContext.addAdapter(adapter);
            }
            
            return this;
        }
        
        @Override
        public String getId() {
            return id;
        }
        
        public OptionTypesContext getConstructingContext() {
            return optionTypesContext;
        }
        
        @Override
        public ConfigManager<T> build() {
            return (ConfigManagerImpl<T>) INSTANCES.computeIfAbsent(type, $ -> new ConfigManagerImpl<>(type, this));
        }
    }
}

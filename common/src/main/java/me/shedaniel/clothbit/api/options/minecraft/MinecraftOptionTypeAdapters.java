package me.shedaniel.clothbit.api.options.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.shedaniel.clothbit.api.options.OptionTypeAdapter;
import me.shedaniel.clothbit.api.options.type.adapter.simple.MappingOptionTypeAdapter;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public class MinecraftOptionTypeAdapters {
    public static OptionTypeAdapter[] all() {
        return new OptionTypeAdapter[]{
                location(),
                tag()
        };
    }
    
    public static OptionTypeAdapter location() {
        return new MappingOptionTypeAdapter<>(String.class, ResourceLocation.class, ResourceLocation::new, Objects::toString);
    }
    
    public static OptionTypeAdapter tag() {
        return new MappingOptionTypeAdapter<>(String.class, Tag.class, s -> {
            try {
                return new TagParser(new StringReader(s)).readValue();
            } catch (CommandSyntaxException e) {
                throw new RuntimeException(e);
            }
        }, Objects::toString);
    }
}

package me.shedaniel.clothbit.api.serializers;

import me.shedaniel.clothbit.api.options.OptionType;

public interface TypedValue<T> {
    OptionType<T> getType();
    
    T getValue();
}

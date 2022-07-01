package me.shedaniel.clothbit.api.serializers;

import me.shedaniel.clothbit.api.options.OptionType;

public class SingleTypedValue<T> implements TypedValue<T> {
    private final OptionType<T> type;
    private T value;
    
    public SingleTypedValue(OptionType<T> type, T value) {
        this.type = type;
        this.value = value;
    }
    
    @Override
    public OptionType<T> getType() {
        return type;
    }
    
    @Override
    public T getValue() {
        return value;
    }
}

package me.shedaniel.clothbit.api.serializers;

import me.shedaniel.clothbit.api.options.OptionType;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class TypedList<T> implements TypedValue<Collection<T>> {
    private final OptionType<Collection<T>> type;
    private List<TypedValue<T>> values;
    
    public TypedList(OptionType<Collection<T>> type, List<TypedValue<T>> values) {
        this.type = type;
        this.values = values;
    }
    
    @Override
    public OptionType<Collection<T>> getType() {
        return type;
    }
    
    @Override
    public Collection<T> getValue() {
        return values.stream().map(value -> value == null ? null : value.getValue()).collect(Collectors.toList());
    }
    
    public List<TypedValue<T>> getValues() {
        return values;
    }
}

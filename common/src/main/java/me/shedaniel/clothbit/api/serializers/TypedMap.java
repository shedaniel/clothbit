package me.shedaniel.clothbit.api.serializers;

import me.shedaniel.clothbit.api.options.OptionType;

import java.util.LinkedHashMap;
import java.util.Map;

public class TypedMap<T> implements TypedValue<Map<String, T>> {
    private final OptionType<Map<String, T>> type;
    private Map<String, TypedValue<T>> values;
    
    public TypedMap(OptionType<Map<String, T>> type, Map<String, TypedValue<T>> values) {
        this.type = type;
        this.values = values;
    }
    
    @Override
    public OptionType<Map<String, T>> getType() {
        return type;
    }
    
    @Override
    public Map<String, T> getValue() {
        Map<String, T> map = new LinkedHashMap<>();
        for (Map.Entry<String, TypedValue<T>> entry : values.entrySet()) {
            TypedValue<T> value = entry.getValue();
            map.put(entry.getKey(), value == null ? null : value.getValue());
        }
        return map;
    }
    
    public Map<String, TypedValue<T>> getValues() {
        return values;
    }
}

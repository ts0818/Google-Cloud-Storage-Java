package com.example.demo.type;

import java.util.Arrays;
import java.util.function.Predicate;

public interface EnumReverseLookupable {
    public static <E extends Enum<E>> E getByCondition(Class<E> enumClass, Predicate<E> p) {
    return Arrays
            .stream(enumClass.getEnumConstants())
            .filter(p)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException());
    }   
}

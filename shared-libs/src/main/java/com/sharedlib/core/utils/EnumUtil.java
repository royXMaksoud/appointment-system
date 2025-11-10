package com.sharedlib.core.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for working with Java enums.
 * Thread-safe and reusable across microservices.
 */
public final class EnumUtil {
    private EnumUtil() {}

    /**
     * Returns a list of all enum constant names for the given enum class.
     *
     * @param enumClass the enum class
     * @param <E> the enum type
     * @return list of enum constant names
     */
    public static <E extends Enum<E>> List<String> getNames(Class<E> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    /**
     * Checks if the given value matches any enum constant name (case-insensitive).
     *
     * @param enumClass the enum class
     * @param value the value to check
     * @param <E> the enum type
     * @return true if valid, false otherwise
     */
    public static <E extends Enum<E>> boolean isValidValue(Class<E> enumClass, String value) {
        if (value == null) return false;
        return Arrays.stream(enumClass.getEnumConstants())
                .anyMatch(e -> e.name().equalsIgnoreCase(value));
    }
}

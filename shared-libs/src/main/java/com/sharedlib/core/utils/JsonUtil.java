package com.sharedlib.core.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sharedlib.core.i18n.MessageResolver;

/**
 * Utility class for JSON serialization and deserialization using Jackson.
 * Thread-safe and reusable across microservices.
 */
public final class JsonUtil {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static MessageResolver messageResolver;

    private JsonUtil() {}

    /**
     * Initializes the MessageResolver for i18n error messages.
     * Should be called once during application startup.
     */
    public static void setMessageResolver(MessageResolver resolver) {
        messageResolver = resolver;
    }

    /**
     * Converts an object to its JSON string representation.
     *
     * @param obj the object to convert
     * @return JSON string
     * @throws RuntimeException if conversion fails
     */
    public static String toJson(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            String msg = resolveMessage("json.error.serialize", e.getMessage());
            throw new RuntimeException(msg, e);
        }
    }

    /**
     * Parses a JSON string into an object of the specified class.
     *
     * @param json the JSON string
     * @param clazz the target class
     * @param <T> the type of the target object
     * @return parsed object
     * @throws RuntimeException if parsing fails
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (Exception e) {
            String msg = resolveMessage("json.error.deserialize", e.getMessage());
            throw new RuntimeException(msg, e);
        }
    }

    private static String resolveMessage(String key, String detail) {
        if (messageResolver != null) {
            return messageResolver.getMessage(key, new Object[]{detail});
        }
        return key + ": " + detail;
    }
}

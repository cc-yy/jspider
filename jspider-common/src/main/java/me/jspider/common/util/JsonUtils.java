package me.jspider.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.jspider.base.bean.SpiderRuntimeException;

public class JsonUtils {
    private static final ObjectMapper mapper = new ObjectMapper();

    private JsonUtils() { }

    public static String serialize(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new SpiderRuntimeException("json serialization failed");
        }
    }

    public static <T> T deserialize(String text, Class<T> clazz) {
        try {
            return mapper.readValue(text, clazz);
        } catch (JsonProcessingException e) {
            throw new SpiderRuntimeException("json deserialization failed");
        }
    }
}

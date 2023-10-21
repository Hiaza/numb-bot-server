package com.example.numo.utils.serialization;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.jackson.nullable.JsonNullable;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.lang.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
@UtilityClass
public class JacksonUtils {

    public static final String DATETIME_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private static ObjectMapper mapper = getMapper();

    private static ObjectMapper fullMapper = createMapper().disable(MapperFeature.USE_ANNOTATIONS).setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

    public static ObjectMapper getMapper() {

        if (mapper != null) {
            return mapper;
        }
        return createMapper();
    }

    public static ObjectMapper createMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
        DateFormat dateFormat = new SimpleDateFormat(DATETIME_FORMAT_STRING);
        objectMapper.setDateFormat(dateFormat);

        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS, true);
        objectMapper.configure(DeserializationFeature.ACCEPT_FLOAT_AS_INT, false); //  For preventing float to integer auto conversion

        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.DEFAULT);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.DEFAULT);
        SimpleModule module = new SimpleModule();
        module.addSerializer(long.class, ToStringSerializer.instance);
        module.addSerializer(Long.class, ToStringSerializer.instance);
        module.addSerializer(BigDecimal.class, ToStringSerializer.instance);
        objectMapper.registerModule(module);
        objectMapper.registerModule(new JsonNullableModule());
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper;
    }

    public static String getJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Cannot get json from the object {}", object);
        }
        return null;
    }

    public static <T> T convert(Class<T> clazz, T object) {
        return fromJson(clazz, getJson(object));
    }

    public static <T> List<T> convertList(Class<T[]> clazz, T object) {
        return getListFromJson(clazz, getJson(object));
    }

    public static String getFullJson(Object object) {
        try {
            return fullMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Cannot get full json from the object {}", object);
        }
        return null;
    }

    public static String getFullPrettyJson(Object object) {
        try {
            return fullMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Cannot get full pretty json from the object {}", object);
        }
        return null;
    }

    public static <T> void changeIfPresent(JsonNullable<T> nullable, Consumer<T> consumer) {
        if (nullable.isPresent()) {
            consumer.accept(nullable.get());
        }
    }

    public static <T> void changeIfPresentAndValueChanged(JsonNullable<T> nullable, @Nullable T oldValue, Consumer<T> consumer) {
        if (nullable.isPresent() && !nullable.equals(JsonNullable.of(oldValue))) {
            consumer.accept(nullable.get());
        }
    }

    public static void getJson(Object object, File file) throws IOException {
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, object);
    }

    public static String getJson(Class<?> view, Object object) throws JsonProcessingException {
        return mapper.writerWithView(view).writeValueAsString(object);
    }

    public static String getPrettyJson(Object object) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Cannot get pretty json from the object {}, because {}", object, e.getMessage());
        }
        return null;
    }

    public static <T> T fromJson(Class<T> clazz, String json) {
        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            log.error("Cannot read object {} from a json {}", clazz, json);
            log.error("", e);
        }
        return null;
    }

    public static <T> T fromJson(TypeReference<T> clazz, String json) {
        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            log.error("Cannot read object {} from a json {}", clazz, json);
            log.error("", e);
        }
        return null;
    }

    public static <T> T fromJson(Class<T> clazz, File file) {
        try {
            return mapper.readValue(file, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T fromJson(TypeReference<T> clazz, File file) {
        try {
            return mapper.readValue(file, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T fromJsonFull(Class<T> clazz, File file) {
        try {
            return fullMapper.readValue(file, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T fromJsonFull(Class<T> clazz, String json) {
        try {
            return fullMapper.readValue(json, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> List<T> getListFromJson(Class<T[]> clazz, String json) {
        try {
            return Arrays.asList(mapper.readValue(json, clazz));
        } catch (IOException e) {
            log.error("Cannot read object list {} from a json {}", clazz, json);
        }
        return Collections.emptyList();
    }

    public static <T> List<T> getListFromJson(Class<T[]> clazz, File file) {
        try {
            return Arrays.asList(mapper.readValue(file, clazz));
        } catch (IOException e) {
            log.error("Cannot read object {} from a file {}", clazz, file);
        }
        return Collections.emptyList();
    }

    public static <T> List<T> getListFromInputStream(Class<T[]> clazz, InputStream inputStream) {
        try {
            return Arrays.asList(mapper.readValue(inputStream, clazz));
        } catch (IOException e) {
            log.error("Cannot read object {} from an input stream", clazz);
        }
        return Collections.emptyList();
    }

    public static <T> List<T> getListFromInputStreamFull(Class<T[]> clazz, InputStream inputStream) {
        try {
            return Arrays.asList(fullMapper.readValue(inputStream, clazz));
        } catch (IOException e) {
            log.error("Cannot read object {} from an input stream", clazz);
        }
        return Collections.emptyList();
    }

    public static <T> List<T> getListFromInputStream(Class<T[]> clazz, Reader reader) {
        try {
            return Arrays.asList(mapper.readValue(reader, clazz));
        } catch (IOException e) {
            log.error("Cannot read object {} from a reader", clazz);
        }
        return Collections.emptyList();
    }

    public static <T> List<T> getListFromJsonFull(Class<T[]> clazz, File file) {
        try {
            return Arrays.asList(fullMapper.readValue(file, clazz));
        } catch (IOException e) {
            log.error("Cannot read object {} from a file {}", clazz, file);
        }
        return Collections.emptyList();
    }

    public static <T> Map<String, T> getMapFromJson(Class<T> clazz, String json) {
        try {
            return mapper.readValue(json, new TypeReference<Map<String, T>>() {
            });
        } catch (IOException e) {
            log.error("Cannot read object {} from a json {}", clazz, json);
        }
        return Collections.emptyMap();
    }
}

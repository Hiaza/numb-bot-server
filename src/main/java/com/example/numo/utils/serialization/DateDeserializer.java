package com.example.numo.utils.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.OffsetDateTime;

public class DateDeserializer extends JsonDeserializer<OffsetDateTime> {

    @Override
    public OffsetDateTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonToken currentToken = jp.getCurrentToken();

        if (currentToken.equals(JsonToken.VALUE_STRING)) {
            OffsetDateTime value = SerializationUtils.parseDateTimeString(jp.getText());
            if (value != null) {
                return value;
            }
        } else if (currentToken.equals(JsonToken.VALUE_NULL)) {
            return null;
        }

        throw new IllegalArgumentException("Only valid date values supported. Values was " + jp.getText());
    }

}

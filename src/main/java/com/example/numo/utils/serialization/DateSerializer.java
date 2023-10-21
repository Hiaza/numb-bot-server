package com.example.numo.utils.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.OffsetDateTime;

public class DateSerializer extends JsonSerializer<OffsetDateTime> {

	@Override
	public void serialize(OffsetDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		gen.writeString(SerializationUtils.parseDateTimeToString(value));
	}
}

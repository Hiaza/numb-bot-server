package com.example.numo.entities.elastic;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;

@Accessors(chain = true)
@Data
public class EventES {
    String e_type;
//    OffsetDateTime timestamp;
}

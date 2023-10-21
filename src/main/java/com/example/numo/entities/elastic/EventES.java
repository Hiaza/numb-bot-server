package com.example.numo.entities.elastic;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class EventES {
    String e_type;
    private String created_at;
}

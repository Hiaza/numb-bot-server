package com.example.numo.entities.elastic;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "search-users")
@Accessors(chain = true)
@Data
public class UserES {
    private Boolean active_survey;
}

package com.example.numo.entities.elastic;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;

@Document(indexName = "search-users")
@Accessors(chain = true)
@Data
public class UserES {
    @Id
    private Long user_id;
    private Long user_bot_id;
    private Boolean active_survey;
    private String location;
    private String bot_type;
    private String utm_source;
    private String deeplink_tag;
    private List<ChildES> children;
    private List<EventES> events;
    private String notification_period;

//    @JsonSerialize(using = DateSerializer.class)
//    @JsonDeserialize(using = DateDeserializer.class)
//    @Field(type = FieldType.Date, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX||yyyy-MM-dd'T'HH:mm:ss.SSS", format = {})
//    @ValueConverter(CustomZonedDateTimeConverter.class)
    private String created_at;

}

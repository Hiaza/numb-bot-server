package com.example.numo.entities.elastic;

import com.example.numo.entities.enums.ActivityLevel;
import com.example.numo.entities.enums.AmountOfAdvices;
import com.example.numo.entities.enums.Origin;
import jakarta.persistence.GeneratedValue;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "groups")
@Accessors(chain = true)
@Data
public class Group {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private Origin origin;
    private Integer age;
    private String location;
    private AmountOfAdvices frequency;
    private Integer numOfChildren;
    private Integer likesMoreThan;
    private Integer dislikesMoreThan;
    private Boolean hasFinishedRegisterForm;
    private ActivityLevel activityLevel;

//    @JsonSerialize(using = DateSerializer.class)
//    @JsonDeserialize(using = DateDeserializer.class)
    private String registeredFrom;

//    @JsonSerialize(using = DateSerializer.class)
//    @JsonDeserialize(using = DateDeserializer.class)
    private String registeredTo;
}
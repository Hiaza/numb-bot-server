package com.example.numo.entities.elastic;

import com.example.numo.entities.enums.CommunicationType;
import jakarta.persistence.GeneratedValue;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.ZonedDateTime;

@Document(indexName = "schedules")
@Accessors(chain = true)
@Data
public class Schedule {
    @Id
    @GeneratedValue
    private Long id;
    private ZonedDateTime createdAt;
    private Long groupId;
    private CommunicationType communicationType;
    private ZonedDateTime scheduledTime;
}
package com.example.numo.controllers;

import co.elastic.clients.elasticsearch._types.aggregations.LongTermsBucket;
import com.example.numo.dto.Bucket;
import com.example.numo.dto.GroupDto;
import com.example.numo.entities.elastic.Group;
import com.example.numo.entities.elastic.UserES;
import com.example.numo.services.GroupService;
import com.example.numo.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregation;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(ControllerAPI.USER_CONTROLLER)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping(value = "/groups" + ControllerAPI.CONTROLLER_SPECIFIC_REQUEST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserES>> getByGroupId(@PathVariable Long id) {
        return new ResponseEntity<>(userService.getUsersByGroup(id), HttpStatus.OK);
    }

    //for testing
    @PostMapping(value = "/groups" + ControllerAPI.CONTROLLER_GENERAL_REQUEST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserES>> create(@RequestBody GroupDto dto) {
        return new ResponseEntity<>(userService.getUsersByGroup(dto), HttpStatus.OK);
    }

    @PostMapping(value = "/aggs/subscription" + ControllerAPI.CONTROLLER_GENERAL_REQUEST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, List<Bucket>>> aggsSubscription(@RequestBody GroupDto dto) {
        return new ResponseEntity<>(userService.getAggregateSubscribeUnsubscribeStatByGroup(dto), HttpStatus.OK);
    }

    @PostMapping(value = "/aggs/sources" + ControllerAPI.CONTROLLER_GENERAL_REQUEST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, List<Bucket>>> aggsSources(@RequestBody GroupDto dto) {
        return new ResponseEntity<>(userService.aggregateUsersBySource(dto), HttpStatus.OK);
    }

    @PostMapping(value = "/aggs/activity" + ControllerAPI.CONTROLLER_GENERAL_REQUEST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, List<Bucket>>> aggsActivity(@RequestBody GroupDto dto) {
        return new ResponseEntity<>(userService.aggregateUsersActivityByGroup(dto), HttpStatus.OK);
    }
}

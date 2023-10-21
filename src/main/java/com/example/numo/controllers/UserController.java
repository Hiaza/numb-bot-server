package com.example.numo.controllers;

import com.example.numo.dto.Bucket;
import com.example.numo.dto.GroupDto;
import com.example.numo.entities.elastic.UserES;
import com.example.numo.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(ControllerAPI.USER_CONTROLLER)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping(value = ControllerAPI.CONTROLLER_GENERAL_REQUEST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserES>> getUsers() {
        return new ResponseEntity<>(userService.getUsers(), HttpStatus.OK);
    }

    @GetMapping(value = ControllerAPI.CONTROLLER_SPECIFIC_REQUEST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserES> getUserById(@PathVariable Long id) {
        return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
    }

    @GetMapping(value = "/groups" + ControllerAPI.CONTROLLER_SPECIFIC_REQUEST + "/file")
    public void getFileOfUsersWithGroupId(HttpServletResponse response, @PathVariable Long id) throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=users_" + currentDateTime + ".csv";
        response.setHeader(headerKey, headerValue);
        response.setCharacterEncoding("utf-8");

        List<UserES> listUsers = userService.getUsersByGroup(id);

        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.EXCEL_PREFERENCE);
        String[] csvHeader = {"User ID", "User Bot ID", "Bot type", "Has active Survey", "Location", "Notification period", "Like count", "Dislike count", "Created at", "Number of children"};
        String[] nameMapping = {"user_id", "user_bot_id", "bot_type", "active_survey", "location", "notification_period", "like_count", "dislike_count", "created_at", "num_of_children"};

        csvWriter.writeHeader(csvHeader);
        for (UserES user : listUsers) {
            csvWriter.write(user, nameMapping);
        }

        csvWriter.close();
    }

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

    @PostMapping(value = "/aggs/likesactivity" + ControllerAPI.CONTROLLER_GENERAL_REQUEST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, List<Bucket>>> aggsLikesActivity(@RequestBody GroupDto dto) {
        return new ResponseEntity<>(userService.aggregateLikesActivity(dto), HttpStatus.OK);
    }

    @PostMapping(value = "/aggs/dislikesactivity" + ControllerAPI.CONTROLLER_GENERAL_REQUEST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, List<Bucket>>> aggsDislikesActivity(@RequestBody GroupDto dto) {
        return new ResponseEntity<>(userService.aggregateDislikesActivity(dto), HttpStatus.OK);
    }

    @PostMapping(value = "/aggs/topEvents" + ControllerAPI.CONTROLLER_GENERAL_REQUEST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, List<Bucket>>> aggsTopEvents(@RequestBody GroupDto dto) {
        return new ResponseEntity<>(userService.aggregateTopEventsByGroup(dto), HttpStatus.OK);
    }
}

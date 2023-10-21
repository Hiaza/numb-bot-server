package com.example.numo.services;

import co.elastic.clients.elasticsearch._types.aggregations.LongTermsBucket;
import com.example.numo.dto.Bucket;
import com.example.numo.dto.GroupDto;
import com.example.numo.entities.elastic.Group;
import com.example.numo.entities.elastic.UserES;
import com.example.numo.mappers.GroupMapper;
import com.example.numo.repositories.elastic.UserRepository.UserESRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregation;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserESRepository userESRepository;

    private final GroupService groupService;

    private final GroupMapper groupMapper;

    public List<UserES> getUsersByGroup(Long id) {
        return userESRepository.findAllFromGroup(groupService.getGroupById(id));
    }

    public List<UserES> getUsersByGroup(GroupDto dto) {
        return userESRepository.findAllFromGroup(groupMapper.toModel(dto));
    }

    public Map<String, List<Bucket>> getAggregateSubscribeUnsubscribeStatByGroup(GroupDto dto) {
        return userESRepository.aggregateSubscribeUnsubscribeStatByGroup(groupMapper.toModel(dto));
    }

    public Map<String, List<Bucket>> aggregateUsersBySource(GroupDto dto) {
        return userESRepository.aggregateUsersBySource(groupMapper.toModel(dto));
    }

    public Map<String, List<Bucket>> aggregateUsersActivityByGroup(GroupDto dto) {
        return userESRepository.aggregateActivityByGroup(groupMapper.toModel(dto));
    }

    public Map<String, List<Bucket>> aggregateLikesActivity(GroupDto dto) {
        return userESRepository.aggregateLikesActivityByGroup(groupMapper.toModel(dto));
    }

    public Map<String, List<Bucket>> aggregateDislikesActivity(GroupDto dto) {
        return userESRepository.aggregateDislikesActivityByGroup(groupMapper.toModel(dto));
    }

    public Map<String, List<Bucket>> aggregateTopEventsByGroup(GroupDto dto) {
        return userESRepository.aggregateTopEventsByGroup(groupMapper.toModel(dto));
    }
}

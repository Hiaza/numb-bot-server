package com.example.numo.services;

import com.example.numo.dto.GroupDto;
import com.example.numo.entities.elastic.Group;
import com.example.numo.entities.elastic.UserES;
import com.example.numo.mappers.GroupMapper;
import com.example.numo.repositories.elastic.UserRepository.UserESRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserESRepository userESRepository;

    private final GroupService groupService;

    private final GroupMapper groupMapper;

    public List<UserES> getUsersByGroup(Long id){
        return userESRepository.findAllFromGroup(groupService.getGroupById(id));
    }
    public List<UserES> getUsersByGroup(GroupDto dto){
        return userESRepository.findAllFromGroup(groupMapper.toModel(dto));
    }
}

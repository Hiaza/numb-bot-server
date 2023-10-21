package com.example.numo.repositories.elastic.UserRepository;

import com.example.numo.entities.elastic.Group;
import com.example.numo.entities.elastic.UserES;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserESCustomRepository {
    List<UserES> findAllFromGroup(Group group);
}

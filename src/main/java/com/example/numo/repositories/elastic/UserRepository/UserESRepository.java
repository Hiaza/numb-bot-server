package com.example.numo.repositories.elastic.UserRepository;

import com.example.numo.entities.elastic.UserES;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UserESRepository extends ElasticsearchRepository<UserES, Long>, UserESCustomRepository {

}

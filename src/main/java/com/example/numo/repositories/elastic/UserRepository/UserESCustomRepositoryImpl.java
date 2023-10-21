package com.example.numo.repositories.elastic.UserRepository;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.example.numo.entities.elastic.Group;
import com.example.numo.entities.elastic.UserES;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserESCustomRepositoryImpl implements UserESCustomRepository{

    @Autowired
    private ElasticsearchOperations esOperations;

    public List<UserES> findAllFromGroup(Group group){
        final BoolQuery.Builder queryBuilder = QueryBuilders.bool();
//        queryBuilder.must(QueryBuilders.matchQuery("name", "A"));
        if (group.getLocation()!=null){
            queryBuilder.must(QueryBuilders.match(q -> q.field("location").query(group.getLocation())));
        }
        if (group.getAge()!=null){
            queryBuilder.must(QueryBuilders.match(q -> q.field("children.age").query(group.getAge())));
        }
        if (group.getFrequency()!=null){
            queryBuilder.must(QueryBuilders.match(q -> q.field("notification_period").query(group.getFrequency().name().toLowerCase())));
        }
        if (group.getOrigin()!=null){
            queryBuilder.must(QueryBuilders.match(q -> q.field("bot_type").query(group.getOrigin().name().toLowerCase())));
        }
        if (group.getHasFinishedRegisterForm()!=null){
            queryBuilder.must(QueryBuilders.match(q -> q.field("active_survey").query(group.getHasFinishedRegisterForm())));
        }
        if (group.getNumOfChildren()!=null){
            queryBuilder.must(QueryBuilders.match(q -> q.field("num_of_children").query(group.getNumOfChildren())));
        }


//        RangeQueryBuilder availability = QueryBuilders.rangeQuery("children.age")
//            .gte(query.getStartDate())
//            .lte(query.getEndDate())
//            .relation("within");
//        queryBuilder.must(availability);


//        final NestedQueryBuilder nested = QueryBuilders.nestedQuery("availability", queryBuilder, ScoreMode.None);

        // @formatter:off
        NativeQuery query = new NativeQueryBuilder()
            .withQuery(queryBuilder.build()._toQuery())
            .build();
        System.out.println(query.getQuery());

        List<SearchHit<UserES>> articles = esOperations
            .search(query, UserES.class, IndexCoordinates.of("search-users"))
            .getSearchHits();
        System.out.println(articles);
        return articles.stream().map(SearchHit::getContent).toList();
    }
}

package com.example.numo.repositories.elastic.UserRepository;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.aggregations.*;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQueryField;
import co.elastic.clients.json.JsonData;
import com.example.numo.dto.Bucket;
import com.example.numo.entities.elastic.EventES;
import com.example.numo.entities.elastic.Group;
import com.example.numo.entities.elastic.UserES;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregation;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserESCustomRepositoryImpl implements UserESCustomRepository {

    @Autowired
    private ElasticsearchOperations esOperations;

    public List<UserES> findAllFromGroup(Group group) {
        final BoolQuery.Builder queryBuilder = getBasicGroupQuery(group);

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

    private BoolQuery.Builder getBasicGroupQuery(Group group) {
        final BoolQuery.Builder queryBuilder = QueryBuilders.bool();
//        queryBuilder.must(QueryBuilders.matchQuery("name", "A"));
        if (group.getLocation() != null) {
            queryBuilder.must(QueryBuilders.match(q -> q.field("location").query(group.getLocation())));
        }
        if (group.getAge() != null) {
            queryBuilder.must(QueryBuilders.match(q -> q.field("children.age").query(group.getAge())));
        }
        if (group.getFrequency() != null) {
            queryBuilder.must(QueryBuilders.match(q -> q.field("notification_period").query(group.getFrequency().name().toLowerCase())));
        }
        if (group.getOrigin() != null) {
            queryBuilder.must(QueryBuilders.match(q -> q.field("bot_type").query(group.getOrigin().name().toLowerCase())));
        }
        if (group.getHasFinishedRegisterForm() != null) {
            queryBuilder.must(QueryBuilders.match(q -> q.field("active_survey").query(group.getHasFinishedRegisterForm())));
        }
        if (group.getNumOfChildren() != null) {
            queryBuilder.must(QueryBuilders.match(q -> q.field("num_of_children").query(group.getNumOfChildren())));
        }
        if (group.getLikesMoreThan()!=null){
            queryBuilder.must(QueryBuilders.range(q -> q.field("like_count").gte(JsonData.of(group.getLikesMoreThan()))));
        }
        if (group.getDislikesMoreThan()!=null){
            queryBuilder.must(QueryBuilders.range(q -> q.field("dislike_count").gte(JsonData.of(group.getDislikesMoreThan()))));
        }
        if (group.getDislikesMoreThan()!=null){
            queryBuilder.must(QueryBuilders.range(q -> q.field("dislike_count").gte(JsonData.of(group.getDislikesMoreThan()))));
        }
        if (group.getRegisteredFrom()!=null){
            queryBuilder.must(QueryBuilders.range(q -> q.field("created_at").gte(JsonData.of(group.getRegisteredFrom()))));
        }
        if (group.getRegisteredTo()!=null){
            queryBuilder.must(QueryBuilders.range(q -> q.field("created_at").lte(JsonData.of(group.getRegisteredTo()))));
        }


        return queryBuilder;
    }

    @Override
    public Map<String, List<Bucket>> aggregateSubscribeUnsubscribeStatByGroup(Group group) {
        final BoolQuery.Builder queryBuilder = getBasicGroupQuery(group);

        Aggregation agg = new Aggregation.Builder()
                .terms(new TermsAggregation.Builder().field("is_subscribed").build())
                .build();

        NativeQuery query = new NativeQueryBuilder()
//                .withQuery(queryBuilder.build()._toQuery())
                .withAggregation("is_subscribed", agg)
                .build();
//        System.out.println(query.getQuery());
        System.out.println(query.getAggregations());

        SearchHits<UserES> searchHits = esOperations.search(query, UserES.class, IndexCoordinates.of("search-users"));

        ElasticsearchAggregations aggregations = (ElasticsearchAggregations) searchHits.getAggregations();
        Map<String, ElasticsearchAggregation> aggMap = aggregations.aggregationsAsMap();
        List<LongTermsBucket> buckets = aggMap.get("is_subscribed").aggregation().getAggregate().lterms().buckets().array();
        System.out.println(buckets);

        return Map.of("is_subscribed", buckets.stream().map(b -> new Bucket(b.keyAsString(), b.docCount())).toList());
    }

    @Override
    public Map<String, List<Bucket>> aggregateUsersBySource(Group group) {
        final BoolQuery.Builder queryBuilder = getBasicGroupQuery(group);

        Aggregation agg = new Aggregation.Builder()
                .terms(new TermsAggregation.Builder().field("utm_source.enum").build())
                .build();

        NativeQuery query = new NativeQueryBuilder()
//                .withQuery(queryBuilder.build()._toQuery())
                .withAggregation("sources", agg)
                .build();
//        System.out.println(query.getQuery());
        System.out.println(query.getAggregations());

        SearchHits<UserES> searchHits = esOperations.search(query, UserES.class, IndexCoordinates.of("search-users"));
        ElasticsearchAggregations aggregations = (ElasticsearchAggregations) searchHits.getAggregations();
        Map<String, ElasticsearchAggregation> aggMap = aggregations.aggregationsAsMap();
        List<StringTermsBucket> buckets = aggMap.get("sources").aggregation().getAggregate().sterms().buckets().array();

        return Map.of("sources", buckets.stream().map(b -> new Bucket(b.key().stringValue(), b.docCount())).toList());
    }

    @Override
    public Map<String, List<Bucket>> aggregateActivityByGroup(Group group) {
        final BoolQuery.Builder queryBuilder = getBasicGroupQuery(group);
        NativeQuery query = new NativeQueryBuilder()
//                .withQuery(queryBuilder.build()._toQuery())
//                .withAggregation("sources", agg)
                .build();
        List<SearchHit<UserES>> searchHits = esOperations
                .search(query, UserES.class, IndexCoordinates.of("search-users"))
                .getSearchHits();
        List<Long> userIds = searchHits.stream().map(SearchHit::getId)
                .map(v -> {
                    try {
                        return Long.parseLong(v);
                    } catch (Exception e) {
                        return (long) 0;
                    }
                }).toList();

        final TermsQuery.Builder eventQueryBuilder = QueryBuilders.terms()
                .field("user_id")
                .terms(new TermsQueryField.Builder().value(userIds.stream().map(v -> new FieldValue.Builder().longValue(v).build()).toList()).build());

        Aggregation agg = new Aggregation.Builder()
                .dateHistogram(new DateHistogramAggregation.Builder()
                        .field("timestamp")
                        .calendarInterval(CalendarInterval.Day).build())
                .build();

        NativeQuery eventsQuery = new NativeQueryBuilder()
                .withQuery(eventQueryBuilder.build()._toQuery())
                .withAggregation("activity", agg)
                .build();
        System.out.println(eventsQuery.getQuery());
        System.out.println(eventsQuery.getAggregations());

        SearchHits<EventES> eventHits = esOperations.search(eventsQuery, EventES.class, IndexCoordinates.of("search-events"));
        ElasticsearchAggregations aggregations = (ElasticsearchAggregations) eventHits.getAggregations();
        Map<String, ElasticsearchAggregation> aggMap = aggregations.aggregationsAsMap();
        List<DateHistogramBucket> buckets = aggMap.get("activity").aggregation().getAggregate().dateHistogram().buckets().array();

        System.out.println(buckets);
        return Map.of("sources", buckets.stream().map(b -> new Bucket(b.keyAsString(), b.docCount())).toList());
    }
}

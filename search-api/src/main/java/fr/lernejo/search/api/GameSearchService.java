package fr.lernejo.search.api;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GameSearchService {


    private final RestHighLevelClient elasticsearchClient;

    public GameSearchService(RestHighLevelClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }
    public List<Map<String, Object>> searchGames(String query, int size) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().query(QueryBuilders.queryStringQuery(query)).size(size);
        SearchRequest searchRequest = new SearchRequest("games").source(sourceBuilder);
        try {
            SearchResponse response = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
            return Arrays.stream(response.getHits().getHits()).map(hit -> hit.getSourceAsMap()).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

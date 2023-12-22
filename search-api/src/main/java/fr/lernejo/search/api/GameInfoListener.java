package fr.lernejo.search.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class GameInfoListener {

    private final RestHighLevelClient elasticsearchClient;
    private final ObjectMapper objectMapper;

    public GameInfoListener(RestHighLevelClient elasticsearchClient, ObjectMapper objectMapper) {
        this.elasticsearchClient = elasticsearchClient;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "game_info")
    public void onMessage(String message) {
        try {
            System.out.println(message);
            Map<String, Object> gameData = objectMapper.readValue(message, Map.class);
            String gameId = (String) gameData.get("game_id");
            IndexRequest indexRequest = new IndexRequest("games").id(gameId).source(gameData);
            IndexResponse response = elasticsearchClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


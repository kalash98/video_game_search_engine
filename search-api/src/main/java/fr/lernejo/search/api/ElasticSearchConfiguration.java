package fr.lernejo.search.api;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;

@Configuration
public class ElasticSearchConfiguration {
    final private String host;
    final private int port;
    final private String username;
    final private String password;

    public ElasticSearchConfiguration(@Value("${elasticsearch.host:localhost}")String host,
                                      @Value("${elasticsearch.port:}") int port,
                                      @Value("${elasticsearch.username}") String username,
                                      @Value("${elasticsearch.password:}") String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }


    @Bean
    public RestHighLevelClient elasticsearchClient() {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
            .connectedTo(host + ":" + port)
            .withBasicAuth(username, password)
            .build();

        return RestClients.create(clientConfiguration).rest();
    }
}

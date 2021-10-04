package com.feblic.secure.configs;

import com.feblic.secure.events.BaseModelListener;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;


@Configuration
@EnableMongoAuditing
public class MongoConfig {
    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${spring.data.mongodb.database}")
    private String database;

    @Bean
    public MongoClient mongo() {
        MongoClient mongoClient = MongoClients.create(mongoUri);
        return mongoClient;
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        MongoTemplate mongoTemplate = new MongoTemplate(mongo(), database);
        return mongoTemplate;
    }

    @Bean
    public BaseModelListener modelListener() {
        return new BaseModelListener();
    }
}

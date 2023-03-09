package com.example.restservice.configuration;

import com.example.restservice.repository.DynamoDBTodoRepository;
import com.example.restservice.repository.ITodoRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public ITodoRepository todoRepository() {
        return new DynamoDBTodoRepository();
    }
}

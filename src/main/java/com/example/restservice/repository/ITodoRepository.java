package com.example.restservice.repository;

import com.example.restservice.model.Todo;

import java.util.UUID;

public interface ITodoRepository {
    Todo get(UUID todoId);
    Todo create(String todoItem);
    Todo update(UUID todoId, String todoItem);
    void delete(UUID todoId);

}

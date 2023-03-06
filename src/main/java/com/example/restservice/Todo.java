package com.example.restservice;
import java.util.UUID;

public class Todo {
    private final UUID id;
    private final String item;

    public Todo(UUID id, String item) {
        this.id = id;
        this.item = item;
    }

    public UUID getId() {
        return id;
    }

    public String getContent() {
        return item;
    }

}

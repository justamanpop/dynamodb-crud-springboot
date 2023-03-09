package com.example.restservice.controller.todo;

import com.example.restservice.model.Todo;
import com.example.restservice.model.TodoRequestModel;
import com.example.restservice.repository.ITodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class TodoListController {
    private ITodoRepository todoRepository;

    @Autowired
    public TodoListController(ITodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @GetMapping("/todo")
    public ResponseEntity<Todo> getTodo(@RequestParam(value = "id")  UUID id) {
        var todo = todoRepository.get(id);
        return new ResponseEntity<Todo>(todo, HttpStatus.OK);
    }

    @PostMapping(
            value = "/todo",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<Todo> createTodo(@RequestBody TodoRequestModel requestModel) {
        var createdTodo = todoRepository.create(requestModel.item);
        return new ResponseEntity<Todo>(createdTodo, HttpStatus.CREATED);
    }

    @PutMapping(
            value = "/todo/{todoId}",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<Todo> updateTodo(@PathVariable("todoId") UUID todoId, @RequestBody TodoRequestModel requestModel) {
        var updatedTodo = todoRepository.update(todoId, requestModel.item);
        return new ResponseEntity<Todo>(updatedTodo, HttpStatus.OK);
    }

    @DeleteMapping(value = "/todo")
    public ResponseEntity deleteTodo(@RequestParam(value = "id")  UUID todoId) {
        todoRepository.delete(todoId);
        return new ResponseEntity(HttpStatus.OK);
    }
}


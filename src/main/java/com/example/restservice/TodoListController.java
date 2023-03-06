package com.example.restservice;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import com.amazonaws.AmazonServiceException;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TodoListController {
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity handleMissingParams(MissingServletRequestParameterException ex) {
        String name = ex.getParameterName();
        return new ResponseEntity(name + " must be passed",HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
    public ResponseEntity handleInvalidId(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException ex) {
        String property = ex.getName();
        return new ResponseEntity(property + " must be a valid UUID",HttpStatus.BAD_REQUEST);
    }
    @GetMapping("/todo")
    public ResponseEntity<Todo> getTodo(@RequestParam(value = "id")  UUID id) {

        HashMap<String,AttributeValue> key_to_get = new HashMap<String,AttributeValue>();
        key_to_get.put("id", new AttributeValue(id.toString()));
        GetItemRequest request = null;

        request = new GetItemRequest()
                .withKey(key_to_get)
                .withTableName("TodoList");

        final AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .withCredentials(new ProfileCredentialsProvider("misctest"))
                .build();
        Map<String,AttributeValue> returned_item =
                ddb.getItem(request).getItem();

        String returnString = "";

        if (returned_item != null) {
            var returnedObject = returned_item.get("item");
            if(returnedObject != null) {
                returnString = returnedObject.getS();
            }

            else {
                return new ResponseEntity("Given object doesn't have field item",HttpStatus.BAD_REQUEST);
            }
        }

        else {
            return new ResponseEntity("Could not find any object with given id",HttpStatus.NOT_FOUND);
        }

        var todo = new Todo(id, returnString);
        return new ResponseEntity<>(todo, HttpStatus.OK);
    }

    @PostMapping(
            value = "/todo",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity createTodo(@RequestBody TodoRequestModel requestModel) {
        final AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .withCredentials(new ProfileCredentialsProvider("misctest"))
                .build();

        var todoItem = requestModel.item;

        HashMap<String,AttributeValue> item_values =
                new HashMap<String,AttributeValue>();

        var todoUuid = UUID.randomUUID();

        item_values.put("id", new AttributeValue(todoUuid.toString()));
        item_values.put("item", new AttributeValue(todoItem));

        try {
            ddb.putItem("TodoList", item_values);
        } catch (ResourceNotFoundException e) {
            System.err.format("Error: The table TodoList can't be found.\n");
            System.err.println("Be sure that it exists and that you've typed its name correctly!");
            return new ResponseEntity<>("Error: The table TodoList can't be found.",HttpStatus.BAD_REQUEST);
        } catch (AmazonServiceException e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(new Todo(todoUuid, todoItem), HttpStatus.OK);
    }

    @PutMapping(
            value = "/todo/{todoId}",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity updateTodo(@PathVariable("todoId") UUID todoId, @RequestBody TodoRequestModel requestModel) {
        final AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .withCredentials(new ProfileCredentialsProvider("misctest"))
                .build();

        var todoItem = requestModel.item;

        HashMap<String, AttributeValue> item_key =
                new HashMap<String, AttributeValue>();

        item_key.put("id", new AttributeValue(todoId.toString()));

        HashMap<String, AttributeValueUpdate> updated_values =
                new HashMap<String, AttributeValueUpdate>();
        updated_values.put("item", new AttributeValueUpdate(new AttributeValue(todoItem), AttributeAction.PUT));

        try {
            ddb.updateItem("TodoList", item_key, updated_values);
        } catch (ResourceNotFoundException e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>("Error: The table TodoList can't be found.", HttpStatus.BAD_REQUEST);
        } catch (AmazonServiceException e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(new Todo(todoId, todoItem), HttpStatus.OK);
    }

    @DeleteMapping(value = "/todo")
    public ResponseEntity deleteTodo(@RequestParam(value = "id")  UUID todoId) {

        final AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .withCredentials(new ProfileCredentialsProvider("misctest"))
                .build();

        HashMap<String, AttributeValue> item_key =
                new HashMap<String, AttributeValue>();
        item_key.put("id", new AttributeValue(todoId.toString()));

        DeleteItemRequest deleteReq = new DeleteItemRequest()
                .withTableName("TodoList")
                .withKey(item_key);

        try {
            ddb.deleteItem(deleteReq);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.noContent().build();
    }
}


package com.example.restservice.repository;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.*;
import com.example.restservice.exception.BadRequestException;
import com.example.restservice.exception.InternalErrorException;
import com.example.restservice.exception.NotFoundException;
import com.example.restservice.model.Todo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DynamoDBTodoRepository implements ITodoRepository{

    private AmazonDynamoDB createClient() {
        return AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .withCredentials(new ProfileCredentialsProvider("misctest"))
                .build();
    }

    private String tableName = "TodoList"; //could be put in a config file
    @Override
    public Todo get(UUID todoId) throws BadRequestException, NotFoundException {
        final AmazonDynamoDB ddb = createClient();

        HashMap<String, AttributeValue> key_to_get = new HashMap<String,AttributeValue>();
        key_to_get.put("id", new AttributeValue(todoId.toString()));

        GetItemRequest request = null;

        request = new GetItemRequest()
                .withKey(key_to_get)
                .withTableName(tableName);

        Map<String,AttributeValue> returned_item =
                ddb.getItem(request).getItem();

        String todoItem = "";

        if (returned_item != null) {
            var returnedObject = returned_item.get("item");
            if(returnedObject != null) {
                todoItem = returnedObject.getS();
            }

            else {
                throw new BadRequestException("Object with given ID does not have the field 'item'");
            }
        }

        else {
            throw new NotFoundException("Could not find any object with given id");
        }

        var todo = new Todo(todoId, todoItem);
        return todo;
    }

    @Override
    public Todo create(String todoItem) {
        final AmazonDynamoDB ddb = createClient();

        HashMap<String,AttributeValue> item_values =
                new HashMap<String,AttributeValue>();

        var todoUuid = UUID.randomUUID();

        item_values.put("id", new AttributeValue(todoUuid.toString()));
        item_values.put("item", new AttributeValue(todoItem));

        try {
            ddb.putItem(tableName, item_values);
        } catch (ResourceNotFoundException e) {
            throw new InternalErrorException(String.format("Error: The table %s can't be found", tableName));
        } catch (AmazonServiceException e) {
            throw new InternalErrorException(e.getMessage());
        }

        return new Todo(todoUuid, todoItem);
    }

    @Override
    public Todo update(UUID todoId, String todoItem) {
        final AmazonDynamoDB ddb = createClient();

        HashMap<String, AttributeValue> item_key =
                new HashMap<String, AttributeValue>();

        item_key.put("id", new AttributeValue(todoId.toString()));

        HashMap<String, AttributeValueUpdate> updated_values =
                new HashMap<String, AttributeValueUpdate>();
        updated_values.put("item", new AttributeValueUpdate(new AttributeValue(todoItem), AttributeAction.PUT));

        try {
            ddb.updateItem(tableName, item_key, updated_values);
        } catch (ResourceNotFoundException e) {
            System.err.println(e.getMessage());
            throw new InternalErrorException(String.format("Error: The table %s can't be found.", tableName));
        } catch (AmazonServiceException e) {
            System.err.println(e.getMessage());
            throw new InternalErrorException(e.getMessage());
        }

        return new Todo(todoId, todoItem);
    }

    @Override
    public void delete(UUID todoId) {
        final AmazonDynamoDB ddb = createClient();

        HashMap<String, AttributeValue> item_key =
                new HashMap<String, AttributeValue>();
        item_key.put("id", new AttributeValue(todoId.toString()));

        DeleteItemRequest deleteReq = new DeleteItemRequest()
                .withTableName(tableName)
                .withKey(item_key);

        try {
            ddb.deleteItem(deleteReq);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new BadRequestException(e.getMessage());
        }

        return;
    }
}

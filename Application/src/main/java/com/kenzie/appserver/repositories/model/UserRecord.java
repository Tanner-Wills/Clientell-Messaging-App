package com.kenzie.appserver.repositories.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.util.List;
import java.util.Objects;

@DynamoDBTable(tableName = "Users")
public class UserRecord {

    private String userId;
    private String name;
    private List<String> threads;

    @DynamoDBHashKey(attributeName = "userId")
    public String getUserId() {
        return userId;
    }

    @DynamoDBAttribute(attributeName = "name")
    public String getName() {
        return name;
    }

    @DynamoDBAttribute(attributeName = "threads")
    public List<String> getThreads() {
        return threads;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setThreads(List<String> threads) {
        this.threads = threads;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserRecord record = (UserRecord) o;
        return Objects.equals(userId, record.userId) && Objects.equals(name, record.name) &&
                Objects.equals(threads, record.threads);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}

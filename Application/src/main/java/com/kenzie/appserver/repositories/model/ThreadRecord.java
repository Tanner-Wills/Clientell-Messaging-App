package com.kenzie.appserver.repositories.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.util.List;
import java.util.Objects;


@DynamoDBTable(tableName = "Threads")
public class ThreadRecord {

    private String threadId;
    private String threadTitle;
    private List<String> users;
    private List<String> messages;

    @DynamoDBHashKey(attributeName = "threadId")
    public String getThreadId() {
        return threadId;
    }

    @DynamoDBAttribute(attributeName = "threadTitle")
    public String getThreadTitle() {
        return threadTitle;
    }

    // TODO: confirm this list is for userId, not name
    @DynamoDBAttribute(attributeName = "users")
    public List<String> getUsers() {
        return users;
    }

    @DynamoDBAttribute(attributeName = "messages")
    public List<String> getMessages() {
        return messages;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public void setThreadTitle(String threadTitle) {
        this.threadTitle = threadTitle;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ThreadRecord that = (ThreadRecord) o;
        return Objects.equals(threadId, that.threadId) && Objects.equals(threadTitle, that.threadTitle) &&
                Objects.equals(users, that.users) && Objects.equals(messages, that.messages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(threadId, threadTitle, users, messages);
    }
}

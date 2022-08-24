package com.kenzie.appserver.repositories.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.util.Objects;

@DynamoDBTable(tableName = "Messages")
public class MessageRecord {

    private String messageId;
    private String timeStamp;
    private String threadId;
    private String sender;
    private String message;

    @DynamoDBHashKey(attributeName = "messageId")
    public String getMessageId() {
        return messageId;
    }

    @DynamoDBAttribute(attributeName = "timeStamp")
    public String getTimeStamp() {
        return timeStamp;
    }

    @DynamoDBAttribute(attributeName = "threadId")
    public String getThreadId() {
        return threadId;
    }

    @DynamoDBAttribute(attributeName = "sender")
    public String getSender() {
        return sender;
    }

    @DynamoDBAttribute(attributeName = "message")
    public String getMessage() {
        return message;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MessageRecord that = (MessageRecord) o;
        return Objects.equals(messageId, that.messageId) && Objects.equals(timeStamp, that.timeStamp) &&
                Objects.equals(threadId, that.threadId) && Objects.equals(sender, that.sender) &&
                Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, timeStamp, threadId, sender, message);
    }
}

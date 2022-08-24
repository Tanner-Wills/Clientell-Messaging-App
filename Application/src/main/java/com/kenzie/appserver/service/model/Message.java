package com.kenzie.appserver.service.model;


public class Message {

    private String messageId;
    private String timeStamp;
    private String threadId;
    private String sender;
    private String message;

    public Message(String messageId, String timeStamp, String threadId, String sender, String message) {
        this.messageId = messageId;
        this.timeStamp = timeStamp;
        this.threadId = threadId;
        this.sender = sender;
        this.message = message;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

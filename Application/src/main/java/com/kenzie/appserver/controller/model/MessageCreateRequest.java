package com.kenzie.appserver.controller.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;


public class MessageCreateRequest {

    @NotEmpty
    @JsonProperty("threadId")
    private String threadId;

    @NotEmpty
    @JsonProperty("sender")
    private String sender;

    @NotEmpty
    @JsonProperty("message")
    private String message;

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

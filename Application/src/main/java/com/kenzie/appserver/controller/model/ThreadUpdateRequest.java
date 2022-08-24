package com.kenzie.appserver.controller.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import javax.validation.constraints.NotEmpty;

public class ThreadUpdateRequest {

    @NotEmpty
    @JsonProperty("threadId")
    private String threadId;

    @NotEmpty
    @JsonProperty("threadTitle")
    private String threadTitle;

    @JsonProperty("users")
    private List<String> users;

    @JsonProperty("messages")
    private List<String> messages;

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public String getThreadTitle() {
        return threadTitle;
    }

    public void setThreadTitle(String threadTitle) {
        this.threadTitle = threadTitle;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }
}

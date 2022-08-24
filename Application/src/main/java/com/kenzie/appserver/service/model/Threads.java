package com.kenzie.appserver.service.model;

import java.util.List;

public class Threads {
    private String threadId;
    private String threadTitle;
    private List<String> users;
    private List<String> messages;

    public Threads(String threadId, String threadTitle, List<String> users, List<String> messages) {
        this.threadId = threadId;
        this.threadTitle = threadTitle;
        this.users = users;
        this.messages = messages;
    }

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

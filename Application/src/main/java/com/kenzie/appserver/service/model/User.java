package com.kenzie.appserver.service.model;

import java.util.List;

public class User {
    private String userId;
    private String name;
    private List<String> threads;

    public User(String userId, String name, List<String> threads) {
        this.userId = userId;
        this.name = name;
        this.threads = threads;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getThreads() {
        return threads;
    }

    public void setThreads(List<String> threads) {
        this.threads = threads;
    }
}

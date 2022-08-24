package com.kenzie.appserver.Exceptions;

public class InvalidUserCountException extends Exception {

    public InvalidUserCountException() {
    }
    public InvalidUserCountException(String message) {
        super(message);
    }
    public InvalidUserCountException(String message, Throwable err) {
        super(message, err);
    }
}

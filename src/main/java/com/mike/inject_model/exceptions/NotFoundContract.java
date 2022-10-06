package com.mike.inject_model.exceptions;

public abstract class NotFoundContract extends Exception {
    public NotFoundContract(String message) {
        super(message);
    }
}

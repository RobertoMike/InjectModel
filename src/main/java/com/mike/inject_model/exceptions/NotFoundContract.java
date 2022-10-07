package com.mike.inject_model.exceptions;

public abstract class NotFoundContract extends RuntimeException {
    public NotFoundContract(String message) {
        super(message);
    }
}

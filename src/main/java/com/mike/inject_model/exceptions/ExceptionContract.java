package com.mike.inject_model.exceptions;

/**
 * Basic class for exceptions
 */
public abstract class ExceptionContract extends RuntimeException {
    public ExceptionContract(String message) {
        super(message);
    }
}

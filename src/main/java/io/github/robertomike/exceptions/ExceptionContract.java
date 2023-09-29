package io.github.robertomike.exceptions;

/**
 * Basic class for exceptions
 */
public abstract class ExceptionContract extends RuntimeException {

    /**
     * @param message used to specify error
     */
    public ExceptionContract(String message) {
        super(message);
    }

    /**
     * @param message used to specify error
     */
    public ExceptionContract(String message, Exception e) {
        super(message, e);
    }
}

package io.github.injectmodel.exceptions;

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
}

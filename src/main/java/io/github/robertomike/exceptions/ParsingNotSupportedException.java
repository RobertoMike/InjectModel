package io.github.robertomike.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ParsingNotSupportedException extends ExceptionContract {
    /**
     * @param message used to specify error
     */
    public ParsingNotSupportedException(String message) {
        super(message);
    }
}

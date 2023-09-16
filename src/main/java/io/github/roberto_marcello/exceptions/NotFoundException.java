package io.github.roberto_marcello.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends ExceptionContract {

    /**
     * @param reason used to specify error
     */
    public NotFoundException(String reason) {
        super(reason);
    }
}

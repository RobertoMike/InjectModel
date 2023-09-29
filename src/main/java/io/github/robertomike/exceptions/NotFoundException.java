package io.github.robertomike.exceptions;

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
    /**
     * @param reason used to specify error
     */
    public NotFoundException(String reason, Exception e) {
        super(reason, e);
    }
}

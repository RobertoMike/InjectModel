package io.roberto_marcello.injectmodel.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ParamNotFoundException extends ExceptionContract {

    /**
     * @param reason used to specify error
     */
    public ParamNotFoundException(String reason) {
        super(reason);
    }
}
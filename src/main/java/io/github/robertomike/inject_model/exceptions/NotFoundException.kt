package io.github.robertomike.inject_model.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * Exception
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
class NotFoundException @JvmOverloads constructor(message: String, e: Throwable? = null) : ExceptionContract(message, e)
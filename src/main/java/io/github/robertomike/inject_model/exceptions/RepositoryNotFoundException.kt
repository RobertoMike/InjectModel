package io.github.robertomike.inject_model.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus


@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
class RepositoryNotFoundException(message: String, e: Throwable? = null) : ExceptionContract(message, e)
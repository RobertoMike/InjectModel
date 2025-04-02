package io.github.robertomike.inject_model.exceptions

/**
 * Basic class for exceptions
 */
abstract class ExceptionContract(message: String, e: Throwable? = null) : RuntimeException(message, e)

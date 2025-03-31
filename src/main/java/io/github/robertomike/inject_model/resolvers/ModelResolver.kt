package io.github.robertomike.inject_model.resolvers

import io.github.robertomike.inject_model.configs.InjectModelProperties
import io.github.robertomike.inject_model.drivers.ModelDriverResolver
import io.github.robertomike.inject_model.drivers.SpringModelDriverResolver
import io.github.robertomike.inject_model.exceptions.ExceptionContract
import io.github.robertomike.inject_model.exceptions.NotFoundException
import io.github.robertomike.inject_model.exceptions.ParamNotFoundException
import io.github.robertomike.inject_model.utils.ResolverPathUtil
import org.springframework.context.ApplicationContext
import org.springframework.web.context.request.NativeWebRequest
import java.util.*
import java.util.function.Function

abstract class ModelResolver(applicationContext: ApplicationContext, properties: InjectModelProperties) {
    private val resolverDriver: ModelDriverResolver<*> =
        SpringModelDriverResolver(applicationContext, properties)

    init {
        resolverDriver.load()
    }

    companion object {
        /**
         * exception set custom exception for NotFoundException
         */
        @JvmStatic
        var notFoundContract: Class<out ExceptionContract> = NotFoundException::class.java
    }


    /**
     * @param result   result from repository
     * @param nullable if the result can be nullable and not throw error
     * @param message  error message
     * @param model    the name of model
     * @return return the final object unwrapped from optional
     * @throws Exception emit exception if model is empty and nullable is false
     */
    fun checkAndReturnValue(result: Any?, nullable: Boolean, message: String, model: String): Any? {
        if (result is Optional<*>) {

            if (result.isPresent) {
                return result.get()
            }

            if (nullable) {
                return null
            }

            throw notFound(errorMessage(message, model))
        }

        if (result == null && !nullable) {
            throw notFound(errorMessage(message, model))
        }

        return result
    }

    /**
     * @param message message to show on throw errors
     * @param model   simple name model
     * @return custom message
     */
    private fun errorMessage(message: String, model: String): String {
        return message.replace("[model]", model)
    }


    /**
     * @param pathNameVariable      name of the variable on the path
     * @param paramType      parameter type (Model)
     * @param transformValue transform the current value with custom lambda function
     * @param request        the current request to search value
     * @param method         method to search
     * @param model          the name of model
     * @return return the result of searching
     * @throws Exception emit exception if model is empty and nullable is false
     */
    @Throws(Exception::class)
    fun getModelResultFromRequest(
        pathNameVariable: String,
        paramType: Class<*>,
        transformValue: Function<String, String>,
        request: NativeWebRequest,
        method: String,
        model: Class<*>
    ): Any? {
        val id = ResolverPathUtil.resolveVariable(request, pathNameVariable)
            ?: throw ParamNotFoundException("Param '$pathNameVariable' not found")

        return resolverDriver.resolveModel(model, method, transformValue.apply(id), paramType)
    }

    /**
     * @param message for exception
     * @return exception
     * @throws Exception if something doesn't work
     */
    fun notFound(message: String?): ExceptionContract {
        return notFoundContract.getConstructor(String::class.java).newInstance(message)
    }
}
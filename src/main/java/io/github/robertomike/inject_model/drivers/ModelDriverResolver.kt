package io.github.robertomike.inject_model.drivers

import io.github.robertomike.inject_model.configs.InjectModelProperties
import io.github.robertomike.inject_model.exceptions.ParsingNotSupportedException
import io.github.robertomike.inject_model.exceptions.RepositoryNotFoundException
import org.springframework.context.ApplicationContext
import org.springframework.core.convert.support.DefaultConversionService
import org.springframework.util.ClassUtils
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.*

abstract class ModelDriverResolver<M>(val applicationContext: ApplicationContext, val properties: InjectModelProperties) {
    /**
     * Get all the generics from the current class
     */
    private fun getGenerics(): Array<Type> {
        return (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments
    }

    /**
     * Get the class of repository from generics
     */
    fun getResolvedClass(): Class<M> {
        return (getGenerics()[0] as ParameterizedType).rawType as Class<M>
    }

    /**
     * Used to resolve or load anything necessary before using the driver
     */
    open fun load() {
    }


    /**
     * search repository from model class if empty throw error
     *
     * @param model the class of searched model from type of param
     * @param method searched method
     * @param value the value from the url
     * @param paramType type of param
     * @return method for searching on repository
     */
    @Throws(Exception::class)
    abstract fun resolveModel(
        model: Class<*>,
        method: String,
        value: String,
        paramType: Class<*>
    ): Any?

    /**
     * @param instance  current instance of type repository
     * @param method    searched method
     * @param paramType type of param
     * @return method for searching on repository
     */
    @Throws(NoSuchMethodException::class)
    fun getMethod(instance: Any, method: String, paramType: Class<*>): Method {
        val clazz = instance.javaClass

        return try {
            ClassUtils.getMethodIfAvailable(clazz, method, paramType) ?:
                clazz.getMethod(method, Any::class.java)
        } catch (e: Exception) {
            clazz.methods.find { it.name == method && it.parameterCount == 1 } ?: throw RepositoryNotFoundException(method, e)
        }
    }

    /**
     * @param value value to parse
     * @param paramType class used to parse the value
     * @return object parsed
     */
    fun parse(value: String, paramType: Class<*>): Any {
        return DefaultConversionService().convert(value, paramType) ?: throw ParsingNotSupportedException("The type '$paramType' is not supported.");
    }
}
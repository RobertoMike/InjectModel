package io.github.robertomike.inject_model.resolvers.annotations

import kotlin.reflect.KClass

/**
 * Class for a search model from repository
 */
@Target(AnnotationTarget.TYPE_PARAMETER, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.TYPE)
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class InjectModel(
    /**
     * @return name of path variable, if is empty use name of param
     */
    val value: String = "",

    /**
     * @return method on repository
     */
    val method: String = "findById",

    /**
     * @return it can be nullable
     */
    val nullable: Boolean = false,

    /**
     * @return message for not found exception
     */
    val message: String = "Model [model] not found",

    /**
     * @return type of param to search
     */
    val paramType: KClass<*> = Long::class,
)

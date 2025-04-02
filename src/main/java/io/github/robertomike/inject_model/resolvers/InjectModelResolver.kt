package io.github.robertomike.inject_model.resolvers

import io.github.robertomike.inject_model.drivers.ModelDriverResolver
import io.github.robertomike.inject_model.resolvers.annotations.InjectModel
import org.springframework.core.MethodParameter
import org.springframework.lang.NonNull
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

/**
 * A custom [HandlerMethodArgumentResolver] that resolves method arguments annotated with [InjectModel].
 *
 * This resolver uses a [ModelDriverResolver] to search for a repository method that matches the
 * annotated parameter's type and name.
 *
 * @param driver the [ModelDriverResolver] instance used to resolve the repository method
 */
@Component
class InjectModelResolver(driver: ModelDriverResolver<*>) : ModelResolver(driver), HandlerMethodArgumentResolver {
    /**
     * @param methodParameter to inspect if the parameter has injected model annotation
     * @return a boolean
     */
    override fun supportsParameter(methodParameter: MethodParameter): Boolean {
        return methodParameter.hasParameterAnnotation(InjectModel::class.java)
    }

    /**
     * @param parameter     annotated parameter
     * @param mavContainer  model and view container
     * @param webRequest    native web request
     * @param binderFactory binder factory
     * @return the searched object (Model) from repository with the method and path
     * @throws Exception of wrong use of annotation
     */
    @Throws(Exception::class)
    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        @NonNull webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {
        val annot = checkNotNull(parameter.getParameterAnnotation(InjectModel::class.java))

        val model = parameter.parameterType.simpleName

        val name = annot.value.trim()

        val namePathVariable = if (!StringUtils.isEmpty(name)) name else parameter.parameterName!!

        val result = getModelResultFromRequest(
            namePathVariable,
            annot.paramType.java,
            { it },
            webRequest,
            annot.method,
            parameter.parameterType
        )

        return checkAndReturnValue(result, annot.nullable, annot.message, model)
    }
}
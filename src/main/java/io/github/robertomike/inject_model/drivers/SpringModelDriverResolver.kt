package io.github.robertomike.inject_model.drivers

import io.github.robertomike.inject_model.configs.InjectModelProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils

/**
 * A driver that resolves models using the Spring framework.
 *
 * This driver is used by default when the `inject-model.driver` property is set to "default".
 *
 * It uses the `InjectModelProperties` to determine the suffix to use when searching for repositories.
 *
 * @see InjectModelProperties
 * @see ModelDriverResolver
 */
@Component
@ConditionalOnMissingBean
@ConditionalOnProperty("inject-model.driver", havingValue = "default", matchIfMissing = true)
class SpringModelDriverResolver(applicationContext: ApplicationContext, properties: InjectModelProperties) :
    ModelDriverResolver<Class<*>>(applicationContext, properties) {

    companion object {
        /**
         * A map of alternative names for models.
         */
        @JvmStatic
        val alternativeNames = mutableMapOf<String, String>()
    }

    /**
     * Resolves a model using the given method and value.
     *
     * @param model the class of the model to resolve
     * @param method the method to use to resolve the model
     * @param value the value to use to resolve the model
     * @param paramType the type of the parameter
     * @return the resolved model, or null if not found
     */
    override fun resolveModel(model: Class<*>, method: String, value: String, paramType: Class<*>): Any? {
        val modelName = StringUtils.uncapitalize(model.simpleName)
        var resolverName = modelName + properties.suffix

        if (alternativeNames.containsKey(modelName)) {
            resolverName = alternativeNames[modelName]!!
        }

        val resolver = applicationContext.getBean(resolverName)

        val callable = getMethod(resolver, method, paramType)
        return callable.invoke(resolver, parse(value, paramType))
    }
}
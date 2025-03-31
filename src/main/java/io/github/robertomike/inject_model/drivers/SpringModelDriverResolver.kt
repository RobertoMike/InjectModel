package io.github.robertomike.inject_model.drivers

import io.github.robertomike.inject_model.configs.InjectModelProperties
import org.springframework.context.ApplicationContext
import org.springframework.util.StringUtils

class SpringModelDriverResolver(applicationContext: ApplicationContext, properties: InjectModelProperties): ModelDriverResolver<Class<*>>(applicationContext, properties) {
    override fun resolveModel(model: Class<*>, method: String, value: String, paramType: Class<*>): Any? {
        val resolver = applicationContext.getBean(StringUtils.uncapitalize(model.simpleName) + properties.suffix)

        val callable = getMethod(resolver, method, paramType)
        return callable.invoke(resolver, parse(value, paramType))
    }
}
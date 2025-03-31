package io.github.robertomike.inject_model.configs

import io.github.robertomike.inject_model.InjectModelApplication
import io.github.robertomike.inject_model.resolvers.InjectModelResolver
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@ComponentScan(
    basePackages = ["io.github.robertomike.inject_model"],
    excludeFilters = [ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = [InjectModelApplication::class])]
)
open class InjectModelAutoConfiguration(private var applicationContext: ApplicationContext): WebMvcConfigurer {

    /**
     * @param argumentResolvers for register the inject model
     */
    override fun addArgumentResolvers(argumentResolvers: MutableList<HandlerMethodArgumentResolver?>) {
        argumentResolvers.add(injectModel())
    }

    /**
     * @return create an inject model with application context
     */
    private fun injectModel(): HandlerMethodArgumentResolver {
        return applicationContext.getBean(InjectModelResolver::class.java)
    }
}
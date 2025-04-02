package io.github.robertomike.inject_model.configs

import io.github.robertomike.inject_model.InjectModelApplication
import io.github.robertomike.inject_model.drivers.ModelDriverResolver
import io.github.robertomike.inject_model.drivers.SpringModelDriverResolver
import io.github.robertomike.inject_model.resolvers.InjectModelResolver
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


/**
 * Auto Configuration class for the Inject Model library
 */
@Configuration
@ComponentScan(
    basePackages = ["io.github.robertomike.inject_model"],
    excludeFilters = [ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = [InjectModelApplication::class])]
)
open class InjectModelAutoConfiguration(private var resolver: InjectModelResolver): WebMvcConfigurer {

    /**
     * @param argumentResolvers for register the inject model
     */
    override fun addArgumentResolvers(argumentResolvers: MutableList<HandlerMethodArgumentResolver?>) {
        argumentResolvers.add(resolver)
    }

    @Bean
    @ConditionalOnMissingBean(ModelDriverResolver::class)
    open fun defaultBean(driver: SpringModelDriverResolver): SpringModelDriverResolver {
        return driver
    }
}
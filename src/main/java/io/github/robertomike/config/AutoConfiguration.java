package io.github.robertomike.config;

import io.github.robertomike.InjectModelApplication;
import io.github.robertomike.resolvers.InjectModelResolver;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Class for automatic injection on argument resolvers
 */
@AllArgsConstructor
@Configuration
@ComponentScan(basePackages = "io.github.robertomike", excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = InjectModelApplication.class)
})
@Log4j2
public class AutoConfiguration implements WebMvcConfigurer {

    ApplicationContext applicationContext;

    /**
     * @param argumentResolvers for register the inject model
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        log.info("Adding inject model to arguments resolvers");
        argumentResolvers.add(injectModel());
    }

    /**
     * @return create an inject model with application context
     */
    private HandlerMethodArgumentResolver injectModel() {
        return applicationContext.getBean(InjectModelResolver.class);
    }
}

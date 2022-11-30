package io.roberto_marcello.injectmodel.config;

import io.roberto_marcello.injectmodel.resolvers.InjectModelResolver;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Class for automatic injection on argument resolvers
 */
@AllArgsConstructor
@Configuration
public class AutoConfiguration implements WebMvcConfigurer {

    ApplicationContext applicationContext;

    /**
     * @param argumentResolvers for register the inject model
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(injectModel());
    }

    /**
     * @return create an inject model with application context
     */
    private HandlerMethodArgumentResolver injectModel() {
        return applicationContext.getBean(InjectModelResolver.class);
    }
}

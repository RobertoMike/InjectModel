package io.github.robertomike.resolvers;

import io.github.robertomike.resolvers.annotations.InjectModel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Class for resolver the value
 */
@Component
@Slf4j
@AllArgsConstructor
public class InjectModelResolver extends ModelResolver implements HandlerMethodArgumentResolver {

    public static CustomLambda transformValue = (value -> value);

    /**
     * @param methodParameter to inspect if the parameter has inject model annotation
     * @return a boolean
     */
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.hasParameterAnnotation(InjectModel.class);
    }

    /**
     * @param parameter     annotated parameter
     * @param mavContainer  model and view container
     * @param webRequest    native web request
     * @param binderFactory binder factory
     * @return the searched object (Model) from repository with the method and path
     * @throws Exception of wrong use of annotation
     */
    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            @NonNull NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) throws Exception {
        InjectModel annot = parameter.getParameterAnnotation(InjectModel.class);

        assert annot != null;

        String model = parameter.getParameterType().getSimpleName();

        Object result = getModelResultFromRequest(
                annot.value(),
                parameter.getParameterName(),
                annot.paramType(),
                transformValue,
                webRequest,
                annot.method(),
                parameter.getParameterType()
        );

        return checkAndReturnValue(result, annot.nullable(), annot.message(), model);
    }
}

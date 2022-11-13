package com.mike.inject_model.resolvers;

import com.mike.inject_model.resolvers.annotations.InjectModel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

@Component
@Slf4j
@AllArgsConstructor
public class InjectModelResolver extends ModelResolver implements HandlerMethodArgumentResolver {

    HttpServletRequest request;
    public static CustomLambda transformValue = (value -> value);

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.hasParameterAnnotation(InjectModel.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, @NonNull NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        InjectModel annot = parameter.getParameterAnnotation(InjectModel.class);

        assert annot != null;

        String model = getNameModelFromClass(parameter.getGenericParameterType());

        Object result = getModelResultFromRequest(
                annot.nullable(),
                annot.value(),
                parameter.getParameterName(),
                annot.paramType(),
                transformValue,
                request,
                annot.method(),
                model
        );

        return checkAndReturnValue(result, annot.nullable(), annot.message(), model);
    }
}

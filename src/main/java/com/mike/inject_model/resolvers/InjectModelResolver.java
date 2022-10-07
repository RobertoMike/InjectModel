package com.mike.inject_model.resolvers;

import com.mike.inject_model.exceptions.NotFoundContract;
import com.mike.inject_model.exceptions.NotFoundException;
import com.mike.inject_model.resolvers.annotations.InjectModel;
import com.mike.inject_model.utils.ResolverPathUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
@Slf4j
@AllArgsConstructor
public class InjectModelResolver implements HandlerMethodArgumentResolver {

    HttpServletRequest request;
    ApplicationContext applicationContext;
    protected static Class<? extends NotFoundContract> notFoundContract = NotFoundException.class;

    public static Set<Class<? extends JpaRepository>> list;
    private static String[] packagePaths;

    private static String suffixRepository = "Repository";

    public static void setNotFoundException(Class<NotFoundContract> exception) {
        InjectModelResolver.notFoundContract = exception;
    }

    public static void setSuffixRepository(String suffixRepository) {
        InjectModelResolver.suffixRepository = suffixRepository;
    }

    public static void setPackagePath(String... packagePaths) {
        InjectModelResolver.packagePaths = packagePaths;
    }

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.hasParameterAnnotation(InjectModel.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, @NonNull NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        InjectModel annot = parameter.getParameterAnnotation(InjectModel.class);

        assert annot != null;

        if (list == null) {
            Reflections reflections = new Reflections(new ConfigurationBuilder().forPackages(packagePaths));
            list = reflections.getSubTypesOf(JpaRepository.class);
        }

        String[] packages = parameter.getGenericParameterType().toString().split("[.]");
        String model = packages[packages.length - 1];

        Optional<Class<? extends JpaRepository>> repository = list
                .stream()
                .filter((classes) -> Arrays.stream(packagePaths).anyMatch(packagePath ->
                        classes.getName().contains(
                                packagePath + "." + model + suffixRepository
                        )
                )).findFirst();

        if (repository.isEmpty()) {
            if (annot.nullable()) {
                return null;
            }
            throw new Exception("Repository '" + model + suffixRepository + "' not found for model: " + parameter.getGenericParameterType());
        }

        Optional<String> id = new ResolverPathUtil(request).resolveVariable(annot.value());

        if (id.isEmpty()) {
            if (annot.nullable()) {
                return null;
            }
            throw new Exception("Param '" + annot.value() + "' not found");
        }

        Class<?> paramType = annot.paramType();

        Object instance = applicationContext.getBean(repository.get());
        Method callable = instance.getClass().getMethod(annot.method(), paramType);
        Object result = callable.invoke(instance, parse(id.get(), paramType));

        if (result instanceof Optional resultOptional) {
            if (resultOptional.isEmpty()) {
                if (annot.nullable()) {
                    return null;
                }
                throw notFound(annot.message());
            }
            return resultOptional.get();
        }

        if (result == null && !annot.nullable()) {
            throw notFound(annot.message());
        }

        return result;
    }

    public Object parse(String value, Class<?> paramType) throws Exception {
        try {
            if (paramType.equals(Long.class)) {
                return Long.parseLong(value);
            }
            if (paramType.equals(String.class)) {
                return value;
            }
            if (paramType.equals(UUID.class)) {
                return UUID.fromString(value);
            }

            throw new Exception("Class " + paramType + " not supported");
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw notFound("Model not found");
        }
    }

    public Exception notFound(String message) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return notFoundContract.getConstructor(String.class).newInstance(message);
    }
}

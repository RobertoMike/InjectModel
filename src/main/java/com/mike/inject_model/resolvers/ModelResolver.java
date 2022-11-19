package com.mike.inject_model.resolvers;

import com.mike.inject_model.exceptions.NotFoundContract;
import com.mike.inject_model.exceptions.NotFoundException;
import com.mike.inject_model.exceptions.ParamNotFoundException;
import com.mike.inject_model.exceptions.RepositoryNotFoundException;
import com.mike.inject_model.utils.ResolverPathUtil;
import lombok.Setter;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.Repository;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

@Setter
@SuppressWarnings("ALL")
public abstract class ModelResolver {

    public static Set<Class<? extends Repository>> list;
    protected static String[] packagePaths;
    @Autowired
    ApplicationContext applicationContext;

    protected static String suffixRepository = "Repository";

    protected static Class<? extends NotFoundContract> notFoundContract = NotFoundException.class;

    public static void setNotFoundException(Class<NotFoundContract> exception) {
        InjectModelResolver.notFoundContract = exception;
    }

    /**
     * @param packagePaths
     */
    public static void setPackagePaths(String... packagePaths) {
        ModelResolver.packagePaths = packagePaths;
    }

    /**
     * @return all packages paths registered on model resolver
     */
    public static String[] getPackagePaths() {
        return ModelResolver.packagePaths;
    }

    /**
     * to search repositories if list is empty
     */
    public void loadRepositories() {
        if (list == null) {
            Reflections reflections = new Reflections(new ConfigurationBuilder().forPackages(packagePaths));
            list = reflections.getSubTypesOf(Repository.class);
        }
    }

    /**
     * @param model
     * @return
     */
    public String getNameModelFromClass(Type model) {
        String[] packages = model.toString().split("[.]");
        return packages[packages.length - 1];
    }

    /**
     * @param model
     * @return
     * @throws Exception
     */
    public Class<? extends Repository> findRepositoryByModel(String model) throws Exception {
        loadRepositories();

        Optional<Class<? extends Repository>> repository = list
                .stream()
                .filter((classes) -> Arrays.stream(packagePaths).anyMatch(packagePath ->
                        classes.getName().contains(
                                packagePath + "." + model + suffixRepository
                        )
                )).findFirst();

        if (repository.isEmpty()) {
            throw new RepositoryNotFoundException("Repository '" + model + suffixRepository + "' not found for model: " + model);
        }

        return repository.get();
    }

    /**
     * @param suffixRepository
     */
    public static void setSuffixRepository(String suffixRepository) {
        ModelResolver.suffixRepository = suffixRepository;
    }

    /**
     * @param value
     * @param paramType
     * @return
     * @throws Exception
     */
    public Object parse(String value, Class<?> paramType) throws Exception {
        try {
            if (paramType.equals(Long.class)) {
                return Long.parseLong(value);
            }

            if (paramType.equals(Integer.class)) {
                return Integer.parseInt(value);
            }

            if (paramType.equals(String.class)) {
                return value;
            }

            if (paramType.equals(UUID.class)) {
                return UUID.fromString(value);
            }

            throw new Exception("Class " + paramType + " not supported");
        } catch (Exception e) {
            throw new Exception("Type of value not supported for " + paramType);
        }
    }

    /**
     * @param method
     * @param value
     * @param paramType
     * @param repository
     * @return
     * @throws Exception find the model
     */
    public Object findModel(
            String method,
            String value,
            Class<?> paramType,
            Class<? extends Repository> repository
    ) throws Exception {
        Repository instance = applicationContext.getBean(repository);
        Method callable = getMethod(instance, method, paramType);
        return callable.invoke(instance, parse(value, paramType));
    }

    /**
     * @param method
     * @param value
     * @param paramType
     * @param model
     * @return
     * @throws Exception find the model and repository
     */
    public Object findModel(
            String method,
            String value,
            Class<?> paramType,
            String model
    ) throws Exception {
        return findModel(
                method,
                value,
                paramType,
                findRepositoryByModel(
                        model
                )
        );
    }

    /**
     * @param result   result from repository
     * @param nullable if the result can be nullable and not throw error
     * @param message  error message
     * @param model
     * @return return the final object unwrapped from optional
     * @throws Exception
     */
    public Object checkAndReturnValue(Object result, boolean nullable, String message, String model) throws Exception {
        if (result instanceof Optional resultOptional) {
            if (resultOptional.isEmpty()) {
                if (nullable) {
                    return null;
                }
                throw notFound(errorMessage(message, model));
            }
            return resultOptional.get();
        }

        if (result == null && !nullable) {
            throw notFound(errorMessage(message, model));
        }

        return result;
    }

    /**
     * @param message
     * @param model
     * @return custom message
     */
    public String errorMessage(String message, String model) {
        return message.replace("[model]", model);
    }


    /**
     * @param nullable       if returned value can be null
     * @param nameValue      name of value annotation
     * @param nameParameter  name of parameter
     * @param paramType      parameter type (Model)
     * @param transformValue transform the current value with custom lambda function
     * @param request        the current request to search value
     * @param method         method to search
     * @param model          the name of model
     * @return return the result of searching
     * @throws Exception
     */
    @Nullable
    public Object getModelResultFromRequest(
            boolean nullable,
            String nameValue,
            String nameParameter,
            Class<?> paramType,
            CustomLambda transformValue,
            HttpServletRequest request,
            String method,
            String model
    ) throws Exception {
        String namePathVariable = !Objects.equals(nameValue, "") ? nameValue : nameParameter;

        Optional<String> id = new ResolverPathUtil(request).resolveVariable(
                namePathVariable
        );

        if (id.isEmpty()) {
            throw new ParamNotFoundException("Param '" + namePathVariable + "' not found");
        }

        return findModel(
                method,
                transformValue.apply(id.get()),
                paramType,
                model
        );
    }

    /**
     * @param instance  current instance of type repository
     * @param method    searched method
     * @param paramType type of param
     * @return method for searching on repository
     * @throws NoSuchMethodException
     */
    public Method getMethod(Repository instance, String method, Class<?> paramType) throws NoSuchMethodException {
        try {
            return instance.getClass().getMethod(method, paramType);
        } catch (Exception e) {
            return instance.getClass().getMethod(method, Object.class);
        }
    }

    /**
     * @param message
     * @return exception
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public NotFoundContract notFound(String message) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return notFoundContract.getConstructor(String.class).newInstance(message);
    }

    public interface CustomLambda {
        String apply(String value) throws Exception;
    }
}

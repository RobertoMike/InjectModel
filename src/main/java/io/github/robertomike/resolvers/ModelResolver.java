package io.github.robertomike.resolvers;

import io.github.robertomike.exceptions.ExceptionContract;
import io.github.robertomike.exceptions.NotFoundException;
import io.github.robertomike.exceptions.ParamNotFoundException;
import io.github.robertomike.exceptions.RepositoryNotFoundException;
import io.github.robertomike.utils.ResolverPathUtil;
import lombok.Setter;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.Repository;
import org.springframework.web.context.request.NativeWebRequest;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Class with basic methods for resolvers to be extended
 */
@Setter
@SuppressWarnings("ALL")
public abstract class ModelResolver {

    public static Set<Class<? extends Repository>> list;
    protected static String[] packagePaths;
    @Autowired
    ApplicationContext applicationContext;

    protected static String suffixRepository = "Repository";

    protected static Class<? extends ExceptionContract> notFoundContract = NotFoundException.class;

    /**
     * @param exception set custom exception for NotFoundException
     */
    public static void setNotFoundException(Class<ExceptionContract> exception) {
        notFoundContract = exception;
    }

    /**
     * @param packagePaths for settings package paths
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
     * @param suffixRepository set suffix repository when search with model name
     */
    public static void setSuffixRepository(String suffixRepository) {
        ModelResolver.suffixRepository = suffixRepository;
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
     * @param model simple name of class
     * @return repository from model simple name
     * @throws Exception emit exception if repository is not founded
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
     * @param value     value to parse
     * @param paramType class used to parse the value
     * @return object parsed
     * @throws Exception can throw error if is not valid classType or invalid value to class
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
     * @param method     for repository
     * @param value      for search on repository
     * @param paramType  type of object to search
     * @param repository the repository use for search
     * @return object getted from repository
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
     * @param method    for repository
     * @param value     for search on repository
     * @param paramType type of object to search
     * @param model     the name of model
     * @return object getted from repository
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
                findRepositoryByModel(model)
        );
    }

    /**
     * @param result   result from repository
     * @param nullable if the result can be nullable and not throw error
     * @param message  error message
     * @param model    the name of model
     * @return return the final object unwrapped from optional
     * @throws Exception emit exception if model is empty and nullable is false
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
     * @param message message to show on throw errors
     * @param model   simple name model
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
     * @throws Exception emit exception if model is empty and nullable is false
     */
    @Nullable
    public Object getModelResultFromRequest(
            boolean nullable,
            String nameValue,
            String nameParameter,
            Class<?> paramType,
            CustomLambda transformValue,
            NativeWebRequest request,
            String method,
            String model
    ) throws Exception {
        String namePathVariable = !Objects.equals(nameValue, "") ? nameValue : nameParameter;

        Optional<String> id = ResolverPathUtil.resolveVariable(request, namePathVariable);

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
     * @throws NoSuchMethodException error getting method from class
     */
    public Method getMethod(Repository instance, String method, Class<?> paramType) throws NoSuchMethodException {
        try {
            return instance.getClass().getMethod(method, paramType);
        } catch (Exception e) {
            return instance.getClass().getMethod(method, Object.class);
        }
    }

    /**
     * @param message for exception
     * @return exception
     * @throws Exception if something doesn't work
     */
    public ExceptionContract notFound(String message) throws Exception {
        return notFoundContract.getConstructor(String.class).newInstance(message);
    }

    /**
     * For custom lambad
     */
    public interface CustomLambda {
        /**
         * @param value from path
         * @return transforme value
         * @throws Exception can be throw error
         */
        String apply(String value) throws Exception;
    }
}

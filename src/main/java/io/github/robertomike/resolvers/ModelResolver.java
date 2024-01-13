package io.github.robertomike.resolvers;

import io.github.robertomike.drivers.RepositoryResolverDriver;
import io.github.robertomike.drivers.SpringRepositoryResolverDriver;
import io.github.robertomike.exceptions.ExceptionContract;
import io.github.robertomike.exceptions.NotFoundException;
import io.github.robertomike.exceptions.ParamNotFoundException;
import io.github.robertomike.utils.ResolverPathUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.request.NativeWebRequest;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Class with basic methods for resolvers to be extended
 */
@Setter
@SuppressWarnings("ALL")
public abstract class ModelResolver {
    @Setter
    @Getter
    private static RepositoryResolverDriver<?> resolverDriver = new SpringRepositoryResolverDriver();
    protected static String[] packagePaths;
    @Autowired
    private ApplicationContext applicationContext;
    @Getter
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
            Class<?> model
    ) throws Exception {
        return resolverDriver.resolveModel(applicationContext, model, method, value, paramType);
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
            String nameValue,
            String nameParameter,
            Class<?> paramType,
            CustomLambda transformValue,
            NativeWebRequest request,
            String method,
            Class<?> model
    ) throws Exception {
        String namePathVariable = nameValue != null && !nameValue.trim().equals("") ? nameValue : nameParameter;

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

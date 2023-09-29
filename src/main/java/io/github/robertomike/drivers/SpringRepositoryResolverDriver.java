package io.github.robertomike.drivers;

import io.github.robertomike.exceptions.RepositoryNotFoundException;
import io.github.robertomike.resolvers.ModelResolver;
import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.Repository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SpringRepositoryResolverDriver extends RepositoryResolverDriver<Repository<?, ?>> {

    /**
     * Resolving repository using by class of the model
     */
    @Override
    public Class<? extends Repository<?, ?>> resolveRepositoryOrThrow(Class<?> modelClass) {
        loadRepositories();

        return list.stream()
                .filter(classType -> getPackagePaths().anyMatch(packagePath -> classType.getName().contains(
                        packagePath + "." + modelClass.getSimpleName() + ModelResolver.getSuffixRepository()
                )))
                .findFirst()
                .orElseThrow(() -> new RepositoryNotFoundException("Repository not found for model: " + modelClass.getSimpleName()));
    }

    /**
     * Resolving model using repository and the method
     */
    @Override
    public Object resolveModel(ApplicationContext applicationContext, Class<?> model, String method, String value, Class<?> paramType) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Repository<?, ?> repository = applicationContext.getBean(resolveRepositoryOrThrow(model));
        Method callable = getMethod(repository, method, paramType);
        return callable.invoke(repository, parse(value, paramType));
    }

    /**
     * @param instance  current instance of type repository
     * @param method    searched method
     * @param paramType type of param
     * @return method for searching on repository
     */
    public Method getMethod(Repository<?, ?> instance, String method, Class<?> paramType) throws NoSuchMethodException {
        try {
            return instance.getClass().getMethod(method, paramType);
        } catch (Exception e) {
            return instance.getClass().getMethod(method, Object.class);
        }
    }
}

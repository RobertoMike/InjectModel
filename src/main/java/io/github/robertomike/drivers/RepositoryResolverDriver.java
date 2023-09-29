package io.github.robertomike.drivers;

import io.github.robertomike.exceptions.ParsingNotSupportedException;
import io.github.robertomike.resolvers.ModelResolver;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

public abstract class RepositoryResolverDriver<R> {
    public Set<Class<? extends R>> list;
    protected Class<? extends R> repository;

    /**
     * Get all the generics from the current class
     */
    public Type[] getGenerics() {
        return ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
    }

    /**
     * Get the class of repository from generics
     */
    @SuppressWarnings("unchecked")
    public Class<R> getRepositoryClass() {
        return (Class<R>) ((ParameterizedType) getGenerics()[0]).getRawType();
    }

    /**
     * to search repositories if list is empty
     */
    public void loadRepositories() {
        if (list == null) {
            Reflections reflections = new Reflections(new ConfigurationBuilder().forPackages(ModelResolver.getPackagePaths()));
            list = reflections.getSubTypesOf(getRepositoryClass());
        }
    }

    /**
     * search repository from model class if empty throw error
     */
    public abstract Class<? extends R> resolveRepositoryOrThrow(Class<?> modelClass) throws Exception;

    /**
     * search repository from model class if empty throw error
     */
    public abstract Object resolveModel(ApplicationContext applicationContext, Class<?> model, String method, String value, Class<?> paramType) throws Exception;

    /**
     * method to facilitate the registered paths
     */
    public Stream<String> getPackagePaths() {
        return Stream.of(ModelResolver.getPackagePaths());
    }

    /**
     * @param value     value to parse
     * @param paramType class used to parse the value
     * @return object parsed
     */
    public Object parse(String value, Class<?> paramType) {
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

            throw new ParsingNotSupportedException("Class " + paramType + " not supported");
        } catch (Exception e) {
            throw new ParsingNotSupportedException("Type of value not supported for " + paramType);
        }
    }
}

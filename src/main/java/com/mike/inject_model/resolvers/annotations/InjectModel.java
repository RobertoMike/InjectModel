package com.mike.inject_model.resolvers.annotations;

import java.lang.annotation.*;

/**
 * Class for a search model from repository
 */
@Target(ElementType.PARAMETER)
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectModel {
    /**
     * @return name of path variable, if is empty use name of param
     */
    String value() default "";

    /**
     * @return if can be nullable
     */
    boolean nullable() default false;

    /**
     * @return message for not found exception
     */
    String message() default "Model [model] not found";

    /**
     * @return type of param to search
     */
    Class<?> paramType() default Long.class;

    /**
     * @return method on repository
     */
    String method() default "findById";
}

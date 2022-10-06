package com.mike.inject_model.resolvers.annotations;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectModel {
    String value();

    boolean nullable() default false;

    String message() default "Model not found";

    Class<?> paramType() default Long.class;

    String method() default "findById";
}

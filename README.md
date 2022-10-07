# Welcome to InjectModel

This library is for simplify work when we need to get model from path variable automatically, searching automatically from repositories

## Example of use

It is used in method parameters.
If repository return null or optional empty throw NotFoundException.

```java
public class controller {
    public Model nameMethod(
            @InjectModel("pathVariable") Model model
    ) {
        return model;
    }
}
```

## First configuration

For the inject model work, you need to define basic configuration.
Example:

You can define many package paths where find automatically repositories (DTOs)

Inject model search repository reading name of model and add declared suffix

Example: ModelRepository

```java
import com.mike.inject_model.exceptions.NotFoundException;
import com.mike.inject_model.resolvers.InjectModelResolver;

@Configuration
public class InjectModelConfig {
    private InjectModelConfig() {
        // Define package paths
        InjectModelResolver.setPackagePath(
                Repository.class.getPackage().getName()
        );
        // Define Suffix name of repository
        InjectModelResolver.setSuffixRepository("Repository");
        // If you want to implement your custom not exception you need to extend from NotFoundContract
        InjectModelResolver.setNotFoundException(NotFoundException.class); 
    }
}
```

## Parameters of InjectModel

```java
import com.mike.inject_model.resolvers.annotations.InjectModel;
public class Controller {
    public void method(
        @InjectModel(
                value = "path_variable",
                paramType = String.class, // default Long | supported Long | String | UUID
                message = "Message exception",
                method = "methodRepository", // default findById
                nullable = true // Permit return null value and non throw exception 
        ) Model model
    ){}
}
```

## Warning ![Warning](./warning.svg)

All methods used from InjectModel need to be declared on the repository and need to have only one parameter (path variable)
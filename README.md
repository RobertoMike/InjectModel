# Welcome to InjectModel

This library is for simplify work when we need to get model from path variable automatically, searching automatically
from repositories

## Links
- [Example of use](#example)
- [Configuration of the Resolver](#Configuration)
- [Parameters of the annotation](#Parameters)
- [Customize repository and model resolver](#Customize)
- [Warning for repository](#Warning)

##  <a id="example"></a>Example of use

It is used in method parameters.
If repository return null or optional empty throw NotFoundException.

```java
package io.github.robertomike.resolvers.InjectModel;

public class controller {
    public Model nameMethod(
            // If path variable have the same name of the var, you don't need to declare the string inside the annotation
            @InjectModel("pathVariable") Model model
    ) {
        return model;
    }
}
```

##  <a id="Configuration"></a>Configuration

For the inject model work, you need to define basic configuration.
Example:

You can define many package paths where find automatically repositories (DTOs)

Inject model search repository reading name of model and add declared suffix

Example: ModelRepository

```java
import io.github.robertomike.exceptions.NotFoundException;
import io.github.robertomike.resolvers.InjectModelResolver;

@Configuration
public class InjectModelConfig {
    private InjectModelConfig() {
        // Define package paths
        InjectModelResolver.setPackagePaths(
                Repository.class.getPackage().getName()
        );
        // This is the default value, if you want to customize define suffix name of repository
        InjectModelResolver.setSuffixRepository("Repository");
        // This is the default value, if you want to implement your custom not exception you need to extend from NotFoundContract
        InjectModelResolver.setNotFoundException(NotFoundException.class);
    }
}
```

## <a id="Parameters"></a>Parameters of InjectModel

```java
import io.github.robertomike.injectmodel.InjectModel;

public class Controller {
    public void method(
            @InjectModel(
                    value = "path_variable",
                    paramType = String.class, // default Long | supported Long, Integer, String, UUID
                    message = "Message exception",
                    method = "methodRepository", // default findById
                    nullable = true // Permit return null value and not throw exception 
            ) Model model
    ) {
    }
}
```

## <a id="Customize"></a>Can I change the way that is resolved the repository and method?
Yes, you can, you need to extend the class RepositoryResolverDriver and need to define 
in the configuration, the current and only resolver is SpringRepositoryResolverDriver

```java
import io.github.robertomike.resolvers.InjectModelResolver;
import io.github.robertomike.drivers.SpringRepositoryResolverDriver;

@Configuration
public class InjectModelConfig {
    private InjectModelConfig() {
        // Define package paths
        InjectModelResolver.setPackagePaths(
                Repository.class.getPackage().getName()
        );

        InjectModelResolver.setResolverDriver(new SpringRepositoryResolverDriver());
    }
}
```

## <a id="Warning"></a>Warning ![Warning](./warning.svg)

All methods used from InjectModel need to be declared on the repository and need to have only one parameter (path variable)

[![coffee](./buy-me-coffee.png)](https://www.buymeacoffee.com/robertomike)
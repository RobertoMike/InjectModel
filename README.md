# Welcome to InjectModel 👋

This library is for simplify work when we need to get model from path variable automatically, searching automatically
from repositories

## Links
- [Example of use](#example-of-use)
- [How to install](#how-to-install)
- [Difference](#difference)
- [Configuration](#configuration)
  - [Custom repositories for models](#custom-repositories-for-models)
  - [Custom driver](#custom-driver)
- [Parameters of the annotation](#parameters-of-injectmodel)
- [Warning for repository](#warning-)

##  Example of use

It is used in method parameters.
If repository return null or optional empty throw NotFoundException.

```java
package io.github.robertomike.inject_model.resolvers.InjectModel;

public class controller {
    @Get("/models/{model}")
    public Model nameMethod(
            // If path variable have the same name of the var, you don't need to declare the string inside the annotation
            @InjectModel() Model model
    ) {
        return model;
    }

    // Example of path variable different from variable method name  
    @Get("/models/{modelId}")
    public Model nameMethod(
            @InjectModel("modelId") Model model
    ) {
        return model;
    }
}
```

## How to install
Maven
```xml
<dependency>
    <groupId>io.github.robertomike</groupId>
    <artifactId>inject_model</artifactId>
    <version>1.0.0</version>
</dependency>
```
Gradle
```gradle
dependencies {
    implementation 'io.github.robertomike:inject_model:1.0.0'
}
```

##  Difference

<table>
<tr>
    <th>Normal</th>
    <th>Inject Model</th>
</tr>
<tr>
<td>

```java
import io.github.robertomike.exceptions.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@AllArgsConstructor
public class ExampleController {
    private ExampleRepository repository;

    @GetMapping("/examples/{exampleId}")
    public Example show(@PathVariable() Long exampleId) {
        return repository.findById(exampleId)
                .orElseThrow(() -> new NotFoundException("Model not fount for id: " + exampleId));
    }
}
```
</td>
<td>

```java
import io.github.robertomike.resolvers.annotations.InjectModel;
import org.springframework.web.bind.annotation.GetMapping;

public class ExampleController {
    // If path var is equals to name var you don't need to declare nothing
    @GetMapping("/examples/{example}")
    public Example show(@InjectModel Example example) {
        return example;
    }
}
```
</td>
</tr>
</table>


##  Configuration

For the inject model work, **_there is no needed configuration_**. 🎉

The InjectModel search the repository using the name of the class

**Example**: model User, he will search 'userRepository' bean.

If you want to use instead the _services_ you can use the property 'inject-model.suffix' and put 'Service'

### Custom repositories for models

If you models and repositories doesn't follow a standard, you can set how to find the corresponding repository in this way:

```java

import io.github.robertomike.inject_model.drivers.SpringModelDriverResolver;

@Configuration
public class InjectModelConfig {
    private InjectModelConfig() {
        // You get the map of alternatives
        Map<String, String> alternatives = SpringModelDriverResolver.getAlternativeNames();
        // Put your custom names
        alternatives.put("User", "SomeRandomNameToRepository");
        // Some more alternatives names
    }
}
```

### Custom driver

A driver is the one that search the repository from the model name class.

If you want to define a custom driver you need to extend from '_ModelDriverResolver_'

After this you can declare a bean of your driver ⚡!

Right now there are two drivers:
- SpringRepositoryReflectionDriverResolver -> deprecated
- SpringModelDriverResolver -> used by default

```java
import io.github.robertomike.inject_model.configs.InjectModelProperties;
import io.github.robertomike.inject_model.exceptions.NotFoundException;
import io.github.robertomike.inject_model.resolvers.ModelResolver;
import io.github.robertomike.inject_model.drivers.SpringRepositoryReflectionDriverResolver;
import org.springframework.context.ApplicationContext;

@Configuration
public class InjectModelConfig {
    @Bean
    public ModelDriverResolver driver(ApplicationContext context, InjectModelProperties properties) {
        // This is an example using the deprecated driver 
        SpringRepositoryReflectionDriverResolver driver = new SpringRepositoryReflectionDriverResolver();
        // Define package paths
        driver.setPackagePath(
                Repository.class.getPackage().getName()
        );
        // If you have defined a function to load necessary data
        driver.load();
        // Set necessary class to work
        driver.setProperties(context, properties);
        // This is the default value, if you want to implement your custom not exception you need to extend from NotFoundContract
        return driver;
    }
}
```

## Parameters of InjectModel

The InjectModel has many parameters that allows you to configurate the behavior

```java
import io.github.robertomike.injectmodel.InjectModel;

public class Controller {
    public void method(
            @InjectModel(
                    value = "path_variable", // This is empty by default, reading the name of variable
                    paramType = String.class, // default Long | supported Long, Integer, String, UUID
                    message = "Message exception", // Customize the error message
                    method = "methodRepository", // default method findById
                    nullable = true // Permit return null value and not throw exception 
            ) Model model
    ) {
    }
}
```

## Warning ![Warning](./warning.svg)

All methods used from InjectModel need to be declared on the repository,
need to have only one parameter (path variable) and 
return an object or optional object

The type of value returned must match what is expected


[![coffee](./buy-me-coffee.png)](https://www.buymeacoffee.com/robertomike)
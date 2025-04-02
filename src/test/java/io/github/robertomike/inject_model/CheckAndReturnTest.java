package io.github.robertomike.inject_model;

import io.github.robertomike.inject_model.drivers.SpringModelDriverResolver;
import io.github.robertomike.inject_model.exceptions.NotFoundException;
import io.github.robertomike.inject_model.resolvers.InjectModelResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class CheckAndReturnTest extends BasicTest {
    @Test
    void checkAndReturnOptionalValue() {
        InjectModelResolver injectModelResolver = new InjectModelResolver(new SpringModelDriverResolver(applicationContext, properties));

        Object result = injectModelResolver.checkAndReturnValue(
                Optional.of(5),
                false,
                "Model not found",
                "Model"
        );

        Assertions.assertEquals(
                5,
                result
        );
    }

    @Test
    void checkAndReturnOptionalEmptyNullableResult() {
        InjectModelResolver injectModelResolver = new InjectModelResolver(new SpringModelDriverResolver(applicationContext, properties));

        Object result = injectModelResolver.checkAndReturnValue(
                Optional.empty(),
                true,
                "Model not found",
                "Model"
        );

        Assertions.assertNull(result);
    }

    @Test
    void throwErrorOfEmptyOptional() {
        InjectModelResolver injectModelResolver = new InjectModelResolver(new SpringModelDriverResolver(applicationContext, properties));

        Assertions.assertThrows(
                NotFoundException.class,
                () -> injectModelResolver.checkAndReturnValue(
                        Optional.empty(),
                        false,
                        "Model not found",
                        "Model"
                ),
                "Model not found"
        );
    }

    @Test
    void throwErrorOfNullableValue() {
        InjectModelResolver injectModelResolver = new InjectModelResolver(new SpringModelDriverResolver(applicationContext, properties));

        Assertions.assertThrows(
                NotFoundException.class,
                () -> injectModelResolver.checkAndReturnValue(
                        null,
                        false,
                        "Model not found",
                        "Model"
                ),
                "Model not found"
        );
    }

    @Test
    void checkAndReturnValue() {
        InjectModelResolver injectModelResolver = new InjectModelResolver(new SpringModelDriverResolver(applicationContext, properties));

        Assertions.assertEquals(
                5,
                injectModelResolver.checkAndReturnValue(
                        5,
                        false,
                        "Model not found",
                        "Model"
                )
        );
    }
}

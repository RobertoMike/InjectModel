package io.github.robertomike;

import io.github.robertomike.exceptions.NotFoundException;
import io.github.robertomike.resolvers.InjectModelResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class CheckAndReturnTest extends BasicTest {
    @Test
    void checkAndReturnOptionalValue() throws Exception {
        InjectModelResolver injectModelResolver = new InjectModelResolver();

        Object result = injectModelResolver.checkAndReturnValue(
                Optional.of(5),
                false,
                "Model not found",
                "Model"
        );

        Assertions.assertEquals(
                result,
                5
        );
    }

    @Test
    void checkAndReturnOptionalEmptyNullableResult() throws Exception {
        InjectModelResolver injectModelResolver = new InjectModelResolver();

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
        InjectModelResolver injectModelResolver = new InjectModelResolver();

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
        InjectModelResolver injectModelResolver = new InjectModelResolver();

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
    void checkAndReturnValue() throws Exception {
        InjectModelResolver injectModelResolver = new InjectModelResolver();

        Assertions.assertEquals(
                injectModelResolver.checkAndReturnValue(
                        5,
                        false,
                        "Model not found",
                        "Model"
                ),
                5
        );
    }
}

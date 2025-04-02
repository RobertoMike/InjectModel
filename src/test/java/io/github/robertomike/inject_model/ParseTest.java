package io.github.robertomike.inject_model;

import io.github.robertomike.inject_model.drivers.ModelDriverResolver;
import io.github.robertomike.inject_model.drivers.SpringRepositoryReflectionDriverResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.UUID;

public class ParseTest extends BasicTest {
    ModelDriverResolver<?> driver = new SpringRepositoryReflectionDriverResolver(applicationContext, properties);

    @Test
    void unsupportedParse() {
        Assertions.assertThrows(
                Exception.class,
                () -> driver.parse("2", HashMap.class),
                "Class " + HashMap.class + " not supported"
        );
    }

    @Test
    void invalidValueForParse() {
        Assertions.assertThrows(
                Exception.class,
                () -> driver.parse("invalid", Long.class),
                "Type of value not supported for " + Long.class
        );
    }

    @Test
    void parseInteger() {
        Assertions.assertEquals(
                6,
                driver.parse("6", Integer.class)
        );
    }

    @Test
    void parseLong() {
        Assertions.assertEquals(
                6L,
                driver.parse("6", Long.class)
        );
    }

    @Test
    void parseUUID() {
        UUID uuid = UUID.randomUUID();
        Assertions.assertEquals(
                uuid,
                driver.parse(uuid.toString(), UUID.class)
        );
    }

    @Test
    void parseString() {
        Assertions.assertEquals(
                "valid",
                driver.parse("valid", String.class)

        );
    }
}

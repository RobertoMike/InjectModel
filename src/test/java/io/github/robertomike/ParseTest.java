package io.github.robertomike;

import io.github.robertomike.drivers.RepositoryResolverDriver;
import io.github.robertomike.drivers.SpringRepositoryResolverDriver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.UUID;

public class ParseTest extends BasicTest {

    @Test
    void unsupportedParse() {
        RepositoryResolverDriver<?> driver = new SpringRepositoryResolverDriver();

        Assertions.assertThrows(
                Exception.class,
                () -> driver.parse("2", HashMap.class),
                "Class " + HashMap.class + " not supported"
        );
    }

    @Test
    void invalidValueForParse() {
        RepositoryResolverDriver<?> driver = new SpringRepositoryResolverDriver();

        Assertions.assertThrows(
                Exception.class,
                () -> driver.parse("invalid", Long.class),
                "Type of value not supported for " + Long.class
        );
    }

    @Test
    void parseInteger() {
        RepositoryResolverDriver<?> driver = new SpringRepositoryResolverDriver();

        Assertions.assertEquals(
                6,
                driver.parse("6", Integer.class)
        );
    }

    @Test
    void parseLong() {
        RepositoryResolverDriver<?> driver = new SpringRepositoryResolverDriver();

        Assertions.assertEquals(
                6L,
                driver.parse("6", Long.class)
        );
    }

    @Test
    void parseUUID() {
        UUID uuid = UUID.randomUUID();
        RepositoryResolverDriver<?> driver = new SpringRepositoryResolverDriver();

        Assertions.assertEquals(
                uuid,
                driver.parse(uuid.toString(), UUID.class)
        );
    }

    @Test
    void parseString() {
        RepositoryResolverDriver<?> driver = new SpringRepositoryResolverDriver();

        Assertions.assertEquals(
                "valid",
                driver.parse("valid", String.class)

        );
    }
}

package io.roberto_marcello.injectmodel;

import io.roberto_marcello.injectmodel.resolvers.InjectModelResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.UUID;

public class ParseTest extends BasicTest {

    @Test
    void unsupportedParse() {
        InjectModelResolver injectModelResolver = new InjectModelResolver(request);

        Assertions.assertThrows(
                Exception.class,
                () -> injectModelResolver.parse("2", HashMap.class),
                "Class " + HashMap.class + " not supported"
        );
    }

    @Test
    void invalidValueForParse() {
        InjectModelResolver injectModelResolver = new InjectModelResolver(request);

        Assertions.assertThrows(
                Exception.class,
                () -> injectModelResolver.parse("invalid", Long.class),
                "Type of value not supported for " + Long.class
        );
    }

    @Test
    void parseInteger() throws Exception {
        InjectModelResolver injectModelResolver = new InjectModelResolver(request);

        Assertions.assertEquals(
                6,
                injectModelResolver.parse("6", Integer.class)
        );
    }

    @Test
    void parseLong() throws Exception {
        InjectModelResolver injectModelResolver = new InjectModelResolver(request);

        Assertions.assertEquals(
                6L,
                injectModelResolver.parse("6", Long.class)
        );
    }

    @Test
    void parseUUID() throws Exception {
        UUID uuid = UUID.randomUUID();
        InjectModelResolver injectModelResolver = new InjectModelResolver(request);

        Assertions.assertEquals(
                uuid,
                injectModelResolver.parse(uuid.toString(), UUID.class)
        );
    }

    @Test
    void parseString() throws Exception {
        InjectModelResolver injectModelResolver = new InjectModelResolver(request);

        Assertions.assertEquals(
                "valid",
                injectModelResolver.parse("valid", String.class)

        );
    }
}

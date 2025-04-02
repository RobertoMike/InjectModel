package io.github.robertomike.inject_model;

import io.github.robertomike.inject_model.drivers.SpringRepositoryReflectionDriverResolver;
import io.github.robertomike.inject_model.exceptions.ExceptionContract;
import io.github.robertomike.inject_model.exceptions.ParamNotFoundException;
import io.github.robertomike.inject_model.exceptions.RepositoryNotFoundException;
import io.github.robertomike.inject_model.models.FakeModel;
import io.github.robertomike.inject_model.models.Model;
import io.github.robertomike.inject_model.resolvers.InjectModelResolver;
import org.junit.jupiter.api.Test;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.HandlerMapping;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ModelTest extends BasicTest {
    @Test
    void setPackagePaths() {
        String packagePath = repository.getClass().getPackage().getName();

        SpringRepositoryReflectionDriverResolver.setPackagePath(packagePath);

        assertArrayEquals(SpringRepositoryReflectionDriverResolver.getPackagePaths(), new String[]{packagePath});
    }

    @Test
    void getNotFoundException() {
        String message = "Not found";

        ExceptionContract notFoundContract = new InjectModelResolver(applicationContext, properties).notFound(message);

        assertNotNull(notFoundContract);
        assertEquals(message, notFoundContract.getMessage());
    }

    @Test
    void getMethod() throws Exception {
        String methodName = "findById";

        Method method = new SpringRepositoryReflectionDriverResolver()
                .getMethod(repository, methodName, Long.class);

        assertNotNull(method);
    }

    @Test
    void repositoryNotFound() {
        String packagePath = repository.getClass().getPackage().getName();

        SpringRepositoryReflectionDriverResolver.setPackagePath(packagePath);
        SpringRepositoryReflectionDriverResolver driver = new SpringRepositoryReflectionDriverResolver();
        driver.load();

        assertThrows(
                RepositoryNotFoundException.class,
                () -> driver.resolveRepositoryOrThrow(FakeModel.class)
        );
    }

    @Test
    void paramNotFound() {
        String methodName = "findById";

        NativeWebRequest request = mock(NativeWebRequest.class);
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, 0)).thenReturn(Collections.singletonMap("id", "2"));

        InjectModelResolver injectModelResolver = new InjectModelResolver(applicationContext, properties);
        assertThrows(ParamNotFoundException.class, () ->
                injectModelResolver.getModelResultFromRequest(
                        "otherId",
                        Long.class,
                        (id) -> id,
                        request,
                        methodName,
                        Model.class
                )
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    void testGetObject() throws Exception {
        String methodName = "findById";

        NativeWebRequest request = mock(NativeWebRequest.class);
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, 0)).thenReturn(Collections.singletonMap("id", "2"));

        InjectModelResolver injectModelResolver = new InjectModelResolver(applicationContext, properties);

        when(repository.findById(2L)).thenReturn(Optional.of(new Model(2L)));

        when(applicationContext.getBean("modelRepository")).thenReturn(repository);

        Object object = injectModelResolver.getModelResultFromRequest(
                "id",
                Long.class,
                (id) -> id,
                request,
                methodName,
                Model.class
        );

        assertNotNull(object);
        assertEquals(Optional.class, object.getClass());
        Optional<Model> model = (Optional<Model>) object;
        assertEquals(Model.class, model.get().getClass());
        assertEquals(2L, model.get().getId());
    }

}

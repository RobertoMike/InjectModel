package io.github.robertomike;

import io.github.robertomike.exceptions.ExceptionContract;
import io.github.robertomike.exceptions.ParamNotFoundException;
import io.github.robertomike.exceptions.RepositoryNotFoundException;
import io.github.robertomike.models.Model;
import io.github.robertomike.repositories.ModelRepository;
import io.github.robertomike.resolvers.InjectModelResolver;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.HandlerMapping;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ModelTest extends BasicTest {
    ApplicationContext applicationContext = mock(ApplicationContext.class);
    ModelRepository repository = mock(ModelRepository.class);

    @Test
    void setPackagePaths() {
        String packagePath = repository.getClass().getPackageName();

        InjectModelResolver.setPackagePaths(packagePath);

        assertArrayEquals(InjectModelResolver.getPackagePaths(), new String[]{packagePath});
    }

    @Test
    void getNotFoundException() throws Exception {
        String message = "Not found";

        ExceptionContract notFoundContract = new InjectModelResolver().notFound(message);

        assertNotNull(notFoundContract);
        assertEquals(notFoundContract.getMessage(), message);
    }

    @Test
    void getMethod() throws Exception {
        String methodName = "findById";

        Method method = new InjectModelResolver().getMethod(repository, methodName, Long.class);

        assertNotNull(method);
    }

    @Test
    void repositoryNotFound() {
        String packagePath = repository.getClass().getPackageName();

        InjectModelResolver.setPackagePaths(packagePath);
        InjectModelResolver injectModelResolver = new InjectModelResolver();

        assertThrows(
                RepositoryNotFoundException.class,
                () -> injectModelResolver.findRepositoryByModel(
                        "FakeModel"
                )
        );
    }

    @Test
    void paramNotFound() {
        String methodName = "findById";

        NativeWebRequest request = mock(NativeWebRequest.class);
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, 0)).thenReturn(Collections.singletonMap("id", "2"));

        InjectModelResolver injectModelResolver = new InjectModelResolver();
        assertThrows(ParamNotFoundException.class, () ->
                injectModelResolver.getModelResultFromRequest(
                        false,
                        "otherId",
                        "model",
                        Long.class,
                        (id) -> id,
                        request,
                        methodName,
                        "Model"
                )
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    void testGetObject() throws Exception {
        String methodName = "findById";
        String packagePath = repository.getClass().getPackageName();

        InjectModelResolver.setPackagePaths(packagePath);

        NativeWebRequest request = mock(NativeWebRequest.class);
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, 0)).thenReturn(Collections.singletonMap("id", "2"));

        InjectModelResolver injectModelResolver = new InjectModelResolver();

        when(repository.findById(2L)).thenReturn(Optional.of(new Model(2L)));

        when(applicationContext.getBean(ModelRepository.class)).thenReturn(repository);

        injectModelResolver.setApplicationContext(applicationContext);

        Object object = injectModelResolver.getModelResultFromRequest(
                false,
                "id",
                "model",
                Long.class,
                (id) -> id,
                request,
                methodName,
                "Model"
        );

        assertNotNull(object);
        assertEquals(object.getClass(), Optional.class);
        assertEquals(((Optional<Model>) object).orElseThrow().getClass(), Model.class);
        assertEquals(
                ((Optional<Model>) object).orElseThrow().getId(), 2L
        );
    }

}

package io.github.inject_model;

import io.github.inject_model.exceptions.ExceptionContract;
import io.github.inject_model.exceptions.ParamNotFoundException;
import io.github.inject_model.exceptions.RepositoryNotFoundException;
import io.github.inject_model.models.Model;
import io.github.inject_model.repositories.ModelRepository;
import io.github.inject_model.resolvers.InjectModelResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.HandlerMapping;

import java.lang.reflect.Method;
import java.util.Optional;

class ModelTest extends BasicTest {
    ApplicationContext applicationContext = Mockito.mock(ApplicationContext.class);
    ModelRepository repository = Mockito.mock(ModelRepository.class);

    @Test
    void setPackagePaths() {
        String packagePath = repository.getClass().getPackageName();

        InjectModelResolver.setPackagePaths(packagePath);

        Assertions.assertArrayEquals(InjectModelResolver.getPackagePaths(), new String[]{packagePath});
    }

    @Test
    void getNotFoundException() throws Exception {
        String message = "Not found";

        ExceptionContract notFoundContract = new InjectModelResolver(request).notFound(message);

        Assertions.assertNotNull(notFoundContract);
        Assertions.assertEquals(notFoundContract.getMessage(), message);
    }

    @Test
    void getMethod() throws Exception {
        String methodName = "findById";

        Method method = new InjectModelResolver(request).getMethod(repository, methodName, Long.class);

        Assertions.assertNotNull(method);
    }

    @Test
    void repositoryNotFound() {
        String packagePath = repository.getClass().getPackageName();

        InjectModelResolver.setPackagePaths(packagePath);
        InjectModelResolver injectModelResolver = new InjectModelResolver(request);

        Assertions.assertThrows(
                RepositoryNotFoundException.class,
                () -> injectModelResolver.findRepositoryByModel(
                        "FakeModel"
                )
        );
    }

    @Test
    void paramNotFound() {
        String methodName = "findById";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/path/2");
        request.setAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE, "/path/{id}");

        InjectModelResolver injectModelResolver = new InjectModelResolver(request);
        Assertions.assertThrows(ParamNotFoundException.class, () ->
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

    @Test
    void getNameFromModelClass() {
        InjectModelResolver injectModelResolver = new InjectModelResolver(request);

        Assertions.assertEquals(
                injectModelResolver.getNameModelFromClass(Model.class),
                "Model"
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    void testGetObject() throws Exception {
        String methodName = "findById";
        String packagePath = repository.getClass().getPackageName();

        InjectModelResolver.setPackagePaths(packagePath);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/path/2");
        request.setAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE, "/path/{id}");

        InjectModelResolver injectModelResolver = new InjectModelResolver(request);

        Mockito.when(repository.findById(2L)).thenReturn(Optional.of(new Model(2L)));

        Mockito.when(applicationContext.getBean(ModelRepository.class)).thenReturn(repository);

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

        Assertions.assertNotNull(object);
        Assertions.assertEquals(object.getClass(), Optional.class);
        Assertions.assertEquals(((Optional<Model>) object).orElseThrow().getClass(), Model.class);
        Assertions.assertEquals(
                ((Optional<Model>) object).orElseThrow().getId(), 2L
        );
    }

}

package io.github.robertomike;

import io.github.robertomike.models.Model;
import io.github.robertomike.repositories.ModelRepository;
import io.github.robertomike.resolvers.InjectModelResolver;
import io.github.robertomike.resolvers.ModelResolver;
import io.github.robertomike.resolvers.annotations.InjectModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class InjectModelTest extends BasicTest {
    ApplicationContext applicationContext = Mockito.mock(ApplicationContext.class);
    ModelRepository repository = Mockito.mock(ModelRepository.class);

    @Test
    void testGetObject() throws Exception {
        String packagePath = repository.getClass().getPackageName();

        InjectModelResolver.setPackagePaths(packagePath);

        InjectModelResolver injectModelResolver = new InjectModelResolver();

        when(repository.findById(2L)).thenReturn(Optional.of(new Model(2L)));

        when(applicationContext.getBean(ModelRepository.class)).thenReturn(repository);

        injectModelResolver.setApplicationContext(applicationContext);
        ModelResolver.setPackagePaths("io.github.robertomike.repositories");

        InjectModel injectModel = mock(InjectModel.class);
        when(injectModel.nullable()).thenReturn(false);
        when(injectModel.value()).thenReturn("id");
        doReturn(Long.class).when(injectModel).paramType();
        when(injectModel.method()).thenReturn("findById");
        when(injectModel.message()).thenReturn("Model not found");

        MethodParameter parameter = mock(MethodParameter.class);

        when(parameter.getParameterAnnotation(InjectModel.class)).thenReturn(injectModel);
        when(parameter.getParameterType()).thenReturn((Class) Model.class);

        NativeWebRequest webRequest = mock(NativeWebRequest.class);
        when(webRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, 0)).thenReturn(Collections.singletonMap("id", "2"));

        Object object = injectModelResolver.resolveArgument(parameter, null, webRequest, null);

        Assertions.assertNotNull(object);
        Assertions.assertEquals(Model.class, object.getClass());
        Assertions.assertEquals(((Model) object).getId(), 2L);
    }
}

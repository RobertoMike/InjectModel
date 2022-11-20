package io.github.injectmodel;

import io.github.injectmodel.models.Model;
import io.github.injectmodel.repositories.ModelRepository;
import io.github.injectmodel.resolvers.InjectModelResolver;
import io.github.injectmodel.resolvers.annotations.InjectModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Optional;

public class InjectModelTest extends BasicTest {
    ApplicationContext applicationContext = Mockito.mock(ApplicationContext.class);
    ModelRepository repository = Mockito.mock(ModelRepository.class);

    @Test
    void testGetObject() throws Exception {
        String packagePath = repository.getClass().getPackageName();

        InjectModelResolver.setPackagePaths(packagePath);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/path/2");
        request.setAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE, "/path/{id}");

        InjectModelResolver injectModelResolver = new InjectModelResolver(request);

        Mockito.when(repository.findById(2L)).thenReturn(Optional.of(new Model(2L)));

        Mockito.when(applicationContext.getBean(ModelRepository.class)).thenReturn(repository);

        injectModelResolver.setApplicationContext(applicationContext);

        InjectModel injectModel = Mockito.mock(InjectModel.class);
        Mockito.when(injectModel.nullable()).thenReturn(false);
        Mockito.when(injectModel.value()).thenReturn("id");
        Mockito.doReturn(Long.class).when(injectModel).paramType();
        Mockito.when(injectModel.method()).thenReturn("findById");
        Mockito.when(injectModel.message()).thenReturn("Model not found");

        MethodParameter parameter = Mockito.mock(MethodParameter.class);

        Mockito.when(parameter.getParameterAnnotation(InjectModel.class)).thenReturn(injectModel);
        Mockito.when(parameter.getGenericParameterType()).thenReturn(Model.class);

        NativeWebRequest webRequest = Mockito.mock(NativeWebRequest.class);

        Object object = injectModelResolver.resolveArgument(parameter, null, webRequest, null);

        Assertions.assertNotNull(object);
        Assertions.assertEquals(Model.class, object.getClass());
        Assertions.assertEquals(((Model) object).getId(), 2L);
    }
}

package io.github.robertomike.inject_model;

import io.github.robertomike.inject_model.drivers.SpringModelDriverResolver;
import io.github.robertomike.inject_model.models.Model;
import io.github.robertomike.inject_model.resolvers.InjectModelResolver;
import io.github.robertomike.inject_model.resolvers.annotations.InjectModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class InjectModelTest extends BasicTest {

    @Test
    void testGetObject() throws Exception {
        InjectModelResolver injectModelResolver = new InjectModelResolver(new SpringModelDriverResolver(applicationContext, properties));

        when(repository.findById(2L)).thenReturn(Optional.of(new Model(2L)));

        when(applicationContext.getBean("modelRepository")).thenReturn(repository);

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
        Assertions.assertEquals(2L, ((Model) object).getId());
    }
}

package io.github.robertomike.inject_model;

import io.github.robertomike.inject_model.configs.InjectModelProperties;
import io.github.robertomike.inject_model.repositories.ModelRepository;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.request.NativeWebRequest;

import static org.mockito.Mockito.mock;

@SpringBootTest
public class BasicTest {
    ApplicationContext applicationContext = Mockito.mock(ApplicationContext.class);
    ModelRepository repository = Mockito.mock(ModelRepository.class);
    NativeWebRequest request = mock(NativeWebRequest.class);
    InjectModelProperties properties = new InjectModelProperties();
}

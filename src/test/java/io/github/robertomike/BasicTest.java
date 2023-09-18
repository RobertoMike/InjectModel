package io.github.robertomike;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.context.request.NativeWebRequest;

import static org.mockito.Mockito.mock;

@SpringBootTest
public class BasicTest {
    NativeWebRequest request = mock(NativeWebRequest.class);
}

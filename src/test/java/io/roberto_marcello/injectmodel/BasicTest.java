package io.roberto_marcello.injectmodel;

import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import javax.servlet.http.HttpServletRequest;

@SpringBootTest
public class BasicTest {
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
}

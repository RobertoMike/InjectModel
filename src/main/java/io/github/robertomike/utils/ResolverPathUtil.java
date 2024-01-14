package io.github.robertomike.utils;

import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;
import java.util.Optional;

/**
 * Class for resolve path variable
 */
public class ResolverPathUtil {
    @SuppressWarnings("unchecked")
    public static Optional<String> resolveVariable(NativeWebRequest request, String variable) {
        Map<String, String> uriTemplateVars = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, 0);
        return Optional.ofNullable(uriTemplateVars != null ? uriTemplateVars.get(variable) : null);
    }
}
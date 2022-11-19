package com.mike.inject_model.utils;

import lombok.AllArgsConstructor;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@AllArgsConstructor
public class ResolverPathUtil {

    HttpServletRequest request;

    /**
     * @param variable the searched value on a current path
     * @return Optional object with the possible value from a current path
     */
    public Optional<String> resolveVariable(String variable) {
        String[] originalPath = originalPath().split("/");
        String[] path = currentRequestPath().split("/");

        String id = null;

        for (int i = 0; i < originalPath.length; i++) {
            if (originalPath[i].contains(variable)) {
                id = path[i];
            }
        }

        return Optional.ofNullable(id);
    }

    /**
     * @return original path
     */
    public String originalPath() {
        return request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString();
    }

    /**
     * @return current request path
     */
    public String currentRequestPath() {
        return request.getRequestURI();
    }

}

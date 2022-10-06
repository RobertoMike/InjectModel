package com.mike.inject_model.utils;

import lombok.AllArgsConstructor;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@AllArgsConstructor
public class ResolverPathUtil {

    HttpServletRequest request;

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

    public String originalPath() {
        return request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString();
    }

    public String currentRequestPath() {
        return request.getRequestURI();
    }

}

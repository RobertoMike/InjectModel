package io.github.robertomike.inject_model.utils

import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.servlet.HandlerMapping

/**
 * Class for resolve path variable
 */
class ResolverPathUtil {
    companion object {
        /**
         * Resolves a variable from the request's URI template variables.
         *
         * @param request the current request
         * @param variable the name of the variable to resolve
         * @return the value of the variable, or null if not found
         */
        @JvmStatic
        fun resolveVariable(request: NativeWebRequest, variable: String): String? {
            val uriTemplateVars =
                request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, 0) as Map<String, String>?
            return uriTemplateVars?.get(variable)
        }
    }
}
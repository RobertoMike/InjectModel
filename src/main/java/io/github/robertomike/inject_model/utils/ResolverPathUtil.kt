package io.github.robertomike.inject_model.utils

import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.servlet.HandlerMapping

/**
 * Class for resolve path variable
 */
class ResolverPathUtil {
    companion object {
        @JvmStatic
        fun resolveVariable(request: NativeWebRequest, variable: String): String? {
            val uriTemplateVars =
                request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, 0) as Map<String, String>?
            return uriTemplateVars?.get(variable)
        }
    }
}
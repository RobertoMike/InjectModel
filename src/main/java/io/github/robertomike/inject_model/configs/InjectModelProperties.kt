package io.github.robertomike.inject_model.configs

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.PropertySource
import org.springframework.stereotype.Component

/**
 * Configuration properties for the Inject Model library.
 *
 * This class holds the configuration properties for the Inject Model library, which can be customized
 * by setting the corresponding properties in the `inject-model.properties` file or by using the
 * `inject-model` prefix in the application's configuration.
 *
 * @property suffix the suffix used to search for repositories (default: "Repository")
 */
@ConfigurationProperties(prefix = "inject-model")
@PropertySource("classpath:inject-model.properties")
@Component
class InjectModelProperties {
    /**
     * The suffix used to search for repositories.
     *
     * This property determines the suffix that will be appended to the model name when searching for
     * a corresponding repository. The default value is "Repository".
     */
    var suffix: String = "Repository"

    /**
     * The driver that will be used to resolve the repository.
     */
    var driver: String = "default"
}
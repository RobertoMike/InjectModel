package io.github.robertomike.inject_model.configs

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.PropertySource

@ConfigurationProperties(prefix = "inject-model")
@PropertySource("classpath:inject-model.properties")
class InjectModelProperties {
    var suffix: String = "Repository"
}
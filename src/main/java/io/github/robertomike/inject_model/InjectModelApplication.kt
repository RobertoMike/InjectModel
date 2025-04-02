package io.github.robertomike.inject_model

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration

/**
 * Start the app
 */
@SpringBootApplication(
    exclude = [DataSourceAutoConfiguration::class, DataSourceTransactionManagerAutoConfiguration::class, HibernateJpaAutoConfiguration::class, ErrorMvcAutoConfiguration::class, WebMvcAutoConfiguration::class
    ]
)
open class InjectModelApplication


/**
 * @param args console
 */
fun main(args: Array<String>) {
    SpringApplication.run(InjectModelApplication::class.java, *args)
}
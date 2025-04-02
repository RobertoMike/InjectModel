package io.github.robertomike.inject_model.drivers

import io.github.robertomike.inject_model.configs.InjectModelProperties
import io.github.robertomike.inject_model.exceptions.RepositoryNotFoundException
import org.reflections.Reflections
import org.reflections.util.ConfigurationBuilder
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.ApplicationContext
import org.springframework.data.repository.Repository
import org.springframework.stereotype.Component
import java.lang.reflect.InvocationTargetException

/**
 * Abstract base class for resolving models using a driver.
 *
 * @param applicationContext the Spring application context
 * @param properties the InjectModel properties
 */
@Component
@ConditionalOnProperty("inject-model.driver", havingValue = "reflection")
@Deprecated("This driver is not longer recommended, use 'SpringModelDriverResolver' instead")
class SpringRepositoryReflectionDriverResolver(
    applicationContext: ApplicationContext,
    properties: InjectModelProperties
) : ModelDriverResolver<Repository<*, *>>(applicationContext, properties) {
    private val modelToRepository = mutableMapOf<String, Class<out Repository<*, *>>>()
    var packagePaths: Array<String> = arrayOf()

    companion object {
        lateinit var listOfRepositories: Set<Class<out Repository<*, *>>>

        fun isListOfRepositoryInitialized(): Boolean {
            return Companion::listOfRepositories.isInitialized
        }

    }

    fun setPackagePath(vararg packagePaths: String) {
        this.packagePaths = arrayOf(*packagePaths)
    }

    /**
     * to search repositories if list is empty
     */
    override fun load() {
        if (isListOfRepositoryInitialized()) {
            return
        }

        val reflections = Reflections(ConfigurationBuilder().forPackages(*packagePaths))
        listOfRepositories = reflections.getSubTypesOf(getResolvedClass())
    }

    /**
     * Resolving repository using by class of the model
     */
    fun resolveRepositoryOrThrow(modelClass: Class<*>): Class<out Repository<*, *>> {
        val modelName = modelClass.simpleName

        return modelToRepository.getOrPut(modelName) {
            listOfRepositories.asSequence()
                .filter { classType ->
                    packagePaths.any { packagePath: String ->
                        classType.name.contains(
                            packagePath + "." + modelName + properties.suffix
                        )
                    }
                }
                .firstOrNull() ?: throw RepositoryNotFoundException("Repository not found for model: $modelName")
        }
    }

    /**
     * Resolving model using repository and the method
     */
    @Throws(InvocationTargetException::class, IllegalAccessException::class, NoSuchMethodException::class)
    override fun resolveModel(model: Class<*>, method: String, value: String, paramType: Class<*>): Any? {
        val repository = applicationContext.getBean(resolveRepositoryOrThrow(model))
        val callable = getMethod(repository, method, paramType)
        return callable.invoke(repository, parse(value, paramType))
    }
}
package io.github.robertomike.inject_model.drivers

import io.github.robertomike.inject_model.configs.InjectModelProperties
import io.github.robertomike.inject_model.exceptions.RepositoryNotFoundException
import org.reflections.Reflections
import org.reflections.util.ConfigurationBuilder
import org.springframework.context.ApplicationContext
import org.springframework.data.repository.Repository
import java.lang.reflect.InvocationTargetException

class SpringRepositoryReflectionDriverResolver (
    applicationContext: ApplicationContext,
    properties: InjectModelProperties
) : ModelDriverResolver<Repository<*, *>>(applicationContext, properties) {
    private val modelToRepository = mutableMapOf<String, Class<out Repository<*, *>>>()

    companion object {
        @JvmStatic
        var packagePaths: Array<String> = arrayOf()
        lateinit var listOfRepositories: Set<Class<out Repository<*, *>>>

        fun isListOfRepositoryInitialized(): Boolean {
            return Companion::listOfRepositories.isInitialized
        }

        @JvmStatic
        fun setPackagePath(vararg packagePaths: String) {
            Companion.packagePaths = arrayOf(*packagePaths)
        }
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
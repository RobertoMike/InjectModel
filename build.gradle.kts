plugins {
    kotlin("jvm") version "2.1.0" // States that this project uses Kotlin and specifies version

    id("java-library") // States that this project is a Java library
    `maven-publish` // Add commands and configuration for publishing
    id("signing") // Used for Maven digital signature
}

group = "io.github.robertomike"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
}

var springVersion = "2.1.0.RELEASE"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.reflections:reflections:0.10.2")
    implementation("org.springframework.boot:spring-boot-starter-web:$springVersion")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:$springVersion")

    api("org.springframework.boot:spring-boot-starter-web:$springVersion")
    // api("org.springframework.boot:spring-boot-starter-data-jpa:$springVersion")

    testImplementation("org.springframework.boot:spring-boot-starter-test:2.1.0.RELEASE")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.11.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    jvmToolchain(8)
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Publishes the library
publishing {
    publications {
        register("library", MavenPublication::class) {
            from(components["java"])

            groupId = "$group" // The group ID of the library
            artifactId = "inject_model" // The artifact ID of the library
            version = version

            pom {
                name = "Inject model"
                description = "This is an open-source Java library that provides an annotation to search models in a more easy way."
                url = "https://github.com/RobertoMike/InjectModel"
                inceptionYear = "2025"

                // The license of the library
                licenses {
                    license {
                        name = "MIT License"
                        url = "http://www.opensource.org/licenses/mit-license.php"
                    }
                }
                developers {
                    developer {
                        name = "Roberto Micheletti"
                        email = "rmworking@hotmail.com"
                        organization = "Kaiten"
                        organizationUrl = "https://github.com/RobertoMike"
                    }
                }
                scm { // Where our library will be hosted (GitHub)
                    connection = "scm:git:git://github.com/RobertoMike/InjectModel.git"
                    developerConnection = "scm:git:ssh://github.com:RobertoMike/InjectModel.git"
                    url = "https://github.com/RobertoMike/InjectModel"
                }
            }
        }
    }
    repositories { // Specifies the repositories used to publish the library
        maven {

            name = "OSSRH"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = System.getenv("OSSRH_USERNAME")
                password = System.getenv("OSSRH_PASSWORD")
            }
            metadataSources {
                gradleMetadata()
            }
        }
    }
}

// If the local property is set, it doesn't execute the signing
if (!project.hasProperty("local")) {
    signing {
        setRequired { !version.toString().endsWith("SNAPSHOT") }
        sign(publishing.publications["library"])
    }
}

tasks.withType(JavaCompile::class).configureEach {
    options.encoding = "UTF-8"
}

java {
    withJavadocJar()
    withSourcesJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}
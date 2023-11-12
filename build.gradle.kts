import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val javaVersion = JavaVersion.VERSION_11

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.gradle.publish)
    `java-gradle-plugin`
}

group = "eu.vendeli"
version = providers.gradleProperty("pluginVersion").getOrElse("dev")

gradlePlugin {
    website.set("https://vendeli.eu")
    vcsUrl.set("https://github.com/vendelieu/jooq-extension")

    plugins {
        create("jooq-extension") {
            id = "eu.vendeli.jooq.extension"
            displayName = "Jooq Extension Gradle Plugin"
            description = "Gradle plugin that extends functionality of the JOOQ."
            tags.set(listOf("kotlin", "spring-boot", "jooq"))
            implementationClass = "eu.vendeli.jooq.JooqExtensionPlugin"
        }
    }
}

configurations.compileClasspath {
    // Allow Java 11 dependencies on compile classpath
    attributes.attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 11)
}

dependencies {
    implementation(libs.jooq.codegen)
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    mavenLocal()
}

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}


tasks {
    withType<Wrapper> {
        distributionType = Wrapper.DistributionType.ALL
    }

    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = javaVersion.majorVersion
        }
    }
}
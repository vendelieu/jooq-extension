import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.KtlintExtension

val javaVersion = JavaVersion.VERSION_11

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.gradle.publish)
    `java-gradle-plugin`
    alias(libs.plugins.detekt)
    alias(libs.plugins.ktlint)
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
            @Suppress("UnstableApiUsage")
            tags.set(listOf("kotlin", "spring-boot", "jooq"))
            implementationClass = "eu.vendeli.jooq.JooqExtensionPlugin"
        }
    }
}

configurations.compileClasspath {
    // Allow Java 11 dependencies on compiler classpath
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

    withType<Detekt> {
        config = rootProject.files("config/detekt.yml")
    }
    configure<KtlintExtension> {
        debug.set(false)
        verbose.set(true)
        android.set(false)
        outputToConsole.set(true)
        ignoreFailures.set(false)
        enableExperimentalRules.set(true)
        filter {
            exclude("**/generated/**")
            include("**/kotlin/**")
        }
    }

    withType<KotlinCompile> {
        compilerOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget.set(JvmTarget.fromTarget(javaVersion.majorVersion))
        }
    }
}

rootProject.name = "jooq-extension"

plugins {
    id("com.gradle.develocity") version "3.18.1"
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        gradlePluginPortal()
        google()
    }
}

develocity {
    buildScan {
        termsOfUseUrl = "https://gradle.com/terms-of-service"
        termsOfUseAgree = "yes"
    }
}

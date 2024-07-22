rootProject.name = "jooq-extension"

plugins {
    id("com.gradle.enterprise") version "3.17.6"
}

dependencyResolutionManagement {
    repositories.gradlePluginPortal()
    repositories.google()
}

gradleEnterprise {
    buildScan {
        publishAlways()
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
    }
}

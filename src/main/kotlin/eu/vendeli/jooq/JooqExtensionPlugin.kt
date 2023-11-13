package eu.vendeli.jooq

import org.gradle.api.Plugin
import org.gradle.api.Project

class JooqExtensionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        println("You can set ExtendedJavaJooqGenerator as your JOOQ custom generator.")
    }
}

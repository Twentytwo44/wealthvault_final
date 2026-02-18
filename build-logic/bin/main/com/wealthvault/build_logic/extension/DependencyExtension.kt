package com.wealthvault.build_logic.extension

import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.provideDelegate
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

fun KotlinDependencyHandler.implementationLocalDependencyWithEnvironment(
    project: Project,
    path: String,
): Dependency? {
    val environment: String by project
    return implementation(project("$path:$environment"))
}

internal fun Project.versionCatalogVersion(alias: String): String {
    return versionCatalog().findVersion(alias).get().toString()
}
internal fun Project.versionCatalogLibrary(alias: String): Provider<MinimalExternalModuleDependency> {
    return versionCatalog().findLibrary(alias).get()
}

internal fun Project.versionCatalogPlugin(alias: String): String {
    return versionCatalog().findPlugin(alias).get().get().pluginId
}

private fun Project.versionCatalog(): VersionCatalog {
    return extensions.getByType<VersionCatalogsExtension>().named("libs")
}

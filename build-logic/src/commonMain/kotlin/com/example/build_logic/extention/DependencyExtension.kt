//package me.orbitalno11.buildLogic.extention
//
//import org.gradle.api.Project
//import org.gradle.api.artifacts.MinimalExternalModuleDependency
//import org.gradle.api.artifacts.VersionCatalog
//import org.gradle.api.artifacts.VersionCatalogsExtension
//import org.gradle.api.provider.Provider
//import org.gradle.kotlin.dsl.getByType
//
//internal fun Project.versionCatalogVersion(alias: String): String {
//    return versionCatalog().findVersion(alias).get().toString()
//}
//internal fun Project.versionCatalogLibrary(alias: String): Provider<MinimalExternalModuleDependency> {
//    return versionCatalog().findLibrary(alias).get()
//}
//
//internal fun Project.versionCatalogPlugin(alias: String): String {
//    return versionCatalog().findPlugin(alias).get().get().pluginId
//}
//
//private fun Project.versionCatalog(): VersionCatalog {
//    return extensions.getByType<VersionCatalogsExtension>().named("libs")
//}
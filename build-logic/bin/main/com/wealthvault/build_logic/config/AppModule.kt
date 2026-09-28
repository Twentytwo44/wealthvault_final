package com.wealthvault.build_logic.config

object AppModule {
    const val composeApp = ":composeApp"
    const val androidApp = ":androidApp"
}

object FeatureModules {
    const val home = ":features:auth:login"
//    const val profile = ":feature:profile"
}

object FunctionalModules {
    // Legacy functional modules are intentionally absent from the build graph.
    // New code depends on the bounded-context data/domain modules below.
    const val authData = ":data:auth"
    const val portfolioData = ":data:portfolio"
    const val socialData = ":data:social"
}

object BaseModule {
    const val config = ":base:config"
    const val core = ":base:core"
    const val network = ":base:network"
    const val security = ":base:security"
    const val database = ":base:database"
}

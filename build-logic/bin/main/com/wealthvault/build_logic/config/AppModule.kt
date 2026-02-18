package com.wealthvault.build_logic.config

object AppModule {
    const val app = ":app"
    const val navigation = ":navigation"
}

object FeatureModules {
    const val home = ":features:auth:login"
//    const val profile = ":feature:profile"
}

object FunctionalModules {
    const val api = ":functional:api"
//    const val coint = ":functional:coin"
    const val dataStore = ":functional:data-store"
//    const val db = ":functional:db"
//    const val navigation = ":functional:navigation"
}

object BaseModule {
    const val config = ":base:config"
    const val core = ":base:core"
}

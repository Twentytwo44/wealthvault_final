package com.wealthvault.core

object KoinConst {
    object KotlinSerialization {
        const val GLOBAL = "global-kotlin-serialization"

        const val USER = "user-kotlin-serialization"
        const val WEBSOCKET = "websocket-kotlin-serialization"
    }

    object HttpClient {

        /** Client for endpoints that must never carry an access token. */
        const val PUBLIC = "public-http-client"

        /** Client for application endpoints that require session auth. */
        const val AUTHENTICATED = "authenticated-http-client"

        /** Compatibility qualifier used by migrated non-auth API adapters. */
        const val GLOBAL = AUTHENTICATED

        const val USER = "user-http-client"
        const val WEBSOCKET = "websocket-http-client"
    }

    object DataStore {
        const val DEFAULT = "default-data-store"
        const val APP_SETTING = "default-data-setting"
    }
}

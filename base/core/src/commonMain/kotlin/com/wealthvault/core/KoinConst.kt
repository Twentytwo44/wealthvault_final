package com.wealthvault.core

object KoinConst {
    object KotlinSerialization {
        const val GLOBAL = "global-kotlin-serialization"

        const val AUTH = "auth-kotlin-serialization"
        const val USER = "user-kotlin-serialization"
    }

    object HttpClient {

        const val GLOBAL = "global-http-client"

        const val AUTH = "auth-http-client"
        const val USER = "user-http-client"
    }

    object Ktor {

        const val GLOBAL = "global-ktorfit"

        const val AUTH = "auth-ktorfit"
        const val USER = "user-ktorfit"
    }

    object DataStore {
        const val DEFAULT = "default-data-store"
        const val APP_SETTING = "default-data-setting"
    }
}

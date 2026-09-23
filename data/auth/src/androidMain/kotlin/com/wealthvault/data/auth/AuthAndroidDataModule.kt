package com.wealthvault.data.auth

import com.wealthvault.data.auth.google.di.GoogleAuthAndroidModule
import org.koin.core.module.Module

/** Android-only authentication provider wiring owned by the auth data module. */
object AuthAndroidDataModule {
    val allModules: List<Module> = listOf(GoogleAuthAndroidModule.allModules)
}

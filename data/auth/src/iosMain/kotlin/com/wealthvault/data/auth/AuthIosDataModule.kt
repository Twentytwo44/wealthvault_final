package com.wealthvault.data.auth

import com.wealthvault.data.auth.google.di.GoogleAuthIOSModule
import org.koin.core.module.Module

/** iOS-only authentication provider wiring owned by the auth data module. */
object AuthIosDataModule {
    val allModules: List<Module> = listOf(GoogleAuthIOSModule.allModules)
}

package com.wealthvault.security

import com.wealthvault.security.session.di.DataStoreModule
import org.koin.core.module.Module

/**
 * Composition-root facade for secure session storage.
 *
 * The implementation is the only composition-root binding for secure session
 * storage. Callers outside this module depend on the domain SessionManager and
 * SessionTokenStore contracts; DataStore/Keystore/Keychain details stay here.
 */
object SecuritySessionModule {
    val allModules: List<Module> = listOf(DataStoreModule.allModules)
}

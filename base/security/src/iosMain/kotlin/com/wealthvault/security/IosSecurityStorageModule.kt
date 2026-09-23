package com.wealthvault.security

import com.wealthvault.security.session.iosDataStoreModule

/** iOS Keychain/DataStore bindings exposed through the security boundary. */
val iosSecurityStorageModule = iosDataStoreModule.allModules

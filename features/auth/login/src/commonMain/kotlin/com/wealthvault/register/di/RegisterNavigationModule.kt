package com.wealthvault.register.di

import cafe.adriel.voyager.core.registry.screenModule
import com.wealthvault.core.navigation.SharedScreen
import com.wealthvault.register.ui.RegisterScreen

val registerScreenModule = screenModule {
    register<SharedScreen.Register> { RegisterScreen() }
}

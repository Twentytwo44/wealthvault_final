package com.wealthvault.register.di


import com.wealthvault.register.ui.RegisterScreenModel
import com.wealthvault.register.usecase.RegisterUseCase
import org.koin.dsl.module

object RegisterModule {
    val allModules = module {
        factory { RegisterUseCase(get(), get(), get()) }

        factory { RegisterScreenModel(get(), get()) }
    }
}

package com.wealthvault.register.di


import com.wealthvault.register.data.RegisterDataSource
import com.wealthvault.register.data.RegisterRepositoryImpl
import com.wealthvault.register.ui.RegisterScreenModel
import com.wealthvault.register.usecase.RegisterUseCase
import org.koin.dsl.module

object RegisterModule {
    val allModules = module {
        factory { RegisterDataSource(get()) }

        single<RegisterRepositoryImpl> {
            RegisterRepositoryImpl(
                networkDataSource = get(),
            )
        }
        factory { RegisterUseCase(get(), get()) }

        factory { RegisterScreenModel(get()) }
    }
}

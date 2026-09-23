package com.wealthvault.login.di
import com.wealthvault.login.ui.LoginScreenModel
import com.wealthvault.login.usecase.LoginUseCase
import org.koin.dsl.module

object LoginModule {
    val allModules = module {

        factory { LoginUseCase(get(), get(), get()) }

        factory { LoginScreenModel(get(), get(), get(), get()) }

    }
}

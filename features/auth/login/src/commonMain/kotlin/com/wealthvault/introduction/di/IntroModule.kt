package com.wealthvault.introduction.di

import com.wealthvault.introduction.data.IntroNetworkDataSource
import com.wealthvault.introduction.data.IntroRepositoryImpl
import com.wealthvault.introduction.ui.IntroScreenModel
import org.koin.dsl.module

object IntroModule {
    val allModules = module {
        factory { IntroScreenModel(get()) }
        factory { IntroNetworkDataSource(get()) }
        single<IntroRepositoryImpl> {
            IntroRepositoryImpl(get())
        }

    }
}

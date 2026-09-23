package com.wealthvault.introduction.di

import com.wealthvault.introduction.ui.IntroScreenModel
import com.wealthvault.domain.profile.ProfileRepository
import org.koin.dsl.module

object IntroModule {
    val allModules = module {
        factory { IntroScreenModel(get<ProfileRepository>(), get()) }
    }
}

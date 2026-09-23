package com.wealthvault.profile.di

import com.wealthvault.profile.ui.EditProfileScreenModel
import com.wealthvault.profile.ui.MenuProfileSettingScreenModel
import com.wealthvault.profile.ui.ProfileScreenModel
import com.wealthvault.profile.ui.ShareSettingScreenModel
import org.koin.dsl.module

object ProfileModule {
    val allModules = module {
        factory { ProfileScreenModel(get()) }
        factory { EditProfileScreenModel(repository = get()) }
        factory { ShareSettingScreenModel(repository = get()) }

        factory { MenuProfileSettingScreenModel(get(),get(),get()) }

    }
}

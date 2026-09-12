package com.wealthvault.profile.di

import LineRepositoryImpl
import com.wealthvault.profile.data.ProfileDataSource
import com.wealthvault.profile.data.ProfileRepositoryImpl
import com.wealthvault.profile.data.device.UnRegisterDeviceDataSource
import com.wealthvault.profile.data.device.UnRegisterDeviceRepositoryImpl
import com.wealthvault.profile.data.linelink.LineNetworkDataSource
import com.wealthvault.profile.ui.EditProfileScreenModel
import com.wealthvault.profile.ui.MenuProfileSettingScreenModel
import com.wealthvault.profile.ui.ProfileScreenModel
import com.wealthvault.profile.ui.ShareSettingScreenModel
import org.koin.dsl.module

//

object ProfileModule {
    val allModules = module {
        factory { ProfileDataSource(userApi = get(), updateUserApi = get(), closeFriendApi = get(), updateCloseFriendApi = get(), friendApi = get()) }

        single<ProfileRepositoryImpl> {
            ProfileRepositoryImpl(
                get(),
            )
        }
        factory { ProfileScreenModel(get()) }
        factory { EditProfileScreenModel(repository = get()) }
        factory { ShareSettingScreenModel(repository = get()) }

        factory { UnRegisterDeviceDataSource(get()) }
        single<UnRegisterDeviceRepositoryImpl> {
            UnRegisterDeviceRepositoryImpl(
                get(),
            )
        }

        factory { MenuProfileSettingScreenModel(get(),get(),get()) }

        factory { LineNetworkDataSource(get()) }
        single<LineRepositoryImpl> { LineRepositoryImpl(get()) }

    }
}

package com.wealthvault.profile.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.wealthvault.data_store.TokenStore
import com.wealthvault.profile.data.ProfileDataSource
import com.wealthvault.profile.data.ProfileRepositoryImpl
import com.wealthvault.profile.ui.EditProfileScreenModel
import com.wealthvault.profile.ui.ProfileScreenModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module

//

object ProfileModule {
    val allModules = module {

        factory { ProfileDataSource(userApi = get(), updateUserApi = get()) }

        single<TokenStore> { TokenStore(get<DataStore<Preferences>>()) }

        single<ProfileRepositoryImpl> {
            ProfileRepositoryImpl(
                networkDataSource = get(),
            )
        }
        single { Dispatchers.IO }

        factory { ProfileScreenModel(get()) }
        factory { EditProfileScreenModel(repository = get()) }
    }
}

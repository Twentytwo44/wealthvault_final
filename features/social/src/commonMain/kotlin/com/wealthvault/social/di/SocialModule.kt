package com.wealthvault.social.di

import com.wealthvault.social.data.SocialDataSource
import com.wealthvault.social.data.SocialRepositoryImpl
import com.wealthvault.social.ui.SocialScreenModel
import com.wealthvault.social.ui.main_social.add_friend.AddFriendScreenModel
import com.wealthvault.social.ui.main_social.form_group.FormGroupScreenModel
import com.wealthvault.social.ui.main_social.friend.FriendScreenModel
import com.wealthvault.social.ui.main_social.group.GroupScreenModel
import com.wealthvault.social.ui.manage_shared.SharedAssetManageScreenModel
import com.wealthvault.social.ui.manage_shared.SharedAssetScreenModel
import com.wealthvault.social.ui.profile.FriendProfileScreenModel
import com.wealthvault.social.ui.profile.GroupProfileScreenModel
import com.wealthvault.social.ui.space.FriendSpaceScreenModel
import com.wealthvault.social.ui.space.GroupSpaceScreenModel
import org.koin.dsl.module

object SocialModule {
    val allModules = module {

        factory {
            SocialDataSource(
                get(), get(), get(), get(), get(), get(), get(),
                get(), get(), get(), get(), get(), get(), get(),
                get(), get(), get(), get(), get(),
                get(), get(), get(), get(), get(),
                get(), get(), get(), get(), get()
            )
        }

        factory { SocialScreenModel(repository = get()) }

        single { SocialRepositoryImpl(get()) }
        factory { FriendScreenModel(repository = get()) }
        factory { GroupScreenModel(repository = get()) }
        factory { FormGroupScreenModel(repository = get()) }
        factory { AddFriendScreenModel(repository = get()) }
        factory { FriendSpaceScreenModel(repository = get()) }
        factory { FriendProfileScreenModel(repository = get()) }
        factory { GroupSpaceScreenModel(repository = get()) }
        factory { GroupProfileScreenModel(repository = get()) }
        factory { SharedAssetScreenModel(repository = get()) }
        factory { SharedAssetManageScreenModel(get()) }



    }
}
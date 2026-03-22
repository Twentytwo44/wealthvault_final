package com.wealthvault.account_api.di

import com.wealthvault.account_api.createaccount.CreateAccountApi
import com.wealthvault.account_api.createaccount.CreateAccountApiImpl
import com.wealthvault.account_api.deleteaccount.DeleteAccountApi
import com.wealthvault.account_api.deleteaccount.DeleteAccountApiImpl
import com.wealthvault.account_api.getaccount.GetAccountApi
import com.wealthvault.account_api.getaccount.GetAccountApiImpl
import com.wealthvault.account_api.getaccountbyid.GetAccountByIdApi
import com.wealthvault.account_api.getaccountbyid.GetAccountByIdApiImpl
import com.wealthvault.account_api.updateaccount.UpdateAccountApi
import com.wealthvault.account_api.updateaccount.UpdateAccountApiImpl
import com.wealthvault.core.KoinConst
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module


object AccountApiModule {
    val allModules = module {
        single<Json>(named(KoinConst.KotlinSerialization.GLOBAL)) {
            Json {
                ignoreUnknownKeys = true
            }
        }


        single<CreateAccountApi> { CreateAccountApiImpl(get(named(KoinConst.Ktor.GLOBAL))) }
        single<GetAccountApi> { GetAccountApiImpl(get(named(KoinConst.Ktor.GLOBAL))) }
        single<GetAccountByIdApi> { GetAccountByIdApiImpl(get(named(KoinConst.Ktor.GLOBAL))) }
        single<UpdateAccountApi> { UpdateAccountApiImpl(get(named(KoinConst.Ktor.GLOBAL))) }
        single<DeleteAccountApi> { DeleteAccountApiImpl(get(named(KoinConst.Ktor.GLOBAL))) }



    }


}

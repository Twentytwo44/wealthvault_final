package com.wealthvault.data.portfolio.account.transport.di

import com.wealthvault.data.portfolio.account.transport.createaccount.CreateAccountApi
import com.wealthvault.data.portfolio.account.transport.createaccount.CreateAccountApiImpl
import com.wealthvault.data.portfolio.account.transport.deleteaccount.DeleteAccountApi
import com.wealthvault.data.portfolio.account.transport.deleteaccount.DeleteAccountApiImpl
import com.wealthvault.data.portfolio.account.transport.getaccount.GetAccountApi
import com.wealthvault.data.portfolio.account.transport.getaccount.GetAccountApiImpl
import com.wealthvault.data.portfolio.account.transport.getaccountbyid.GetAccountByIdApi
import com.wealthvault.data.portfolio.account.transport.getaccountbyid.GetAccountByIdApiImpl
import com.wealthvault.data.portfolio.account.transport.updateaccount.UpdateAccountApi
import com.wealthvault.data.portfolio.account.transport.updateaccount.UpdateAccountApiImpl
import com.wealthvault.core.KoinConst
import io.ktor.client.HttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module


object AccountApiModule {
    val allModules = module {
        single<CreateAccountApi> { CreateAccountApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<GetAccountApi> { GetAccountApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<GetAccountByIdApi> { GetAccountByIdApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<UpdateAccountApi> { UpdateAccountApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<DeleteAccountApi> { DeleteAccountApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }



    }


}

package com.example.account_api.di

import com.example.account_api.createaccount.CreateAccountApi
import com.example.account_api.createaccount.CreateAccountApiImpl
import com.example.account_api.deleteaccount.DeleteAccountApi
import com.example.account_api.deleteaccount.DeleteAccountApiImpl
import com.example.account_api.getaccount.GetAccountApi
import com.example.account_api.getaccount.GetAccountApiImpl
import com.example.account_api.getaccountbyid.GetAccountByIdApi
import com.example.account_api.getaccountbyid.GetAccountByIdApiImpl
import com.example.account_api.updateaccount.UpdateAccountApi
import com.example.account_api.updateaccount.UpdateAccountApiImpl
import com.wealthvault.core.KoinConst
import org.koin.core.qualifier.named
import org.koin.dsl.module


object AccountApiModule {
    val allModules = module {

        single<CreateAccountApi> { CreateAccountApiImpl(get(named(KoinConst.Ktor.USER))) }
        single<GetAccountApi> { GetAccountApiImpl(get(named(KoinConst.Ktor.USER))) }
        single<GetAccountByIdApi> { GetAccountByIdApiImpl(get(named(KoinConst.Ktor.USER))) }
        single<UpdateAccountApi> { UpdateAccountApiImpl(get(named(KoinConst.Ktor.USER))) }
        single<DeleteAccountApi> { DeleteAccountApiImpl(get(named(KoinConst.Ktor.USER))) }



    }


}

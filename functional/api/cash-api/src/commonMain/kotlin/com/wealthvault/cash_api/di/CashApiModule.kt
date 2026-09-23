package com.wealthvault.cash_api.di



import com.wealthvault.cash_api.createcash.CreateCashApi
import com.wealthvault.cash_api.createcash.CreateCashApiImpl
import com.wealthvault.cash_api.deletecash.DeleteCashApi
import com.wealthvault.cash_api.deletecash.DeleteCashApiImpl
import com.wealthvault.cash_api.getcash.GetCashApi
import com.wealthvault.cash_api.getcash.GetCashApiImpl
import com.wealthvault.cash_api.getcashtbyid.GetCashByIdApi
import com.wealthvault.cash_api.getcashtbyid.GetCashByIdApiImpl
import com.wealthvault.cash_api.updatecash.UpdateCashApi
import com.wealthvault.cash_api.updatecash.UpdateCashApiImpl
import com.wealthvault.core.KoinConst
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module


object CashApiModule {
    val allModules = module {
        single<Json>(named(KoinConst.KotlinSerialization.GLOBAL)) {
            Json {
                ignoreUnknownKeys = true
            }
        }

        single<CreateCashApi> { CreateCashApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<GetCashApi> { GetCashApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<GetCashByIdApi> { GetCashByIdApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<UpdateCashApi> { UpdateCashApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<DeleteCashApi> { DeleteCashApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }



    }


}

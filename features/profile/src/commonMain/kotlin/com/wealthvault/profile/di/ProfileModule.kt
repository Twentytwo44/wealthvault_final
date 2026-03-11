package com.wealthvault.profile.di//
//import com.example.dashboard.data.RegisterDataSource
//import com.example.dashboard.data.RegisterRepositoryImpl
//import com.example.dashboard.usecase.RegisterUseCase
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.IO
//import org.koin.dsl.module
//
//object DashboardModule {
//    val allModules = module {
//        factory { RegisterDataSource(get()) }
//
//        single<RegisterRepositoryImpl> {
//            RegisterRepositoryImpl(
//                networkDataSource = get(),
//            )
//        }
//        single { Dispatchers.IO }
//
//        factory { RegisterUseCase(get(), get()) }
//
////        factory { RegisterScreenModel(get()) }
//    }
//}

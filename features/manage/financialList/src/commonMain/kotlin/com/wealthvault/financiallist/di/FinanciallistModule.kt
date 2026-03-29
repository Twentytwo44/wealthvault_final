package com.wealthvault.financiallist.di

import com.wealthvault.financiallist.data.FinanciallistDataSource
import com.wealthvault.financiallist.data.FinanciallistRepositoryImpl
import com.wealthvault.financiallist.ui.asset.AssetScreenModel
import com.wealthvault.financiallist.ui.debt.DebtScreenModel
import com.wealthvault.financiallist.usecase.FinanciallistUseCase
import org.koin.dsl.module

val financiallistModule = module {
    // 🌟 ใส่ get() ให้ครบ 7 ตัวตาม API ที่เรารับเข้ามา
    single { FinanciallistDataSource(get(), get(), get(), get(), get(), get(), get()) }
    single { FinanciallistRepositoryImpl(get()) }
    factory { FinanciallistUseCase(get()) }

    factory { AssetScreenModel(get()) }
    factory { DebtScreenModel(get()) }
}
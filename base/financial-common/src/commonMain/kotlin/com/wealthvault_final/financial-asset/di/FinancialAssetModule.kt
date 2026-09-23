package com.wealthvault.`financial-asset`.di

import com.wealthvault.`financial-asset`.ui.bankaccount.summary.BankAccountSummaryScreenModel
import com.wealthvault.`financial-asset`.ui.bankaccount.usecase.AddBankAccountUseCase
import com.wealthvault.`financial-asset`.ui.bankaccount.viewmodel.BankAccountScreenModel
import com.wealthvault.`financial-asset`.ui.cash.summary.CashSummaryScreenModel
import com.wealthvault.`financial-asset`.ui.cash.usecase.AddCashUseCase
import com.wealthvault.`financial-asset`.ui.cash.viewmodel.CashScreenModel
import com.wealthvault.`financial-asset`.ui.insurance.summary.InsuranceSummaryScreenModel
import com.wealthvault.`financial-asset`.ui.insurance.usecase.AddInsuranceUseCase
import com.wealthvault.`financial-asset`.ui.insurance.viewmodel.InsuranceScreenModel
import com.wealthvault.`financial-asset`.ui.realestate.building.summary.BuildingSummaryScreenModel
import com.wealthvault.`financial-asset`.ui.realestate.building.usecase.AddBuildingUseCase
import com.wealthvault.`financial-asset`.ui.realestate.building.viewmodel.BuildingScreenModel
import com.wealthvault.`financial-asset`.ui.realestate.land.summary.LandSummaryScreenModel
import com.wealthvault.`financial-asset`.ui.realestate.land.usecase.AddLandUseCase
import com.wealthvault.`financial-asset`.ui.realestate.land.viewmodel.LandScreenModel
import com.wealthvault.`financial-asset`.ui.share.ShareAssetScreenModel
import com.wealthvault.`financial-asset`.ui.stock.summary.SummaryScreenModel
import com.wealthvault.`financial-asset`.ui.stock.usecase.AddStockUseCase
import com.wealthvault.`financial-asset`.ui.stock.viewmodel.StockScreenModel
import com.wealthvault.`financial-obligations`.ui.expense.summary.ExpenseSummaryScreenModel
import com.wealthvault.`financial-obligations`.ui.expense.usecase.AddExpenseUseCase
import com.wealthvault.`financial-obligations`.ui.expense.viewmodel.ExpenseScreenModel
import com.wealthvault.`financial-obligations`.ui.liability.summary.LiabilitySummaryScreenModel
import com.wealthvault.`financial-obligations`.ui.liability.usecase.AddLiabilityUseCase
import com.wealthvault.`financial-obligations`.ui.liability.viewmodel.LiabilityScreenModel
import org.koin.dsl.module

object FinancialAssetModule {
    val allModules = module {
        factory { StockScreenModel()}
        factory { SummaryScreenModel(get(),get() )}
        factory { AddStockUseCase(get(),get()) }

        factory { CashScreenModel() }
        factory { CashSummaryScreenModel(get(), get()) }
        factory { AddCashUseCase(get(), get()) }

        factory { LandScreenModel(get()) }
        factory { LandSummaryScreenModel(get(), get()) }
        factory { AddLandUseCase(get(), get()) }

        factory { BuildingScreenModel(get(),get()) }
        factory { BuildingSummaryScreenModel(get(), get()) }
        factory { AddBuildingUseCase(get(), get()) }

        factory { BankAccountScreenModel() }
        factory { BankAccountSummaryScreenModel(get(), get()) }
        factory { AddBankAccountUseCase(get(), get()) }

        factory { InsuranceScreenModel() }
        factory { InsuranceSummaryScreenModel(get(), get()) }
        factory { AddInsuranceUseCase(get(), get()) }

        factory { LiabilityScreenModel() }
        factory { LiabilitySummaryScreenModel(get(), get()) }
        factory { AddLiabilityUseCase(get(), get()) }

        factory { ExpenseScreenModel() }
        factory { ExpenseSummaryScreenModel(get(), get()) }
        factory { AddExpenseUseCase(get(), get()) }

        factory { ShareAssetScreenModel<Any>(get(),get()) }



    }
}

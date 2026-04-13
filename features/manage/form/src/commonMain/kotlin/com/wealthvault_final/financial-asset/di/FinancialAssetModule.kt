package com.wealthvault_final.`financial-asset`.di

import com.wealthvault_final.`financial-asset`.data.bankaccount.BankAccountNetworkDataSource
import com.wealthvault_final.`financial-asset`.data.bankaccount.BankAccountRepositoryImpl
import com.wealthvault_final.`financial-asset`.data.building.BuildingNetworkDataSource
import com.wealthvault_final.`financial-asset`.data.building.BuildingRepositoryImpl
import com.wealthvault_final.`financial-asset`.data.building.GetBuildingNetworkDataSource
import com.wealthvault_final.`financial-asset`.data.building.GetBuildingRepositoryImpl
import com.wealthvault_final.`financial-asset`.data.cash.CashNetworkDataSource
import com.wealthvault_final.`financial-asset`.data.cash.CashRepositoryImpl
import com.wealthvault_final.`financial-asset`.data.friend.FriendNetworkDataSource
import com.wealthvault_final.`financial-asset`.data.friend.FriendRepositoryImpl
import com.wealthvault_final.`financial-asset`.data.group.GroupNetworkDataSource
import com.wealthvault_final.`financial-asset`.data.group.GroupRepositoryImpl
import com.wealthvault_final.`financial-asset`.data.insurance.GetInsuranceNetworkDataSource
import com.wealthvault_final.`financial-asset`.data.insurance.GetInsuranceRepositoryImpl
import com.wealthvault_final.`financial-asset`.data.insurance.InsuranceNetworkDataSource
import com.wealthvault_final.`financial-asset`.data.insurance.InsuranceRepositoryImpl
import com.wealthvault_final.`financial-asset`.data.land.GetLandNetworkDataSource
import com.wealthvault_final.`financial-asset`.data.land.GetLandRepositoryImpl
import com.wealthvault_final.`financial-asset`.data.land.LandNetworkDataSource
import com.wealthvault_final.`financial-asset`.data.land.LandRepositoryImpl
import com.wealthvault_final.`financial-asset`.data.share.ShareItemNetworkDataSource
import com.wealthvault_final.`financial-asset`.data.share.ShareItemRepositoryImpl
import com.wealthvault_final.`financial-asset`.data.stock.AssetNetworkDataSource
import com.wealthvault_final.`financial-asset`.data.stock.AssetRepositoryImpl
import com.wealthvault_final.`financial-asset`.ui.bankaccount.summary.BankAccountSummaryScreenModel
import com.wealthvault_final.`financial-asset`.ui.bankaccount.usecase.AddBankAccountUseCase
import com.wealthvault_final.`financial-asset`.ui.bankaccount.viewmodel.BankAccountScreenModel
import com.wealthvault_final.`financial-asset`.ui.cash.summary.CashSummaryScreenModel
import com.wealthvault_final.`financial-asset`.ui.cash.usecase.AddCashUseCase
import com.wealthvault_final.`financial-asset`.ui.cash.viewmodel.CashScreenModel
import com.wealthvault_final.`financial-asset`.ui.insurance.summary.InsuranceSummaryScreenModel
import com.wealthvault_final.`financial-asset`.ui.insurance.usecase.AddInsuranceUseCase
import com.wealthvault_final.`financial-asset`.ui.insurance.viewmodel.InsuranceScreenModel
import com.wealthvault_final.`financial-asset`.ui.realestate.building.summary.BuildingSummaryScreenModel
import com.wealthvault_final.`financial-asset`.ui.realestate.building.usecase.AddBuildingUseCase
import com.wealthvault_final.`financial-asset`.ui.realestate.building.viewmodel.BuildingScreenModel
import com.wealthvault_final.`financial-asset`.ui.realestate.land.summary.LandSummaryScreenModel
import com.wealthvault_final.`financial-asset`.ui.realestate.land.usecase.AddLandUseCase
import com.wealthvault_final.`financial-asset`.ui.realestate.land.viewmodel.LandScreenModel
import com.wealthvault_final.`financial-asset`.ui.share.ShareAssetScreenModel
import com.wealthvault_final.`financial-asset`.ui.stock.summary.SummaryScreenModel
import com.wealthvault_final.`financial-asset`.ui.stock.usecase.AddStockUseCase
import com.wealthvault_final.`financial-asset`.ui.stock.viewmodel.StockScreenModel
import com.wealthvault_final.`financial-obligations`.data.liability.LiabilityNetworkDataSource
import com.wealthvault_final.`financial-obligations`.data.liability.LiabilityRepositoryImpl
import com.wealthvault_final.`financial-obligations`.ui.expense.summary.ExpenseSummaryScreenModel
import com.wealthvault_final.`financial-obligations`.ui.expense.usecase.AddExpenseUseCase
import com.wealthvault_final.`financial-obligations`.ui.expense.viewmodel.ExpenseScreenModel
import com.wealthvault_final.`financial-obligations`.ui.liability.summary.LiabilitySummaryScreenModel
import com.wealthvault_final.`financial-obligations`.ui.liability.usecase.AddLiabilityUseCase
import com.wealthvault_final.`financial-obligations`.ui.liability.viewmodel.LiabilityScreenModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module

object FinancialAssetModule {
    val allModules = module {
        // stock
        factory { AssetNetworkDataSource(get()) }
        single<AssetRepositoryImpl> {
            AssetRepositoryImpl(
                networkDataSource = get(),
            )
        }
        factory { StockScreenModel()}
        factory { SummaryScreenModel(get(),get() )}
        factory { AddStockUseCase(get(),get()) }

        // cash
        factory { CashNetworkDataSource(get()) }
        single<CashRepositoryImpl> {
            CashRepositoryImpl(
                networkDataSource = get(),
            )
        }
        factory { CashScreenModel() }
        factory { CashSummaryScreenModel(get(), get()) }
        factory { AddCashUseCase(get(), get()) }

        // land
        factory { LandNetworkDataSource(get()) }
        single<LandRepositoryImpl> {
            LandRepositoryImpl(
                networkDataSource = get(),
            )
        }
        factory { GetLandNetworkDataSource(get()) }
        single<GetLandRepositoryImpl> {
            GetLandRepositoryImpl(
                networkDataSource = get(),
            )
        }
        factory { LandScreenModel(get()) }
        factory { LandSummaryScreenModel(get(), get()) }
        factory { AddLandUseCase(get(), get()) }

        // building
        factory { BuildingNetworkDataSource(get()) }
        single<BuildingRepositoryImpl> {
            BuildingRepositoryImpl(
                networkDataSource = get(),
            )
        }
        factory { GetBuildingNetworkDataSource(get()) }
        single<GetBuildingRepositoryImpl> {
            GetBuildingRepositoryImpl(
                networkDataSource = get(),
            )
        }
        factory { BuildingScreenModel(get(),get()) }
        factory { BuildingSummaryScreenModel(get(), get()) }
        factory { AddBuildingUseCase(get(), get()) }

        // bankaccount
        factory { BankAccountNetworkDataSource(get()) }
        single<BankAccountRepositoryImpl> {
            BankAccountRepositoryImpl(
                networkDataSource = get(),
            )
        }
        factory { BankAccountScreenModel() }
        factory { BankAccountSummaryScreenModel(get(), get()) }
        factory { AddBankAccountUseCase(get(), get()) }

        // insurance
        factory { InsuranceNetworkDataSource(get()) }
        single<InsuranceRepositoryImpl> {
            InsuranceRepositoryImpl(
                networkDataSource = get(),
            )
        }
        factory { GetInsuranceNetworkDataSource(get()) }
        single<GetInsuranceRepositoryImpl> {
            GetInsuranceRepositoryImpl(
                networkDataSource = get(),
            )
        }
        factory { InsuranceScreenModel() }
        factory { InsuranceSummaryScreenModel(get(), get()) }
        factory { AddInsuranceUseCase(get(), get()) }

        // liabilit and expense
        factory { LiabilityNetworkDataSource(get()) }
        single<LiabilityRepositoryImpl> {
            LiabilityRepositoryImpl(
                networkDataSource = get(),
            )
        }
        factory { LiabilityScreenModel() }
        factory { LiabilitySummaryScreenModel(get(), get()) }
        factory { AddLiabilityUseCase(get(), get()) }

        factory { ExpenseScreenModel() }
        factory { ExpenseSummaryScreenModel(get(), get()) }
        factory { AddExpenseUseCase(get(), get()) }

        // friend
        factory { FriendNetworkDataSource(get()) }
        single<FriendRepositoryImpl> {
            FriendRepositoryImpl(
                networkDataSource = get(),
            )
        }
        // group
        factory { GroupNetworkDataSource(get()) }
        single<GroupRepositoryImpl> {
            GroupRepositoryImpl(
                networkDataSource = get(),
            )
        }
        // share item
        factory { ShareItemNetworkDataSource(get()) }
        single<ShareItemRepositoryImpl> {
            ShareItemRepositoryImpl(
                networkDataSource = get(),
            )
        }
        single { Dispatchers.IO }
        factory { ShareAssetScreenModel<Any>(get(),get()) }



    }
}

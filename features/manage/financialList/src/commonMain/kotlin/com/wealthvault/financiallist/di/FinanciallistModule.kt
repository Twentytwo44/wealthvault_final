package com.wealthvault.financiallist.di



import com.wealthvault.financiallist.data.FinanciallistDataSource
import com.wealthvault.financiallist.data.FinanciallistRepositoryImpl
import com.wealthvault.financiallist.data.account.BankAccountNetworkDataSource
import com.wealthvault.financiallist.data.account.BankAccountRepositoryImpl
import com.wealthvault.financiallist.data.building.BuildingNetworkDataSource
import com.wealthvault.financiallist.data.building.BuildingRepositoryImpl
import com.wealthvault.financiallist.data.cash.CashNetworkDataSource
import com.wealthvault.financiallist.data.cash.CashRepositoryImpl
import com.wealthvault.financiallist.data.debt.LiabilityNetworkDataSource
import com.wealthvault.financiallist.data.debt.LiabilityRepositoryImpl
import com.wealthvault.financiallist.data.friend.FriendNetworkDataSource
import com.wealthvault.financiallist.data.friend.FriendRepositoryImpl
import com.wealthvault.financiallist.data.group.GroupNetworkDataSource
import com.wealthvault.financiallist.data.group.GroupRepositoryImpl
import com.wealthvault.financiallist.data.insurance.InsuranceNetworkDataSource
import com.wealthvault.financiallist.data.insurance.InsuranceRepositoryImpl
import com.wealthvault.financiallist.data.investment.AssetNetworkDataSource
import com.wealthvault.financiallist.data.investment.AssetRepositoryImpl
import com.wealthvault.financiallist.data.land.LandNetworkDataSource
import com.wealthvault.financiallist.data.land.LandRepositoryImpl
import com.wealthvault.financiallist.data.share.ShareItemNetworkDataSource
import com.wealthvault.financiallist.data.share.ShareItemRepositoryImpl
import com.wealthvault.financiallist.data.share.ShareTargetsNetworkDatasource
import com.wealthvault.financiallist.data.share.ShareTargetsRepositoryImpl
import com.wealthvault.financiallist.ui.asset.AssetScreenModel
import com.wealthvault.financiallist.ui.asset.form.account.BankAccountScreenModel
import com.wealthvault.financiallist.ui.asset.form.building.BuildingScreenModel
import com.wealthvault.financiallist.ui.asset.form.cash.CashScreenModel
import com.wealthvault.financiallist.ui.asset.form.insurance.InsuranceScreenModel
import com.wealthvault.financiallist.ui.asset.form.investment.StockScreenModel
import com.wealthvault.financiallist.ui.asset.form.land.LandScreenModel
import com.wealthvault.financiallist.ui.debt.DebtScreenModel
import com.wealthvault.financiallist.ui.debt.form.debt.LiabilityScreenModel
import com.wealthvault.financiallist.ui.debt.form.expense.ExpenseScreenModel
import com.wealthvault.financiallist.ui.shareasset.ShareScreenModel
import com.wealthvault.financiallist.ui.shareasset.usecase.GetShareAssetUseCase
import com.wealthvault.financiallist.usecase.FinanciallistUseCase
import org.koin.dsl.module

val financiallistModule = module {
    // 🌟 ใส่ get() ให้ครบ 7 ตัวตาม API ที่เรารับเข้ามา
    single { // หรือ factory {
        FinanciallistDataSource(
            get(), get(), get(), get(), get(), get(), get(), // 7 ตัวแรกของ Get All
            get(), get(), get(), get(), get(), get(), get(), // 7 ตัวของ Get By ID

            // 🌟 เพิ่ม get() ตรงนี้อีก 7 ตัว สำหรับ Delete API ครับ!
            get(), get(), get(), get(), get(), get(), get()
        )
    }
    single { FinanciallistRepositoryImpl(get()) }
    factory { FinanciallistUseCase(get()) }

    factory { AssetScreenModel(get(),get()) }
    factory { DebtScreenModel(get(),get()) }


    // share
    factory { ShareScreenModel(get(), get()) }
    factory { GetShareAssetUseCase(get(), get(), get()) }

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

    // share
    factory { ShareItemNetworkDataSource(get()) }
    single<ShareItemRepositoryImpl> {
        ShareItemRepositoryImpl(
            networkDataSource = get(),
        )
    }

    // share target
    factory { ShareTargetsNetworkDatasource(get()) }
    single<ShareTargetsRepositoryImpl> {
        ShareTargetsRepositoryImpl(
            networkDataSource = get(),
        )
    }

    // cash
    factory { CashScreenModel(get()) }
    factory { CashNetworkDataSource(get()) }
    single<CashRepositoryImpl> {
        CashRepositoryImpl(
            networkDataSource = get(),
        )
    }

    // building
    factory { BuildingScreenModel(get(),get(),get()) }
    factory { BuildingNetworkDataSource(get()) }
    single<BuildingRepositoryImpl> {
        BuildingRepositoryImpl(
            networkDataSource = get(),
        )
    }

    // insurance
    factory { InsuranceScreenModel(get()) }
    factory { InsuranceNetworkDataSource(get()) }
    single<InsuranceRepositoryImpl> {
        InsuranceRepositoryImpl(
            networkDataSource = get(),
        )
    }

    // stock
    factory { StockScreenModel(get()) }
    factory { AssetNetworkDataSource(get()) }
    single<AssetRepositoryImpl> {
        AssetRepositoryImpl(
            networkDataSource = get(),
        )
    }

    // land
    factory { LandScreenModel(get(),get()) }
    factory { LandNetworkDataSource(get()) }
    single<LandRepositoryImpl> {
        LandRepositoryImpl(
            networkDataSource = get(),
        )
    }
    // lia
    factory { LiabilityScreenModel(get()) }
    factory { ExpenseScreenModel(get()) }
    factory { LiabilityNetworkDataSource(get()) }
    single<LiabilityRepositoryImpl> {
        LiabilityRepositoryImpl(
            networkDataSource = get(),
        )
    }
    // acc
    factory { BankAccountScreenModel(get()) }
    factory { BankAccountNetworkDataSource(get()) }
    single<BankAccountRepositoryImpl> {
        BankAccountRepositoryImpl(
            networkDataSource = get(),
        )
    }








}

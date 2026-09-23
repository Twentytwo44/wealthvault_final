package com.wealthvault.data.portfolio

import com.wealthvault.data.portfolio.account.transport.di.AccountApiModule
import com.wealthvault.data.portfolio.building.transport.di.BuildingApiModule
import com.wealthvault.data.portfolio.cash.transport.di.CashApiModule
import com.wealthvault.data.portfolio.insurance.transport.di.InsuranceApiModule
import com.wealthvault.data.portfolio.investment.transport.di.InvestmentApiModule
import com.wealthvault.data.portfolio.land.transport.di.LandApiModule
import com.wealthvault.data.portfolio.liability.transport.di.LiabilityApiModule
import com.wealthvault.core.KoinConst
import com.wealthvault.domain.portfolio.BuildingReferenceRepository
import com.wealthvault.domain.portfolio.CreateBankAccountRepository
import com.wealthvault.domain.portfolio.CreateBuildingRepository
import com.wealthvault.domain.portfolio.CreateCashRepository
import com.wealthvault.domain.portfolio.CreateInsuranceRepository
import com.wealthvault.domain.portfolio.CreateInvestmentRepository
import com.wealthvault.domain.portfolio.CreateLandRepository
import com.wealthvault.domain.portfolio.CreateLiabilityRepository
import com.wealthvault.domain.portfolio.PortfolioRepository
import com.wealthvault.domain.portfolio.UpdateBankAccountRepository
import com.wealthvault.domain.portfolio.UpdateBuildingRepository
import com.wealthvault.domain.portfolio.UpdateCashRepository
import com.wealthvault.domain.portfolio.UpdateInsuranceRepository
import com.wealthvault.domain.portfolio.UpdateInvestmentRepository
import com.wealthvault.domain.portfolio.UpdateLandRepository
import com.wealthvault.domain.portfolio.UpdateLiabilityRepository
import com.wealthvault.domain.profile.FriendDirectoryRepository
import com.wealthvault.domain.social.GroupDirectoryRepository
import com.wealthvault.data.portfolio.repository.FinanciallistDataSource
import com.wealthvault.data.portfolio.repository.FinanciallistRemoteDataSource
import com.wealthvault.data.portfolio.repository.FinanciallistRepositoryImpl
import com.wealthvault.data.portfolio.repository.account.BankAccountNetworkDataSource
import com.wealthvault.data.portfolio.repository.account.BankAccountRepositoryImpl
import com.wealthvault.data.portfolio.repository.building.BuildingNetworkDataSource
import com.wealthvault.data.portfolio.repository.building.BuildingRepositoryImpl
import com.wealthvault.data.portfolio.repository.cash.CashNetworkDataSource
import com.wealthvault.data.portfolio.repository.cash.CashRepositoryImpl
import com.wealthvault.data.portfolio.repository.debt.LiabilityNetworkDataSource
import com.wealthvault.data.portfolio.repository.debt.LiabilityRepositoryImpl
import com.wealthvault.data.portfolio.repository.friend.FriendRepositoryImpl
import com.wealthvault.data.portfolio.repository.group.GroupRepositoryImpl
import com.wealthvault.data.portfolio.repository.insurance.InsuranceNetworkDataSource
import com.wealthvault.data.portfolio.repository.insurance.InsuranceRepositoryImpl
import com.wealthvault.data.portfolio.repository.investment.AssetNetworkDataSource
import com.wealthvault.data.portfolio.repository.investment.AssetRepositoryImpl
import com.wealthvault.data.portfolio.repository.land.LandNetworkDataSource
import com.wealthvault.data.portfolio.repository.land.LandRepositoryImpl
import com.wealthvault.data.portfolio.form.asset.data.bankaccount.BankAccountNetworkDataSource as LegacyBankAccountNetworkDataSource
import com.wealthvault.data.portfolio.form.asset.data.bankaccount.BankAccountRepositoryImpl as LegacyBankAccountRepositoryImpl
import com.wealthvault.data.portfolio.form.asset.data.building.BuildingNetworkDataSource as LegacyBuildingNetworkDataSource
import com.wealthvault.data.portfolio.form.asset.data.building.BuildingRepositoryImpl as LegacyBuildingRepositoryImpl
import com.wealthvault.data.portfolio.form.asset.data.building.GetBuildingNetworkDataSource as LegacyGetBuildingNetworkDataSource
import com.wealthvault.data.portfolio.form.asset.data.building.GetBuildingRepositoryImpl as LegacyGetBuildingRepositoryImpl
import com.wealthvault.data.portfolio.form.asset.data.cash.CashNetworkDataSource as LegacyCashNetworkDataSource
import com.wealthvault.data.portfolio.form.asset.data.cash.CashRepositoryImpl as LegacyCashRepositoryImpl
import com.wealthvault.data.portfolio.form.asset.data.insurance.GetInsuranceNetworkDataSource as LegacyGetInsuranceNetworkDataSource
import com.wealthvault.data.portfolio.form.asset.data.insurance.GetInsuranceRepositoryImpl as LegacyGetInsuranceRepositoryImpl
import com.wealthvault.data.portfolio.form.asset.data.insurance.InsuranceNetworkDataSource as LegacyInsuranceNetworkDataSource
import com.wealthvault.data.portfolio.form.asset.data.insurance.InsuranceRepositoryImpl as LegacyInsuranceRepositoryImpl
import com.wealthvault.data.portfolio.form.asset.data.land.GetLandNetworkDataSource as LegacyGetLandNetworkDataSource
import com.wealthvault.data.portfolio.form.asset.data.land.GetLandRepositoryImpl as LegacyGetLandRepositoryImpl
import com.wealthvault.data.portfolio.form.asset.data.land.LandNetworkDataSource as LegacyLandNetworkDataSource
import com.wealthvault.data.portfolio.form.asset.data.land.LandRepositoryImpl as LegacyLandRepositoryImpl
import com.wealthvault.data.portfolio.form.asset.data.stock.AssetNetworkDataSource as LegacyAssetNetworkDataSource
import com.wealthvault.data.portfolio.form.asset.data.stock.AssetRepositoryImpl as LegacyAssetRepositoryImpl
import com.wealthvault.data.portfolio.form.obligation.data.liability.LiabilityNetworkDataSource as LegacyLiabilityNetworkDataSource
import com.wealthvault.data.portfolio.form.obligation.data.liability.LiabilityRepositoryImpl as LegacyLiabilityRepositoryImpl
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * DI boundary for portfolio and sharing transport adapters.
 *
 * Endpoint adapters are owned by this bounded-context module. App composition
 * depends on this facade rather than importing each endpoint implementation
 * directly.
 */
object PortfolioDataModule {
    val allModules: List<Module> = listOf(
        AccountApiModule.allModules,
        BuildingApiModule.allModules,
        CashApiModule.allModules,
        InsuranceApiModule.allModules,
        InvestmentApiModule.allModules,
        LandApiModule.allModules,
        LiabilityApiModule.allModules,
        portfolioRepositoriesModule,
    )
}

/**
 * Data bindings for the portfolio presentation surface.
 *
 * The feature keeps its screens and use cases, while all endpoint-facing
 * repositories are assembled here so the composition root owns the data
 * implementation boundary.
 */
private val portfolioRepositoriesModule = module {
    single<FinanciallistRemoteDataSource> {
        FinanciallistDataSource(
            get(), get(), get(), get(), get(), get(), get(),
            get(), get(), get(), get(), get(), get(), get(),
            get(), get(), get(), get(), get(), get(), get(),
        )
    }
    single<PortfolioRepository> {
        FinanciallistRepositoryImpl(
            dataSource = get(),
            cache = get(),
            json = get(named(KoinConst.KotlinSerialization.GLOBAL)),
        )
    }

    single<FriendDirectoryRepository> { FriendRepositoryImpl(get()) }
    single<GroupDirectoryRepository> { GroupRepositoryImpl(get()) }

    // Form repositories remain domain-facing, but their endpoint
    // implementations are owned by data:portfolio rather than presentation.
    factory { LegacyBankAccountNetworkDataSource(get()) }
    single<CreateBankAccountRepository> { LegacyBankAccountRepositoryImpl(get(), get()) }
    factory { LegacyBuildingNetworkDataSource(get()) }
    single<CreateBuildingRepository> { LegacyBuildingRepositoryImpl(get(), get()) }
    factory { LegacyGetBuildingNetworkDataSource(get()) }
    single<BuildingReferenceRepository> {
        LegacyGetBuildingRepositoryImpl(
            networkDataSource = get(),
            cache = get(),
            json = get(named(KoinConst.KotlinSerialization.GLOBAL)),
        )
    }
    factory { LegacyCashNetworkDataSource(get()) }
    single<CreateCashRepository> { LegacyCashRepositoryImpl(get(), get()) }
    factory { LegacyInsuranceNetworkDataSource(get()) }
    single<CreateInsuranceRepository> { LegacyInsuranceRepositoryImpl(get(), get()) }
    factory { LegacyGetInsuranceNetworkDataSource(get()) }
    single<com.wealthvault.domain.portfolio.InsuranceReferenceRepository> {
        LegacyGetInsuranceRepositoryImpl(
            networkDataSource = get(),
            cache = get(),
            json = get(named(KoinConst.KotlinSerialization.GLOBAL)),
        )
    }
    factory { LegacyAssetNetworkDataSource(get()) }
    single<CreateInvestmentRepository> { LegacyAssetRepositoryImpl(get(), get()) }
    factory { LegacyLandNetworkDataSource(get()) }
    single<CreateLandRepository> { LegacyLandRepositoryImpl(get(), get()) }
    factory { LegacyGetLandNetworkDataSource(get()) }
    single<com.wealthvault.domain.portfolio.LandReferenceRepository> {
        LegacyGetLandRepositoryImpl(
            networkDataSource = get(),
            cache = get(),
            json = get(named(KoinConst.KotlinSerialization.GLOBAL)),
        )
    }
    factory { LegacyLiabilityNetworkDataSource(get()) }
    single<CreateLiabilityRepository> { LegacyLiabilityRepositoryImpl(get(), get()) }

    factory { CashNetworkDataSource(get()) }
    single<UpdateCashRepository> { CashRepositoryImpl(get(), get()) }
    factory { BuildingNetworkDataSource(get()) }
    single<UpdateBuildingRepository> { BuildingRepositoryImpl(get(), get()) }
    factory { InsuranceNetworkDataSource(get()) }
    single<UpdateInsuranceRepository> { InsuranceRepositoryImpl(get(), get()) }
    factory { AssetNetworkDataSource(get()) }
    single<UpdateInvestmentRepository> { AssetRepositoryImpl(get(), get()) }
    factory { LandNetworkDataSource(get()) }
    single<UpdateLandRepository> { LandRepositoryImpl(get(), get()) }
    factory { LiabilityNetworkDataSource(get()) }
    single<UpdateLiabilityRepository> { LiabilityRepositoryImpl(get(), get()) }
    factory { BankAccountNetworkDataSource(get()) }
    single<UpdateBankAccountRepository> { BankAccountRepositoryImpl(get(), get()) }

}

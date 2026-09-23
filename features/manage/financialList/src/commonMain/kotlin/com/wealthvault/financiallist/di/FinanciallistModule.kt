package com.wealthvault.financiallist.di



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
import com.wealthvault.domain.portfolio.BuildingReferenceRepository
import com.wealthvault.domain.portfolio.InsuranceReferenceRepository
import com.wealthvault.domain.portfolio.LandReferenceRepository
import com.wealthvault.domain.portfolio.UpdateBankAccountRepository
import com.wealthvault.domain.portfolio.UpdateBuildingRepository
import com.wealthvault.domain.portfolio.UpdateCashRepository
import com.wealthvault.domain.portfolio.UpdateInsuranceRepository
import com.wealthvault.domain.portfolio.UpdateInvestmentRepository
import com.wealthvault.domain.portfolio.UpdateLandRepository
import com.wealthvault.domain.portfolio.UpdateLiabilityRepository
import com.wealthvault.domain.profile.FriendDirectoryRepository
import com.wealthvault.domain.social.GroupDirectoryRepository
import com.wealthvault.domain.social.ShareItemRepository
import com.wealthvault.domain.social.ShareTargetsRepository
import com.wealthvault.domain.social.UnshareRepository

val financiallistModule = module {
    factory { FinanciallistUseCase(get()) }

    factory { AssetScreenModel(get(), get(), get()) }
    factory { DebtScreenModel(get(),get()) }


    // share
    factory { ShareScreenModel(get(), get(), get()) }
    factory { GetShareAssetUseCase(get(), get(), get()) }

    factory { CashScreenModel(get(), get()) }
    factory { BuildingScreenModel(get(),get(),get(),get()) }
    factory { InsuranceScreenModel(get(), get()) }
    factory { StockScreenModel(get(), get()) }
    factory { LandScreenModel(get(),get(),get()) }
    factory { LiabilityScreenModel(get(), get()) }
    factory { ExpenseScreenModel(get(), get()) }
    factory { BankAccountScreenModel(get(), get()) }

}

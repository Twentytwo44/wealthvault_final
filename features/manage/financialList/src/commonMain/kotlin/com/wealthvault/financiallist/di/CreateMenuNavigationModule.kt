package com.wealthvault.financiallist.di

import cafe.adriel.voyager.core.registry.screenModule
import com.wealthvault.core.navigation.SharedScreen
import com.wealthvault.financiallist.ui.menu.DebtCreateMenuScreen
import com.wealthvault.financiallist.ui.menu.FinancialCreateMenuScreen
import com.wealthvault.financiallist.ui.menu.RealEstateCreateMenuScreen

/** Navigation entry points for create flows owned by this feature. */
val financialListCreateMenuScreenModule = screenModule {
    register<SharedScreen.FinancialMenu> { FinancialCreateMenuScreen() }
    register<SharedScreen.DebtMenu> { DebtCreateMenuScreen() }
    register<SharedScreen.CreateRealEstate> { RealEstateCreateMenuScreen() }
    register<SharedScreen.CreateCash> { com.wealthvault.financiallist.ui.asset.form.cash.CreateCashFormScreen }
    register<SharedScreen.CreateBankAccount> { com.wealthvault.financiallist.ui.asset.form.account.CreateBankAccountFormScreen }
    register<SharedScreen.CreateInvestment> { com.wealthvault.financiallist.ui.asset.form.investment.CreateStockFormScreen }
    register<SharedScreen.CreateInsurance> { com.wealthvault.financiallist.ui.asset.form.insurance.CreateInsuranceFormScreen }
    register<SharedScreen.CreateBuilding> { com.wealthvault.financiallist.ui.asset.form.building.CreateBuildingFormScreen }
    register<SharedScreen.CreateLand> { com.wealthvault.financiallist.ui.asset.form.land.CreateLandFormScreen }
    register<SharedScreen.CreateLiability> { com.wealthvault.financiallist.ui.debt.form.debt.CreateLiabilityFormScreen }
    register<SharedScreen.CreateExpense> { com.wealthvault.financiallist.ui.debt.form.expense.CreateExpenseFormScreen }
}

package com.wealthvault.financiallist

import com.wealthvault.financiallist.ui.asset.AssetUiData
import com.wealthvault.financiallist.ui.debt.DebtUiData
import kotlin.test.Test
import kotlin.test.assertTrue

class FinancialListAndroidContractTest {
    @Test
    fun portfolioListsStartEmptyBeforeTheRepositoryLoad() {
        assertTrue(AssetUiData().accounts.isEmpty())
        assertTrue(AssetUiData().cashes.isEmpty())
        assertTrue(DebtUiData().loans.isEmpty())
        assertTrue(DebtUiData().expenses.isEmpty())
    }
}

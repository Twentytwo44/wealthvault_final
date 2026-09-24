package com.wealthvault.financiallist

import kotlin.test.Test
import com.wealthvault.core.model.Money
import kotlin.test.assertEquals

class FinancialListContractTest {
    @Test
    fun moneyIsStoredAsMinorUnits() {
        val money = Money(minorUnits = 12345)

        assertEquals(12345L, money.minorUnits)
        assertEquals("THB", money.currencyCode)
    }
}

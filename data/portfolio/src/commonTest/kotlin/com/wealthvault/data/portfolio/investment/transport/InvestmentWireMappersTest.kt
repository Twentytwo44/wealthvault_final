package com.wealthvault.data.portfolio.investment.transport

import com.wealthvault.core.model.FixedDecimal
import com.wealthvault.core.model.Money
import com.wealthvault.domain.portfolio.InvestmentRequest
import kotlin.test.Test
import kotlin.test.assertEquals

class InvestmentWireMappersTest {
    @Test
    fun fixedPointCommandValuesAreFormattedOnlyAtTheWireBoundary() {
        val request = InvestmentRequest(
            quantity = FixedDecimal.fromDecimal("10.5", scale = 4),
            costPerPrice = Money.fromDecimal("20.25"),
        )

        val wire = request.toWire()

        assertEquals("10.5000", wire.quantity)
        assertEquals("20.25", wire.costPerPrice)
    }
}

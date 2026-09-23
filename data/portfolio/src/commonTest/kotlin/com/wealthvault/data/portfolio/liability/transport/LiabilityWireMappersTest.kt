package com.wealthvault.data.portfolio.liability.transport

import com.wealthvault.core.model.FixedDecimal
import com.wealthvault.core.model.Money
import com.wealthvault.domain.portfolio.LiabilityRequest
import kotlin.test.Test
import kotlin.test.assertEquals

class LiabilityWireMappersTest {
    @Test
    fun fixedPointInterestRateIsFormattedOnlyAtTheWireBoundary() {
        val request = LiabilityRequest(
            principal = Money.fromDecimal("4000"),
            interestRate = FixedDecimal.fromDecimal("5.25", scale = 4),
        )

        val wire = request.toWire()

        assertEquals(4000.0, wire.principal)
        assertEquals("5.2500", wire.interestRate)
    }
}

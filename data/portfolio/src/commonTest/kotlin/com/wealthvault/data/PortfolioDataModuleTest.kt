package com.wealthvault.data

import com.wealthvault.data.portfolio.PortfolioDataModule
import kotlin.test.Test
import kotlin.test.assertEquals

class PortfolioDataModuleTest {
    @Test
    fun exposesApiAndRepositoryModulesFromOneCompositionBoundary() {
        val modules = PortfolioDataModule.allModules

        assertEquals(8, modules.size)
    }
}

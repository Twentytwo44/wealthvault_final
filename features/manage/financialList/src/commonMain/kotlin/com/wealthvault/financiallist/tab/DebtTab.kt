package com.wealthvault.financiallist.tab

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.registry.screenModule
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_nav_debt
import com.wealthvault.financiallist.ui.debt.DebtScreen
import com.wealthvault.main.SharedScreen
import org.jetbrains.compose.resources.painterResource

object DebtTab : Tab {
    override val options: TabOptions
        @Composable
        get() = TabOptions(
            index = 2u,
            title = "หนี้สิน",
            icon = painterResource(Res.drawable.ic_nav_debt)
        )

    @Composable
    override fun Content() {
        // 🌟 2. ครอบด้วย Navigator
        cafe.adriel.voyager.navigator.Navigator(DebtScreen())
    }
}

val debtTabModule = screenModule {
    register<SharedScreen.DebtTab> { DebtTab }
}


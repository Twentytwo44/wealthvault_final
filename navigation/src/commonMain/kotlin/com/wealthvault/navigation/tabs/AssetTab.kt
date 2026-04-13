package com.wealthvault.navigation.tabs

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator // 🌟 1. นำเข้า Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_nav_asset
import com.wealthvault.financiallist.ui.asset.AssetScreen
import org.jetbrains.compose.resources.painterResource

object AssetTab : Tab {

    override val options: TabOptions
        @Composable
        get() = TabOptions(
            index = 3u,
            title = "ทรัพย์สิน",
            icon = painterResource(Res.drawable.ic_nav_asset)
        )

    @Composable
    override fun Content() {
        // 🌟 2. ครอบด้วย Navigator
        Navigator(AssetScreen())
    }
}
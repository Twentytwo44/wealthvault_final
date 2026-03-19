package com.wealthvault.navigation.tabs

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_nav_social
import com.wealthvault.social.ui.SocialScreen
import org.jetbrains.compose.resources.painterResource

object SocialTab : Tab {
    override val options: TabOptions
        @Composable
        get() = TabOptions(
            index = 3u,
            title = "โซเชียล",
            icon = painterResource(Res.drawable.ic_nav_social)
        )

    @Composable
    override fun Content() {
        SocialScreen()

    }
}

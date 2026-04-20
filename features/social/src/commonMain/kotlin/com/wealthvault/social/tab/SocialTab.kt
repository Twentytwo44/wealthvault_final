package com.wealthvault.social.tab

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.registry.screenModule
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_nav_social
import com.wealthvault.main.SharedScreen
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
        // 🌟 2. ครอบ SocialScreen ด้วย Navigator
        cafe.adriel.voyager.navigator.Navigator(SocialScreen())
    }
}


val socialTabModule = screenModule {
    register<SharedScreen.SocialTab> { SocialTab }
}

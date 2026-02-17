package com.wealthvault.navigation.tabs

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.social
import org.jetbrains.compose.resources.painterResource

object SocialTab : Tab {
    override val options: TabOptions
        @Composable
        get() = TabOptions(
            index = 3u,
            title = "Social",
//            icon = painterResource(Res.drawable.social)
        )

    @Composable
    override fun Content() {
        Text("Social Screen")
    }
}

package com.wealthvault.navigation.tabs

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_nav_profile
import com.wealthvault.profile.ui.ProfileScreen
import org.jetbrains.compose.resources.painterResource

object ProfileTab : Tab {
    override val options: TabOptions
        @Composable
        get() = TabOptions(
            index = 3u,
            title = "โปรไฟล์",
            icon = painterResource(Res.drawable.ic_nav_profile)
        )

    @Composable
    override fun Content() {
        ProfileScreen(
            onEditProfileClick = {},
            onLogoutClick = {}
        )
    }
}

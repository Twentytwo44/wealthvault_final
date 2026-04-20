package com.wealthvault.profile.tab

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.registry.screenModule
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_nav_profile
import com.wealthvault.main.SharedScreen
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
        val rootNavigator = LocalNavigator.currentOrThrow.parent ?: LocalNavigator.currentOrThrow

        Navigator(
            ProfileScreen(

            )
        )
    }
}


val profileTabModule = screenModule {
    register<SharedScreen.ProfileTab> { ProfileTab }
}

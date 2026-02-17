package com.wealthvault.navigation.tabs

//import com.example.navigation.generated.resources.asset

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.asset
import org.jetbrains.compose.resources.painterResource


object AssetTab : Tab {


    override val options: TabOptions
        @Composable
        get() = TabOptions(
            index = 3u,
            title = "Social",
//            icon = painterResource(Res.drawable.asset)
        )

    @Composable
    override fun Content() {
        Text("aset Screen")
    }
}

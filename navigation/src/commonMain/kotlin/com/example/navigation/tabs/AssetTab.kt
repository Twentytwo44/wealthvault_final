package com.example.navigation.tabs

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
//import com.example.navigation.generated.resources.asset

import org.jetbrains.compose.resources.painterResource


object AssetTab : Tab {
    var tabIcon: Painter? = null

    override val options: TabOptions
        @Composable
        get() = TabOptions(
            index = 3u,
            title = "Social",
            icon = tabIcon ?: null
        )

    @Composable
    override fun Content() {
        Text("aset Screen")
    }
}
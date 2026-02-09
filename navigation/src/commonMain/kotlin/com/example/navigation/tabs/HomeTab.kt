package com.example.navigation.tabs

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions

import org.jetbrains.compose.resources.painterResource


object HomeTab : Tab {
    override val options: TabOptions
        @Composable
        get() = TabOptions(
            index = 0u,
            title = "Home",
//            icon = painterResource(Res.drawable.home)
        )

    @Composable
    override fun Content() {
        Text("home Screen")
    }
}
package com.example.navigation.tabs

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions



object DebtTab : Tab {
    override val options: TabOptions
        @Composable
        get() = TabOptions(
            index = 2u,
            title = "Debt",
//            icon = painterResource(Res.drawable.debt)
        )

    @Composable
    override fun Content() {
        Text("debt Screen")
    }
}
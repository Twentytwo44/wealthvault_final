package com.example.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.example.core.generated.resources.asset
import com.example.navigation.components.TabItem
import com.example.navigation.tabs.HomeTab
import org.jetbrains.compose.resources.painterResource

import wealthvault_final.navigation.generated.resources.Res

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {

    TabNavigator(HomeTab) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {

            },
            bottomBar = {
                NavigationBar(
                    windowInsets = WindowInsets(0.dp),
                    modifier = Modifier

                        .graphicsLayer {
                            shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
                            clip = true
                        }
                ) {
                    TabItem(HomeTab, painterResource(Res.drawable.asset))
//                    TabItem(AssetTab, painterResource(Res.drawable.asset))
//                    TabItem(DebtTab, painterResource(Res.drawable.debt))
//                    TabItem(SocialTab, painterResource(Res.drawable.social))
//                    TabItem(ProfileTab, painterResource(Res.drawable.profile))
                }
            }
        ) { padding ->
            Box(Modifier.padding(padding)) {
                CurrentTab()
            }
        }
    }
}

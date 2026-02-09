package com.wealthvault.navigation

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
import com.wealthvault.navigation.components.TabItem
import com.wealthvault.navigation.tabs.AssetTab
import com.wealthvault.navigation.tabs.DebtTab
import com.wealthvault.navigation.tabs.HomeTab
import com.wealthvault.navigation.tabs.ProfileTab
import com.wealthvault.navigation.tabs.SocialTab

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

                    TabItem(HomeTab)
                    TabItem(AssetTab)
                    TabItem(DebtTab)
                    TabItem(SocialTab)
                    TabItem(ProfileTab)
                }
            }
        ) { padding ->
            Box(Modifier.padding(padding)) {
                CurrentTab()
            }
        }
    }
}

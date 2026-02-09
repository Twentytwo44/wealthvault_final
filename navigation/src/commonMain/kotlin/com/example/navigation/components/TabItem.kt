package com.example.navigation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.example.navigation.tabs.AssetTab
import com.example.navigation.tabs.DebtTab
import com.example.navigation.tabs.HomeTab
import com.example.navigation.tabs.ProfileTab
import com.example.navigation.tabs.SocialTab


@Composable
fun TabItem(tab: Tab,iconPainter: androidx.compose.ui.graphics.painter.Painter) {
    val tabNavigator = LocalTabNavigator.current

    Box(
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(MaterialTheme.colorScheme.surface)
                .align(Alignment.BottomEnd)
                .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomBarItem(DebtTab, tabNavigator,iconPainter)
            BottomBarItem(AssetTab, tabNavigator)
            BottomBarItem(HomeTab, tabNavigator)
            BottomBarItem(SocialTab, tabNavigator)
            BottomBarItem(ProfileTab, tabNavigator)

        }
    }
}

@Composable
fun BottomBarItem(
    tab: Tab,
    tabNavigator: TabNavigator,
    customIcon: androidx.compose.ui.graphics.painter.Painter? = null
) {
    val selected = tabNavigator.current == tab

    Column(
        modifier = Modifier
            .clickable { tabNavigator.current = tab }
            .background(
                color = if (selected)
                    MaterialTheme.colorScheme.secondary
                else
                    MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            ).padding(horizontal = 13.dp, vertical = 7.dp)
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val iconPainter = customIcon ?: tab.options.icon

        iconPainter?.let {
            Icon(
                painter = it,
                contentDescription = tab.options.title,
                tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }

        Text(
            text = tab.options.title,
            fontSize = 12.sp,
            color = if (selected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}

package com.wealthvault.navigation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightBGNavbar
import com.wealthvault.core.theme.UnselectedColor
import com.wealthvault.core.theme.WealthVaultTheme


@Composable
fun BottomBarItem(
    tab: Tab,
    tabNavigator: TabNavigator
) {
    val selected = tabNavigator.current == tab
    WealthVaultTheme {
        Column(
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null, // ปิดเอฟเฟกต์กระเพื่อมสีเทาตอนกด
                    onClick = { tabNavigator.current = tab }
                )
                .background(
                    color = if (selected) LightBGNavbar else Color.Transparent,
                    shape = RoundedCornerShape(18.dp)
                )
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val iconPainter = tab.options.icon

            iconPainter?.let {
                Icon(
                    painter = it,
                    contentDescription = tab.options.title,
                    tint = if (selected) LightPrimary else UnselectedColor,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = tab.options.title,
                style = MaterialTheme.typography.labelSmall,
                color = if (selected) LightPrimary else UnselectedColor,
                fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal
            )
        }
    }
}
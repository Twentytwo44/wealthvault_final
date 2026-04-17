package com.wealthvault.social.ui.components.space

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_back
import com.wealthvault.core.generated.resources.ic_social_menu_dots
import com.wealthvault.core.theme.LightPrimary
import org.jetbrains.compose.resources.painterResource

@Composable
fun SpaceTopBar(
    title: String,
    onBackClick: () -> Unit,
    showMoreOption: Boolean = false, // หน้า Space มี 3 จุด แต่หน้าอื่นไม่มี
    onMoreClick: () -> Unit = {},

) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 12.dp).padding(horizontal = 20.dp)
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_common_back),
            contentDescription = "Back",
            tint = LightPrimary, // 🌟 ใช้ LightPrimary
            modifier = Modifier.size(24.dp).clickable { onBackClick() }
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, style = MaterialTheme.typography.titleLarge, color = LightPrimary)
        Spacer(modifier = Modifier.weight(1f))
        if (showMoreOption) {
            Icon(
                painter = painterResource(Res.drawable.ic_social_menu_dots),
                contentDescription = "Back",
                tint = LightPrimary, // 🌟 ใช้ LightPrimary
                modifier = Modifier.size(24.dp).clickable { onMoreClick() }
            )
        }
    }
}
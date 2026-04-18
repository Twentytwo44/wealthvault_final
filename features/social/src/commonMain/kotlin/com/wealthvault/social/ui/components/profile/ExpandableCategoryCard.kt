package com.wealthvault.social.ui.components.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_down_line
import com.wealthvault.core.generated.resources.ic_common_up_line
import com.wealthvault.core.generated.resources.ic_common_bin  // 🌟 ต้องมี resource นี้
import com.wealthvault.core.generated.resources.ic_common_pen    // 🌟 ต้องมี resource นี้
import com.wealthvault.core.generated.resources.ic_dashboard_share   // 🌟 ต้องมี resource นี้
import com.wealthvault.core.theme.LightAsset
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightDebt
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSecondary
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault.core.theme.WvBgGradientStart
import org.jetbrains.compose.resources.painterResource

@Composable
fun ExpandableCategoryCard(
    title: String,
    itemCount: Int,
    themeColor: String, // "asset" หรือ "debt"
    initiallyExpanded: Boolean = true,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (themeColor == "asset") {
                            Brush.linearGradient(colors = listOf(LightAsset, LightSecondary))
                        } else {
                            Brush.linearGradient(colors = listOf(LightDebt, LightSecondary))
                        }
                    )
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = LightPrimary,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "$itemCount รายการ",
                style = MaterialTheme.typography.bodyMedium,
                color = LightPrimary
            )
            Spacer(modifier = Modifier.width(4.dp))

            Icon(
                painter = if (expanded) painterResource(Res.drawable.ic_common_up_line) else painterResource(Res.drawable.ic_common_down_line),
                contentDescription = null,
                tint = LightPrimary.copy(alpha = 0.8f),
                modifier = Modifier.size(20.dp)
            )
        }

        HorizontalDivider(color = LightPrimary.copy(alpha = 0.3f), thickness = 1.dp)

        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
fun RealItemCard(
    title: String,
    subtitleLabel: String,
    subtitleValue: String,
    amountLabel: String,
    amountValue: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = LightSoftWhite
        ),
        border = BorderStroke(1.dp, LightBorder),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xFF3A2F2A))
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = subtitleLabel, fontSize = 14.sp, color = Color.Gray)
                Text(text = subtitleValue, fontSize = 14.sp, color = Color(0xFF3A2F2A))
            }
            Spacer(modifier = Modifier.height(2.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = amountLabel, fontSize = 14.sp, color = Color.Gray)
                Text(text = amountValue, fontSize = 14.sp, color = Color(0xFF3A2F2A))
            }
        }
    }
}


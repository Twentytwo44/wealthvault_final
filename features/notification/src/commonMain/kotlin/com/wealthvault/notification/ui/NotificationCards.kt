package com.wealthvault.notification.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_solid_right
import com.wealthvault.core.generated.resources.ic_form_email_outline
import com.wealthvault.core.generated.resources.ic_nav_profile
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightMuted
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault.core.theme.LightText
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
private fun NotificationLeadingIcon(iconRes: DrawableResource, isRead: Boolean) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(if (isRead) LightBg else LightSoftWhite),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = if (isRead) LightMuted else LightPrimary,
            modifier = Modifier.size(16.dp),
        )
    }
}

@Composable
fun StandardNotificationCard(
    title: String,
    time: String,
    isRead: Boolean,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isRead) LightSoftWhite.copy(0.6f) else LightSoftWhite,
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, LightBorder),
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
            ) {
                NotificationLeadingIcon(iconRes = Res.drawable.ic_form_email_outline, isRead = isRead)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isRead) LightMuted else LightText,
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (!isRead) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFE57373)))
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(
                    text = time.take(10),
                    style = MaterialTheme.typography.labelSmall,
                    color = LightMuted,
                )
            }
        }
    }
}

@Composable
fun InviteNotificationCard(
    title: String,
    time: String,
    isCompleted: Boolean,
    isRead: Boolean,
    onNavigateToAddFriend: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isRead) LightSoftWhite.copy(0.6f) else LightSoftWhite,
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, LightBorder),
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
            ) {
                NotificationLeadingIcon(iconRes = Res.drawable.ic_nav_profile, isRead = isRead)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isRead) LightMuted else LightText,
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!isRead) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFE57373)))
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = time.take(10),
                        style = MaterialTheme.typography.labelSmall,
                        color = LightMuted,
                    )
                }

                if (isCompleted) {
                    Text(
                        text = "✓ ตอบรับคำขอแล้ว",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isRead) LightMuted else LightPrimary,
                        fontWeight = FontWeight.Bold,
                    )
                } else {
                    Button(
                        onClick = onNavigateToAddFriend,
                        colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.height(32.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    ) {
                        Text("จัดการคำขอ", color = Color.White, fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            painter = painterResource(Res.drawable.ic_common_solid_right),
                            contentDescription = null,
                            tint = LightSoftWhite,
                            modifier = Modifier.size(12.dp),
                        )
                    }
                }
            }
        }
    }
}

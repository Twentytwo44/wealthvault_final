package com.wealthvault.notification.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_back
import com.wealthvault.core.generated.resources.ic_common_solid_right
import com.wealthvault.core.generated.resources.ic_form_email_outline
import com.wealthvault.core.generated.resources.ic_nav_profile
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightMuted
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault.core.theme.LightSurface
import com.wealthvault.core.theme.LightText
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.notification.viewmodel.NotificationScreenModel
import com.wealthvault.notification_api.model.NotificationData
import com.wealthvault.social.ui.main_social.add_friend.AddFriendScreen
import kotlinx.coroutines.delay // 🌟 อย่าลืม import delay
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.jetbrains.compose.resources.painterResource

class NotificationScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<NotificationScreenModel>()
        val navigator = LocalNavigator.currentOrThrow
        val notificationData by screenModel.notificationData.collectAsState()

        // 🌟 เปลี่ยนจาก LaunchedEffect(Unit) เป็นสังเกต Navigator
        // ทุกครั้งที่มีการ Push หรือ Pop หน้าจอ โค้ดในนี้จะเช็กว่าถ้าหน้าปัจจุบันคือ NotificationScreen ให้โหลดข้อมูลใหม่
        LaunchedEffect(navigator.lastItem) {
            if (navigator.lastItem is NotificationScreen) {
                screenModel.fetchNotifications()
            }
        }

        // ส่วนของการ Read All เงียบๆ 2 วินาที (คงไว้เหมือนเดิม)
        LaunchedEffect(Unit) {
            delay(1000)
            screenModel.markAllAsReadBackground()
        }

        NotificationContent(
            onBackClick = { navigator.pop() },
            onReadClick = { data -> screenModel.readNotification(data) },
            onNavigateToAddFriend = {
                navigator.push(AddFriendScreen())
            },
            notificationData = notificationData
        )
    }
}

@Composable
fun NotificationContent(
    onBackClick: () -> Unit,
    onReadClick: (String) -> Unit,
    onNavigateToAddFriend: () -> Unit,
    notificationData: List<NotificationData>,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBg)
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        // --- ส่วนหัว (Header) ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_common_back),
                contentDescription = "Back",
                tint = LightPrimary,
                modifier = Modifier.size(24.dp).clickable { onBackClick() }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "การแจ้งเตือน", style = MaterialTheme.typography.titleLarge, color = LightPrimary)
        }

        // --- ส่วนเนื้อหา ---
        if (notificationData.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_form_email_outline),
                        contentDescription = "Empty",
                        tint = LightMuted.copy(alpha = 0.5f),
                        modifier = Modifier.size(60.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "ยังไม่มีการแจ้งเตือน",
                        style = MaterialTheme.typography.bodyLarge,
                        color = LightMuted
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(notificationData) { notification ->
                    val isRead = notification.isRead == true

                    when (notification.entityType) {
                        "FRIEND_REQUEST" -> {
                            InviteNotificationCard(
                                title = notification.message ?: "",
                                time = notification.createdAt ?: "",
                                metadata = notification.metaData ?: "{}",
                                isRead = isRead,
                                onNavigateToAddFriend = {
                                    onReadClick(notification.id ?: "")
                                    onNavigateToAddFriend()
                                }
                            )
                        }
                        else -> {
                            StandardNotificationCard(
                                title = notification.message ?: "",
                                time = notification.createdAt ?: "",
                                isRead = isRead,
                            )
                        }
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------
// Component ย่อยสำหรับการ์ดแจ้งเตือนแบบต่างๆ
// ---------------------------------------------------------

/**
 * 🌟 Helper: ไอคอนแจ้งเตือน
 */
@Composable
private fun NotificationLeadingIcon(iconRes: org.jetbrains.compose.resources.DrawableResource, isRead: Boolean) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(if (isRead) LightBg else LightSoftWhite),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = if (isRead) LightMuted else LightPrimary,
            modifier = Modifier.size(16.dp)
        )
    }
}

/**
 * 🌟 การ์ดแจ้งเตือนแบบมาตรฐาน
 */
@Composable
fun StandardNotificationCard(
    title: String,
    time: String,
    isRead: Boolean,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = if (isRead) LightSoftWhite.copy(0.6f) else LightSoftWhite),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp,  LightBorder)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {

            // แถวบน: ไอคอน และ ข้อความหลัก
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                NotificationLeadingIcon(iconRes = Res.drawable.ic_form_email_outline, isRead = isRead)

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isRead) LightMuted else LightText,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // แถวล่าง: วันที่ และ Badge (ดันชิดซ้าย)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isRead) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFE57373))) // จุดแดง/ส้ม
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(
                    text = time.take(10), // ดึงแค่วันที่ YYYY-MM-DD
                    style = MaterialTheme.typography.labelSmall,
                    color = LightMuted
                )
            }
        }
    }
}

/**
 * 🌟 การ์ดแจ้งเตือนเพื่อน
 */
@Composable
fun InviteNotificationCard(
    title: String,
    time: String,
    metadata: String,
    isRead: Boolean,
    onNavigateToAddFriend: () -> Unit
) {
    val jsonElement = try { Json.parseToJsonElement(metadata) } catch (e: Exception) { null }
    val isCompleted = jsonElement?.jsonObject?.get("is_completed")?.jsonPrimitive?.booleanOrNull ?: false

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = if (isRead) LightSoftWhite.copy(0.6f) else LightSoftWhite),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, LightBorder)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {

            // แถวบน: ไอคอน และ ข้อความหลัก
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                NotificationLeadingIcon(iconRes = Res.drawable.ic_nav_profile, isRead = isRead)

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isRead) LightMuted else LightText,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // แถวล่าง: จัดเรียงให้อยู่บรรทัดเดียวกัน (วันที่อยู่ซ้าย - ปุ่มอยู่ขวา)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ส่วนซ้าย: วันที่และจุด Unread
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!isRead) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFE57373)))
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = time.take(10),
                        style = MaterialTheme.typography.labelSmall,
                        color = LightMuted
                    )
                }

                // ส่วนขวา: ปุ่มจัดการคำขอ หรือ สถานะ
                if (isCompleted) {
                    Text(
                        text = "✓ ตอบรับคำขอแล้ว",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isRead) LightMuted else LightPrimary,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Button(
                        onClick = onNavigateToAddFriend,
                        colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.height(32.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                    ) {
                        Text("จัดการคำขอ", color = Color.White, fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            painter = painterResource(Res.drawable.ic_common_solid_right),
                            contentDescription = null,
                            tint = LightSoftWhite,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    }
}
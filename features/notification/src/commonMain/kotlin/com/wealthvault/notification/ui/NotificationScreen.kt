package com.wealthvault.notification.ui

//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack

// Import สีจาก Theme ของเรา (เช็ค import ให้ตรงกับโครงสร้างจริงนะครับ)
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_back
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightMuted
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSurface
import com.wealthvault.core.theme.LightText
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.notification.viewmodel.NotificationScreenModel
import com.wealthvault.notification_api.model.NotificationData
import org.jetbrains.compose.resources.painterResource


class NotificationScreen : Screen {
    @Composable
    override fun  Content() {
        val screenModel = getScreenModel<NotificationScreenModel>()
        val navigator = LocalNavigator.currentOrThrow
        val notificationData by screenModel.notificationData.collectAsState()
        NotificationContent(onBackClick = {
            navigator.pop()
        },
            onReadClick = { data ->
                screenModel.readNotification(data)
            },
            onAcceptClick = { id, action ->
                screenModel.acceptFriend(id,action)
            },
            notificationData = notificationData
           )
    }
}

@Composable
fun NotificationContent(
    onBackClick: () -> Unit,
    onAcceptClick: (String,String) -> Unit,
    onReadClick: (String) -> Unit,
    notificationData: List<NotificationData>,
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBg) // สีพื้นหลังแอป
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        // --- ส่วนหัว (Header) ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_common_back),
                contentDescription = "Back",
                tint = LightPrimary,
                modifier = Modifier.size(24.dp).clickable { onBackClick() }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "การแจ้งเตือน",
                fontSize = 24.sp,
                color = LightPrimary,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))


                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // 🌟 วนลูปตามจำนวนข้อมูลที่มีใน Array ของ API
                    items(notificationData) { notification ->

                        when (notification.entityType) { // เช็กประเภทของการแจ้งเตือน

                            "FRIEND_REQUEST" -> {
                                // ถ้าเป็นแอดเพื่อน ให้แสดงการ์ด Invite
                                InviteNotificationCard(
                                    title = notification.message ?: "", // ใช้ข้อความจาก API เช่น "👋 test ได้ส่งคำขอเป็นเพื่อนกับคุณ"
                                    inviter = notification.senderId ?: "", // (ถ้าข้อความมีชื่อคนส่งมาแล้ว อาจจะเว้นว่างไว้ หรือใส่ชื่อ SenderID)
                                    time = notification.createdAt ?: "",
                                    onDeclineClick = {
                                        // TODO: เรียกฟังก์ชันปฏิเสธ
                                        onAcceptClick(notification.senderId ?: "","decline")
                                        println("ปฏิเสธคำขอจาก: ${notification.senderId}")
                                    },
                                    onAcceptClick = {
                                        // 🌟 เรียกใช้ฟังก์ชัน acceptFriend ที่เราทำไว้ใน ScreenModel
                                        // screenModel.acceptFriend(AcceptFriendRequest(...))
                                        onAcceptClick(notification.senderId ?: "","accept")
                                        println("ยอมรับคำขอจาก: ${notification.senderId}")
                                    },
                                    onReadClick = {
                                        onReadClick(notification.id ?: "")
                                    }
                                )
                            }

                            // ถ้าเป็นประเภทอื่นๆ (เช่น ประกัน, ผ่อนชำระ, แชร์)
                            else -> {
                                StandardNotificationCard(
                                    title = notification.message ?: "",
                                    time = notification.createdAt ?: "",
                                    // ดึงวันที่จาก createdAt มาแสดง (สามารถเขียนฟังก์ชันแปลงรูปแบบวันที่ให้สวยขึ้นได้)
                                    subtitleLeft = "วันที่: ${notification.createdAt?.take(10)}",
                                    onReadClick = { onReadClick(notification.id ?: "") }
                                )
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
 * การ์ดแจ้งเตือนแบบมาตรฐาน (รองรับข้อมูล 1-2 บรรทัด และจัดซ้าย-ขวาได้)
 */
@Composable
fun StandardNotificationCard(
    title: String,
    subtitleLeft: String,
    time: String,
    subtitleRight: String? = null,
    secondLineLeft: String? = null,
    secondLineRight: String? = null,
    onReadClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onReadClick() },
        colors = CardDefaults.cardColors(containerColor = LightSurface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, LightBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // หัวข้อหลัก
            Text(text = title, fontSize = 16.sp, color = LightText)
            Spacer(modifier = Modifier.height(8.dp))

            // บรรทัดย่อยที่ 1
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = subtitleLeft, fontSize = 14.sp, color = LightMuted)
                if (subtitleRight != null) {
                    Text(text = subtitleRight, fontSize = 14.sp, color = LightMuted)
                }
            }

            // บรรทัดย่อยที่ 2 (ถ้ามี)
            if (secondLineLeft != null || secondLineRight != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = secondLineLeft ?: "", fontSize = 14.sp, color = LightMuted)
                    Text(text = secondLineRight ?: "", fontSize = 14.sp, color = LightMuted)
                }
            }
        }
    }
}

/**
 * การ์ดแจ้งเตือนแบบมีปุ่มคำเชิญ (ปฏิเสธ / เข้าร่วม)
 */
@Composable
fun InviteNotificationCard(
    title: String,
    inviter: String,
    time: String,
    onDeclineClick: () -> Unit,
    onAcceptClick: () -> Unit,
    onReadClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onReadClick() },
        colors = CardDefaults.cardColors(containerColor = LightSurface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, LightBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // หัวข้อหลัก
            Text(text = title, fontSize = 16.sp, color = LightText)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = time.take(10), fontSize = 16.sp, color = LightText)

            // ผู้เชิญ
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "ผู้เชิญ", fontSize = 14.sp, color = LightMuted)
                Text(text = inviter, fontSize = 14.sp, color = LightMuted)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ปุ่มกด (ชิดขวา)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                // ปุ่มปฏิเสธ (ขอบส้ม)
                OutlinedButton(
                    onClick = onDeclineClick,
                    border = BorderStroke(1.dp, LightPrimary),
                    shape = RoundedCornerShape(percent = 50),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text("ปฏิเสธ", color = LightPrimary, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                // ปุ่มเข้าร่วม (พื้นส้ม)
                Button(
                    onClick = onAcceptClick,
                    colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
                    shape = RoundedCornerShape(percent = 50),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text("เข้าร่วม", color = Color.White, fontSize = 14.sp)
                }
            }
        }
    }
}

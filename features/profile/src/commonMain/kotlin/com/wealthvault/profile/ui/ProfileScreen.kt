package com.wealthvault.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.screenModule
import cafe.adriel.voyager.core.screen.Screen
import coil3.compose.AsyncImage
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_nav_profile
import com.wealthvault.core.generated.resources.ic_profile_setting
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.WvWaveGradientEnd
import com.wealthvault.core.utils.formatThaiDate
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.login.ui.LoginScreen
import com.wealthvault.navigation.SharedScreen
import com.wealthvault.profile.ui.components.ClosePersonItem
import com.wealthvault.`user-api`.model.CloseFriendData
import com.wealthvault.`user-api`.model.UserData
import org.jetbrains.compose.resources.painterResource

class ProfileScreen(private val onSettingsClick: () -> Unit) : Screen {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<ProfileScreenModel>()

        LaunchedEffect(Unit) {
            screenModel.fetchUser()
             screenModel.fetchCloseFriends()
        }

        val userData by screenModel.userState.collectAsState()
        // 🌟 ดึงข้อมูลจาก State ใน ScreenModel มาใช้งาน
        val closeFriends by screenModel.closeFriends.collectAsState()

        ProfileContent(
            userData = userData,
            closeFriends = closeFriends,
            onSettingsClick = onSettingsClick
        )
    }
}



@Composable
fun ProfileContent(
    userData: UserData?,
    closeFriends: List<CloseFriendData>,
    onSettingsClick: () -> Unit
) {
    val themeColor = Color(0xFFC27A5A)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp)
    ) {
        item {
            // --- Header ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "โปรไฟล์",
                    style = MaterialTheme.typography.titleLarge,
                    color = themeColor
                )

                Icon(
                    painter = painterResource(Res.drawable.ic_profile_setting),
                    contentDescription = "Settings",
                    tint = LightPrimary,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { onSettingsClick() }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- Profile Info ---
            if (userData == null) {
                Box(modifier = Modifier.fillMaxWidth().height(110.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = themeColor)
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {

                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .border(width = 2.dp, color = themeColor, shape = CircleShape)
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(LightBg),
                        contentAlignment = Alignment.Center
                    ) {
                        if (userData.profile.toString().isNotEmpty()) {
                            AsyncImage(
                                model = userData.profile,
                                contentDescription = "Profile Picture",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                painter = painterResource(Res.drawable.ic_nav_profile),
                                contentDescription = "Default Profile",
                                tint = WvWaveGradientEnd,
                                modifier = Modifier.size(50.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    Column {
                        Text(
                            text = userData.username ?: "",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3A2F2A)
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row {
                            Text(
                                text = "${userData.firstName} ${userData.lastName}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = userData.email ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = formatThaiDate(userData.birthday),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider(color = themeColor.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(24.dp))

            // --- Settings Summary ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "เปิดให้เห็นสินทรัพย์ทั้งหมดให้คนใกล้ชิดเมื่ออายุ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF3A2F2A),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = if (userData?.shareEnabled == true) "${userData.sharedAge} ปี" else "ไม่ได้เปิดใช้งาน",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- Close People List Header ---
            // 🌟 สร้าง Row คลุมเพื่อให้ข้อความ 2 อันอยู่บรรทัดเดียวกัน
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween, // 🌟 ดันให้อยู่ซ้ายสุด-ขวาสุด
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "คนใกล้ชิด",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF3A2F2A)
                )

                // 🌟 ถ้ารายชื่อเพื่อนว่างเปล่า ให้โชว์ข้อความนี้ที่ฝั่งขวา
                if (closeFriends.isEmpty()) {
                    Text(
                        text = "ยังไม่มีคนใกล้ชิด",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 🌟 ถ้ารายชื่อเพื่อนไม่ว่างเปล่า ถึงจะวาดรายการ
        if (closeFriends.isNotEmpty()) {
            items(closeFriends) { friend ->
                ClosePersonItem(
                    friend = friend,
                    isEnabled = userData?.shareEnabled ?: false
                )
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}
val profileScreenModule = screenModule {
    register< SharedScreen.Profile> {
        ProfileScreen()
    }
}

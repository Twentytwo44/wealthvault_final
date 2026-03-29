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
import cafe.adriel.voyager.core.screen.Screen
import coil3.compose.AsyncImage
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_profile_setting
import com.wealthvault.core.generated.resources.ic_nav_profile // 🌟 1. นำเข้าไอคอนโปรไฟล์เริ่มต้น
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightSecondary
import com.wealthvault.core.theme.WvWaveGradientEnd
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.profile.ui.components.ClosePersonItem
import com.wealthvault.`user-api`.model.UserData
import org.jetbrains.compose.resources.painterResource

class ProfileScreen(private val onSettingsClick: () -> Unit) : Screen {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<ProfileScreenModel>()

        LaunchedEffect(Unit) {
            screenModel.fetchUser()
        }

        val userData by screenModel.userState.collectAsState()

        ProfileContent(
            userData = userData,
            onSettingsClick = onSettingsClick
        )
    }
}

private fun formatBirthday(rawDate: String): String {
    if (rawDate.isBlank()) return ""
    val cleanDate = rawDate.take(10)
    val parts = cleanDate.split("-")
    if (parts.size == 3) {
        return "${parts[2]}/${parts[1]}/${parts[0]}"
    }
    return cleanDate
}

@Composable
fun ProfileContent(
    userData: UserData?,
    onSettingsClick: () -> Unit
) {
    val themeColor = Color(0xFFC27A5A)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp)
    ) {

        // --- Header ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "โปรไฟล์",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                color = themeColor
            )

            Icon(
                painter = painterResource(Res.drawable.ic_profile_setting),
                contentDescription = "Settings",
                tint = Color(0xFFC47B5D),
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

                // 🌟 2. เพิ่มการจัดการรูปโปรไฟล์ตอนที่ไม่มีรูป
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .border(width = 2.dp, color = themeColor, shape = CircleShape)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(LightBg),
                    contentAlignment = Alignment.Center
                ) {
                    if (userData.profile.isNotEmpty()) {
                        AsyncImage(
                            model = userData.profile,
                            contentDescription = "Profile Picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // 🌟 โชว์ไอคอนนี้ถ้า User ยังไม่มีรูปโปรไฟล์
                        Icon(
                            painter = painterResource(Res.drawable.ic_nav_profile),
                            contentDescription = "Default Profile",
                            tint = WvWaveGradientEnd, // ให้สีกลืนไปกับขอบ
                            modifier = Modifier.size(50.dp) // ปรับขนาดไอคอนให้อยู่ตรงกลางสวยๆ
                        )
                    }
                }

                Spacer(modifier = Modifier.width(20.dp))

                Column {
                    Text(
                        text = userData.username,
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
                        text = userData.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${formatBirthday(userData.birthday)}",
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

        // --- Close People List ---
        Text(
            text = "คนใกล้ชิด",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF3A2F2A)
        )
        Spacer(modifier = Modifier.height(16.dp))

        ClosePersonItem(name = "Nai")
    }
}
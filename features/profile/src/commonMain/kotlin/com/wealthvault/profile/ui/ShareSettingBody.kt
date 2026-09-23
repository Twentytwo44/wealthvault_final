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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_back
import com.wealthvault.core.generated.resources.ic_common_plus
import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault.domain.profile.CloseFriendData
import com.wealthvault.domain.profile.UserData
import com.wealthvault.profile.ui.components.ClosePersonItem
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun ShareSettingBody(
    userData: UserData?,
    closeFriends: List<CloseFriendData>,
    isLoading: Boolean,
    error: AppError?,
    themeColor: Color,
    isSharingEnabled: Boolean,
    sharedAgeText: String,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    onSharingEnabledChange: (Boolean) -> Unit,
    onSharedAgeTextChange: (String) -> Unit,
    onPlusClick: () -> Unit,
    onDeleteClick: (CloseFriendData) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBg)
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 24.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 32.dp),
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_common_back),
                contentDescription = "Back",
                tint = themeColor,
                modifier = Modifier.size(24.dp).clickable(onClick = onBackClick),
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "ตั้งค่าการแชร์ทรัพย์สิน",
                style = MaterialTheme.typography.titleLarge,
                color = themeColor,
            )
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = themeColor)
            }
            return
        }

        if (userData == null) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = if (error != null) "ไม่สามารถโหลดข้อมูลได้" else "ไม่พบข้อมูลโปรไฟล์",
                    color = Color.Gray,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onRetryClick,
                    colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                ) {
                    Text("ลองใหม่", color = Color.White)
                }
            }
            return
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "แชร์ทรัพย์สินทั้งหมดให้คนใกล้ชิดตามกำหนด",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF3A2F2A),
                modifier = Modifier.weight(1f),
            )
            Switch(
                checked = isSharingEnabled,
                onCheckedChange = onSharingEnabledChange,
                thumbContent = { },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = LightSoftWhite,
                    checkedTrackColor = LightPrimary,
                    uncheckedThumbColor = LightSoftWhite,
                    uncheckedTrackColor = Color(0xFFE8DDD7),
                    uncheckedBorderColor = Color.Transparent,
                ),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "เปิดให้เห็นทรัพย์สินเมื่อถึงอายุ",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF3A2F2A),
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                BasicTextField(
                    value = sharedAgeText,
                    onValueChange = onSharedAgeTextChange,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    textStyle = androidx.compose.material3.LocalTextStyle.current.copy(
                        textAlign = TextAlign.Center,
                        color = Color(0xFF3A2F2A),
                    ),
                    cursorBrush = SolidColor(themeColor),
                    modifier = Modifier.width(70.dp).height(44.dp),
                    decorationBox = { innerTextField ->
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(LightSoftWhite, RoundedCornerShape(12.dp))
                                .border(1.dp, LightBorder, RoundedCornerShape(12.dp)),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            innerTextField()
                        }
                    },
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "ปี", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "คนใกล้ชิด",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF3A2F2A),
            )
            Icon(
                painter = painterResource(Res.drawable.ic_common_plus),
                contentDescription = "Add",
                tint = themeColor,
                modifier = Modifier.size(24.dp).clickable(onClick = onPlusClick),
            )
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            if (closeFriends.isEmpty()) {
                item {
                    Text(
                        text = "ยังไม่มีคนใกล้ชิด",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    )
                }
            } else {
                items(items = closeFriends, key = { friend -> friend.id }) { friend ->
                    ClosePersonItem(
                        friend = friend,
                        showDelete = true,
                        isEnabled = true,
                        onDeleteClick = { onDeleteClick(friend) },
                    )
                }
            }
        }
    }
}

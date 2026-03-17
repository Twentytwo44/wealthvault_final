package com.wealthvault.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_back
import com.wealthvault.core.generated.resources.ic_common_plus // 🌟 ดึงไอคอนบวก
import org.jetbrains.compose.resources.painterResource

@Composable
fun ShareSettingScreen(
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    val themeColor = Color(0xFFC27A5A)
    var isSharingEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        // --- Header ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_common_back),
                contentDescription = "Back",
                tint = themeColor,
                modifier = Modifier.size(24.dp).clickable { onBackClick() }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "ตั้งค่าการแชร์ทรัพย์สิน", style = MaterialTheme.typography.titleLarge, color = themeColor)
        }

        // --- Toggle Switch ---
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "แชร์ทรัพย์สินทั้งหมดให้คนใกล้ชิดตามกำหนด",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF3A2F2A),
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = isSharingEnabled,
                onCheckedChange = { isSharingEnabled = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = themeColor
                )
            )
        }

        // --- Age Setting ---
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "เปิดให้เห็นทรัพย์สินเมื่อถึงอายุ", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF3A2F2A))
            Text(text = "80 ปี", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }

        // --- Close People List ---
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "คนใกล้ชิด", style = MaterialTheme.typography.titleMedium, color = Color(0xFF3A2F2A))
            Icon(
                painter = painterResource(Res.drawable.ic_common_plus),
                contentDescription = "Add",
                tint = themeColor,
                modifier = Modifier.size(24.dp).clickable { /* TODO: เพิ่มคน */ }
            )
        }

        // รายการคนใกล้ชิด
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFE0DCDA)))
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "Nai", style = MaterialTheme.typography.bodyLarge, color = Color(0xFF3A2F2A))
            }
            Text(
                text = "ลบ",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFE74C3C),
                modifier = Modifier.clickable { /* TODO: ลบคน */ }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // --- Save Button ---
        Button(
            onClick = onSaveClick,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = themeColor)
        ) {
            Text("บันทึก", color = Color.White, style = MaterialTheme.typography.titleMedium)
        }
    }
}
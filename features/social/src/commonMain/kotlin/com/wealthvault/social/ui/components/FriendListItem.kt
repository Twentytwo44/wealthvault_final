package com.wealthvault.social.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FriendListItem(name: String, themeColor: Color = Color(0xFFC27A5A)) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // รูปโปรไฟล์ (ทำเป็นกล่องจำลองไว้ก่อน)
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFEFEBE9)), // สีพื้นหลังเทาอ่อนๆ
            contentAlignment = Alignment.Center
        ) {
            // TODO: โหลดรูปจริงด้วย Coil หรือ Kamel ในอนาคต
//            Icon(Icons.Default.Person, contentDescription = null, tint = themeColor.copy(alpha = 0.5f))
        }

        Spacer(modifier = Modifier.width(16.dp))

        // ชื่อเพื่อน
        Text(text = name, fontSize = 16.sp, color = themeColor)
    }
}
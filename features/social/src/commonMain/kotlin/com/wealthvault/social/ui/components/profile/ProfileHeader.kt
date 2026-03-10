package com.wealthvault.social.ui.components.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileHeader(
    name: String,
    subtitle: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // รูปโปรไฟล์ (ใช้ Box สีเทาแทนรูปภาพชั่วคราว)
        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .background(Color(0xFFEFEBE9)),
            contentAlignment = Alignment.Center
        ) {
//            Icon(
//                imageVector = Icons.Default.Person,
//                contentDescription = "Profile Picture",
//                tint = Color.Gray,
//                modifier = Modifier.size(48.dp)
//            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ชื่อ
        Text(
            text = name,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF3A2F2A)
        )

        Spacer(modifier = Modifier.height(4.dp))

        // ซับไตเติ้ล (อีเมล หรือ จำนวนสมาชิก)
        Text(
            text = subtitle,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}
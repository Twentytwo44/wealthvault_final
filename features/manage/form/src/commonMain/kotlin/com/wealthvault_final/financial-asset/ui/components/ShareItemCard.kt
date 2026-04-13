package com.wealthvault_final.`financial-asset`.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wealthvault_final.`financial-asset`.ui.bankaccount.summary.WealthVaultBrown

@Composable
fun ShareItemCard(
    name: String,
    date:String,
    groupCount: String? = null,
    isEmail: Boolean = false
) {
    // แต่ละ Item มีพื้นหลังสีขาวและขอบมน
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ส่วนของ Icon Box
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = if (isEmail) Color(0xFFF2E8E1) else Color(0xFFD9D9D9),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isEmail) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = WealthVaultBrown,
                        modifier = Modifier.size(28.dp)
                    )
                }
                // ถ้าไม่ใช่ Email ในรูปจะเป็นพื้นที่สีเทาว่างๆ
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = name,
                    fontSize = 18.sp,
                    color = Color(0xFF4A4A4A)
                )
                Text(
                    text = date,
                    fontSize = 18.sp,
                    color = Color(0xFF4A4A4A)
                )


                // Badge สำหรับจำนวนคนในกลุ่ม
                if (groupCount != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = Color(0xFFF2E8E1),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Groups,
                                contentDescription = null,
                                // ✅ ต้องใช้ Modifier.size และหน่วยต้องเป็น .dp (ไม่ใช่ .sp)
                                modifier = Modifier.size(16.dp),
                                tint = WealthVaultBrown
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = groupCount,
                                fontSize = 12.sp,
                                color = WealthVaultBrown
                            )
                        }
                    }
                }
            }
        }
    }
}


package com.wealthvault.dashboard.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightSoftWhite

@Composable
fun RealItemCard(
    title: String,
    subtitleLabel: String,
    subtitleValue: String,
    amountLabel: String,
    amountValue: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = LightSoftWhite
        ),
        border = BorderStroke(1.dp, LightBorder),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) { // 🌟 ปรับ padding แนวตั้งให้ดูโปร่งขึ้นนิดนึง
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium, // 🌟 ปรับหัวข้อให้หนาขึ้น
                color = Color(0xFF3A2F2A)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = subtitleLabel, fontSize = 14.sp, color = Color.Gray)
                Text(text = subtitleValue, fontSize = 14.sp, color = Color(0xFF3A2F2A))
            }
            Spacer(modifier = Modifier.height(2.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = amountLabel, fontSize = 14.sp, color = Color.Gray)
                Text(text = amountValue, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF3A2F2A)) // 🌟 เน้นตัวเลขมูลค่าให้ชัด
            }
        }
    }
}
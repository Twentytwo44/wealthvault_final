package com.wealthvault.social.ui.components.space

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ActivityBubbleCard(
    title: String,
    assetName: String,
    assetType: String,
    showAvatar: Boolean = false,
    themeColor: Color = Color(0xFFC27A5A)
) {
    Row(modifier = Modifier.fillMaxWidth()
        .padding(horizontal = 24.dp)) {
        if (showAvatar) {
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFFEFEBE9)),
                contentAlignment = Alignment.Center
            ) {
//                Icon(Icons.Default.Person, contentDescription = null, tint = themeColor, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
        } else {
            Spacer(modifier = Modifier.width(48.dp))
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, themeColor.copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = title, fontSize = 14.sp, color = Color(0xFF3A2F2A), fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "ชื่อทรัพย์สิน", fontSize = 12.sp, color = Color.Gray)
                    Text(text = assetName, fontSize = 12.sp, color = Color(0xFF3A2F2A))
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "ประเภท", fontSize = 12.sp, color = Color.Gray)
                    Text(text = assetType, fontSize = 12.sp, color = Color(0xFF3A2F2A))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "รายละเอียดเพิ่มเติม >",
                    fontSize = 12.sp,
                    color = themeColor,
                    modifier = Modifier.align(Alignment.End).clickable { /* TODO */ }
                )
            }
        }
    }
}
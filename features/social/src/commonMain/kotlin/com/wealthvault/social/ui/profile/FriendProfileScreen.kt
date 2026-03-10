package com.wealthvault.social.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// Import component ที่เราเขียนไว้
import com.wealthvault.social.ui.components.space.SpaceTopBar
import com.wealthvault.social.ui.components.profile.ProfileHeader

@Composable
fun FriendProfileScreen(
    onBackClick: () -> Unit,
    onRemoveFriendClick: () -> Unit
) {
    val themeColor = Color(0xFFC27A5A)
    val bgColor = Color(0xFFFFF8F3)

    Scaffold(
        containerColor = bgColor,
        bottomBar = {
            // ปุ่มลบเพื่อนอยู่ด้านล่างสุด
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                TextButton(onClick = onRemoveFriendClick) {
                    Text(text = "ลบเพื่อน", color = Color(0xFFE74C3C), fontSize = 14.sp)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)

        ) {
            SpaceTopBar(title = "โปรไฟล์เพื่อน", onBackClick = onBackClick)
            HorizontalDivider(color = themeColor.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(24.dp))

            ProfileHeader(name = "Twentytwo01", subtitle = "nptwosudimw@gmail.com")

            Spacer(modifier = Modifier.height(32.dp))

            LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
                // --- หมวดหมู่: สินทรัพย์ ---
                item {
                    SectionHeader(title = "สินทรัพย์", themeColor = themeColor)
                    Spacer(modifier = Modifier.height(16.dp))

                    ProfileListItem(name = "Bitcoin", tag = "Crypto", tagColor = Color(0xFFF5A623), amount = "$43932.54", subName = "Bitcoin / US Dollar")
                    ProfileListItem(name = "Apple inc.", tag = "Stock", tagColor = Color(0xFF4A90E2), amount = "$30032.42", subName = "NASDAQ")
                    ProfileListItem(name = "Gold", tag = "Gold", tagColor = Color(0xFFF5A623), amount = "$20323.42", subName = "")
                    ProfileListItem(name = "Cash", tag = "Cash", tagColor = Color(0xFF7ED321), amount = "$32093.00", subName = "")

                    Spacer(modifier = Modifier.height(24.dp))
                }

                // --- หมวดหมู่: หนี้สิน ---
                item {
                    SectionHeader(title = "หนี้สิน", themeColor = themeColor)
                    Spacer(modifier = Modifier.height(16.dp))

                    ProfileListItem(name = "Debt", tag = "Debt", tagColor = Color(0xFFE74C3C), amount = "$2343.00", subName = "")

                    Spacer(modifier = Modifier.height(80.dp)) // ดันเผื่อปุ่มด้านล่าง
                }
            }
        }
    }
}

// ชิ้นส่วนหัวข้อหมวดหมู่ (มีเส้นคั่นและลูกศร)
@Composable
private fun SectionHeader(title: String, themeColor: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, fontSize = 14.sp, color = themeColor)
//            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Collapse", tint = themeColor, modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = themeColor.copy(alpha = 0.2f))
    }
}

// ชิ้นส่วนรายการทรัพย์สิน/หนี้สิน ในหน้าโปรไฟล์ (ไม่มี Checkbox)
@Composable
private fun ProfileListItem(name: String, tag: String, tagColor: Color, amount: String, subName: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(Color.White))
        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = name, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF3A2F2A))
                Spacer(modifier = Modifier.width(8.dp))
                Surface(color = tagColor, shape = RoundedCornerShape(8.dp)) {
                    Text(text = tag, color = Color.White, fontSize = 8.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }
            if (subName.isNotEmpty()) {
                Text(text = subName, fontSize = 10.sp, color = Color.Gray)
            }
        }

        Text(text = amount, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF3A2F2A))
    }
}
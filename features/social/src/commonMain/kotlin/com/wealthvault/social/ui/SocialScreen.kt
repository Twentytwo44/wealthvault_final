package com.wealthvault.social.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.wealthvault.social.ui.components.SocialHeader
import com.wealthvault.social.ui.friend.FriendScreen
import com.wealthvault.social.ui.group.GroupScreen

@Composable
fun SocialScreen() {
    // กำหนด State เพื่อจำว่าตอนนี้อยู่หน้า "เพื่อน" หรือ "กลุ่ม" (ค่าเริ่มต้นคือ เพื่อน)
    var currentTab by remember { mutableStateOf("เพื่อน") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8F3)) // สีพื้นหลัง (LightBg)
            .padding(horizontal = 20.dp)
            .padding(top = 20.dp)
    ) {

        // 1. แถบ Header (เมนู Dropdown และปุ่มเพิ่ม)
        SocialHeader(
            currentTabName = currentTab,
            onTabSelected = { selectedTab ->
                currentTab = selectedTab // เมื่อเลือก Dropdown ให้เปลี่ยน State
            },
            onAddFriendClick = { /* TODO: นำทางไปหน้าเพิ่มเพื่อน */ },
            onAddGroupClick = { /* TODO: นำทางไปหน้าสร้างกลุ่ม */ }
        )

        // 2. ส่วนเนื้อหา (สลับหน้าจอไปมาอย่างนุ่มนวลด้วย Crossfade)
        Crossfade(targetState = currentTab, label = "Tab Switching") { tab ->
            when (tab) {
                "เพื่อน" -> FriendScreen()
                "กลุ่ม" -> GroupScreen()
            }
        }
    }
}
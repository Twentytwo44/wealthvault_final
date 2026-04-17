package com.wealthvault.social.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen // 🌟 อย่าลืม Import Screen ของ Voyager
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.social.ui.components.SocialHeader
import com.wealthvault.social.ui.main_social.add_friend.AddFriendScreen
import com.wealthvault.social.ui.main_social.form_group.CreateGroupScreen
import com.wealthvault.social.ui.main_social.friend.FriendScreen
import com.wealthvault.social.ui.main_social.group.GroupScreen

// 🌟 1. คลาส Screen สำหรับผูกกับ Voyager และจัดการ State / ScreenModel
class SocialScreen : Screen {
    @Composable
    override fun Content() {


        var currentTab by rememberSaveable { mutableStateOf("กลุ่ม") }

        val navigator = LocalNavigator.currentOrThrow

        // 🌟 2. ท่าไม้ตาย! ไต่หา Navigator ตัวนอกสุด (Root) เพื่อซ่อน Navbar
        var rootNavigator = navigator
        while (rootNavigator.parent != null) {
            rootNavigator = rootNavigator.parent!!
        }
        SocialContent(
            currentTab = currentTab,
            onTabSelected = { selectedTab -> currentTab = selectedTab },
            onAddFriendClick = { rootNavigator.push(AddFriendScreen()) },
            onAddGroupClick = { rootNavigator.push(CreateGroupScreen()) }
        )
    }
}

// 🌟 2. ฟังก์ชัน Content สำหรับวาด UI ล้วนๆ (Stateless)
@Composable
fun SocialContent(
    currentTab: String,
    onTabSelected: (String) -> Unit,
    onAddFriendClick: () -> Unit,
    onAddGroupClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(Color(0xFFFFF8F3)) // สีพื้นหลัง (LightBg)
            .padding(horizontal = 20.dp)
            .padding(top = 20.dp)
    ) {

        // 1. แถบ Header (เมนู Dropdown และปุ่มเพิ่ม)
        SocialHeader(
            currentTabName = currentTab,
            onTabSelected = onTabSelected,
            onAddFriendClick = onAddFriendClick,
            onAddGroupClick = onAddGroupClick
        )

        // 2. ส่วนเนื้อหา (สลับหน้าจอไปมาอย่างนุ่มนวลด้วย Crossfade)
        Crossfade(
            targetState = currentTab,
            label = "Tab Switching",
            modifier = Modifier.weight(1f) // <--- ตรงนี้สำคัญมาก!
        ) { tab ->
            when (tab) {
                "เพื่อน" -> FriendScreen().Content()
                "กลุ่ม" -> GroupScreen().Content()
            }
        }
    }
}
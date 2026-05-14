package com.wealthvault.social.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.social.ui.components.SocialHeader
import com.wealthvault.social.ui.main_social.add_friend.AddFriendScreen
import com.wealthvault.social.ui.main_social.form_group.CreateGroupScreen
import com.wealthvault.social.ui.main_social.friend.FriendScreen
import com.wealthvault.social.ui.main_social.group.GroupScreen

class SocialScreen : Screen {
    @Composable
    override fun Content() {
        // 🌟 1. ดึง ScreenModel มาใช้งาน
        val screenModel = getScreenModel<SocialScreenModel>()

        // 🌟 2. ดึงสถานะจุดแดง (True/False)
        val hasPendingRequest by screenModel.hasPendingRequest.collectAsStateWithLifecycle()

        var currentTab by rememberSaveable { mutableStateOf("เพื่อน") }
        val navigator = LocalNavigator.currentOrThrow

        // 🌟 3. ดึง Lifecycle มาใช้งาน
        val lifecycleOwner = LocalLifecycleOwner.current

        // 🌟 4. ใช้ DisposableEffect ดัก ON_RESUME แทน เพื่อให้จุดแดงอัปเดตเสมอ
        // ไม่ว่าจะกด Back กลับมา หรือสลับแอปกลับมา
        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    println("🔄 SocialScreen ตื่นแล้ว! อัปเดตสถานะจุดแดงคำขอเป็นเพื่อน...")
                    screenModel.fetchPendingFriendsBadge()
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }

        var rootNavigator = navigator
        while (rootNavigator.parent != null) {
            rootNavigator = rootNavigator.parent!!
        }

        SocialContent(
            currentTab = currentTab,
            onTabSelected = { selectedTab -> currentTab = selectedTab },
            onAddFriendClick = { rootNavigator.push(AddFriendScreen()) },
            onAddGroupClick = { rootNavigator.push(CreateGroupScreen()) },
            hasPendingRequest = hasPendingRequest // 🌟 5. ส่งค่าสถานะจุดแดงลงไป
        )
    }
}

@Composable
fun SocialContent(
    currentTab: String,
    onTabSelected: (String) -> Unit,
    onAddFriendClick: () -> Unit,
    onAddGroupClick: () -> Unit,
    hasPendingRequest: Boolean // 🌟 รับค่าที่ตรงนี้
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
            onAddGroupClick = onAddGroupClick,
            hasPendingRequest = hasPendingRequest // 🌟 ส่งต่อไปให้ Component วาดจุดแดง
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

package com.wealthvault.social.ui.space

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
// --- Import ชิ้นส่วนจากโฟลเดอร์ components/space ---
import com.wealthvault.social.ui.components.space.ActivityBubbleCard
import com.wealthvault.social.ui.components.space.SpaceFloatingMenu
import com.wealthvault.social.ui.components.space.SpaceTopBar

@Composable
fun FriendSpaceScreen(
    friendName: String = "Twentytwo",
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    onManageClick: () -> Unit
) {
    val themeColor = Color(0xFFC27A5A)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 20.dp)
    ) {
        SpaceTopBar(title = friendName, onBackClick = onBackClick, showMoreOption = true)

        HorizontalDivider(color = themeColor.copy(alpha = 0.3f), thickness = 1.dp)
        Spacer(modifier = Modifier.height(16.dp))

        // รายการความเคลื่อนไหว (Activity Feed)
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                // เรียกใช้การ์ดจากไฟล์ที่แยกไว้
                ActivityBubbleCard(
                    title = "$friendName ได้แชร์ทรัพย์สินนี้กับคุณ",
                    assetName = "บัญชีเงินเก็บเพื่อเกษียณ...",
                    assetType = "บัญชีเงินฝาก",
                    showAvatar = true,
                    themeColor = themeColor
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                ActivityBubbleCard(
                    title = "คุณได้แชร์ทรัพย์สินนี้กับ $friendName",
                    assetName = "เก็บเงินซื้อเกม",
                    assetType = "บัญชีเงินฝาก",
                    showAvatar = false,
                    themeColor = themeColor
                )
                Spacer(modifier = Modifier.height(100.dp)) // ดันหนีปุ่ม FAB
            }
        }
    }

}
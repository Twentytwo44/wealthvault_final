package com.wealthvault.social.ui.manage_shared

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

// --- Import ชิ้นส่วนจากโฟลเดอร์ components/space ---
import com.wealthvault.social.ui.components.space.SharedAssetItem
import com.wealthvault.social.ui.components.space.SpaceTopBar

// 🌟 1. สร้าง Class แบบ Screen เพื่อรับค่าที่ส่งมา
class SharedAssetManageScreen(
    private val targetId: String,
    private val targetName: String,
    private val isGroup: Boolean // 🌟 เอาไว้เช็คตอนเรียก API ว่าจะดึงของกลุ่มหรือเพื่อน
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        SharedAssetManageContent(
            targetName = targetName,
            onBackClick = { navigator.pop() }
        )
    }
}

// 🌟 2. ตัว UI เดิมของคุณ Champ (แยกออกมาเพื่อให้โค้ดสะอาด)
@Composable
fun SharedAssetManageContent(
    targetName: String,
    onBackClick: () -> Unit
) {
    val themeColor = Color(0xFFC27A5A)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding() // 🌟 เผื่อไว้กันขอบจอด้านบน
            .padding(top = 20.dp)
    ) {
        SpaceTopBar(title = "การจัดการทรัพย์สิน", onBackClick = onBackClick)
        HorizontalDivider(color = themeColor.copy(alpha = 0.3f), thickness = 1.dp)
        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            // ส่วนที่ 1: เราแชร์ให้เขา
            item {
                Text(
                    text = "ทรัพย์สินที่คุณแชร์",
                    fontSize = 16.sp,
                    color = themeColor,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                SharedAssetItem(
                    name = "เก็บเงินซื้อเกม",
                    tag = "บัญชีเงินฝาก",
                    showDelete = true,
                    themeColor = themeColor
                )
                SharedAssetItem(
                    name = "ซื้อหุ้นเพื่อรวย",
                    tag = "บัญชีเงินฝาก",
                    showDelete = true,
                    themeColor = themeColor
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            // ส่วนที่ 2: เขาแชร์ให้เรา
            item {
                Text(
                    text = "ทรัพย์สินที่ $targetName แชร์กับคุณ",
                    fontSize = 16.sp,
                    color = themeColor,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                SharedAssetItem(
                    name = "บัญชีเงินฝากเพื่อเกษียณ...",
                    tag = "บัญชีเงินฝาก",
                    showDelete = false,
                    themeColor = themeColor
                )
                SharedAssetItem(
                    name = "บัญชีเงินเก็บเพื่อหลาน",
                    tag = "บัญชีเงินฝาก",
                    showDelete = false,
                    themeColor = themeColor
                )
            }
        }
    }
}
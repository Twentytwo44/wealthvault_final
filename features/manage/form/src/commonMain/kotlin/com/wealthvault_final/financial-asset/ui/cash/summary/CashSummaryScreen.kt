package com.wealthvault_final.`financial-asset`.ui.cash.summary

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage

// 🌟 Import ธีมของแอป และ Utils
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightText
import com.wealthvault.core.utils.formatAmount
import com.wealthvault.core.utils.getScreenModel

import com.wealthvault_final.`financial-asset`.model.CashModel
import com.wealthvault_final.`financial-asset`.model.ShareInfo
import com.wealthvault_final.`financial-asset`.model.ShareTo

data class CashSummaryScreen(val request: CashModel, val shareTo: ShareTo) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<CashSummaryScreenModel>()

        LaunchedEffect(Unit) {
            screenModel.initData(request)
            screenModel.initShareInfo(shareTo)
        }

        val state by screenModel.state.collectAsState()

        SummaryContent(
            onBackClick = { navigator.pop() },
            onConfirmClick = {
                screenModel.submitCash(
                    onSuccess = {
                        navigator.popUntilRoot()
                    }
                )
            },
            data = state.cashRequest,
            shareInfo = state.shareTo,
            isSaving = state.isLoading // 🌟 ส่งสถานะ Loading
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryContent(
    onBackClick: () -> Unit = {},
    onConfirmClick: () -> Unit = {},
    data: CashModel?,
    shareInfo: ShareTo?,
    isSaving: Boolean = false
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = LightBg,
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
                    title = {
                        Text("สรุป", style = MaterialTheme.typography.titleLarge, color = LightPrimary)
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null, tint = LightPrimary)
                        }
                    }
                )
            }
        },
        bottomBar = {
            Box(modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(24.dp)) {
                Button(
                    onClick = onConfirmClick,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
                    enabled = !isSaving
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Text("ยืนยัน", color = Color.White, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // 🌟 แก้ไขข้อความหัวข้อ
            Text("รายละเอียดเงินสดและทองคำ", style = MaterialTheme.typography.titleMedium, color = LightPrimary)
            Spacer(modifier = Modifier.height(12.dp))

            SummaryCard(data ?: return@Column)

            Spacer(modifier = Modifier.height(24.dp))

            Text("แชร์", style = MaterialTheme.typography.titleMedium, color = LightPrimary)
            Spacer(modifier = Modifier.height(12.dp))

            ShareSection(shareInfo)

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SummaryCard(data: CashModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, LightBorder.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // 🌟 ปรับ Label และใช้ formatAmount
            SummaryRow("ชื่อรายการ", data.cashName)
            SummaryRow("มูลค่า / จำนวน", "${formatAmount(data.amount)} บาท")
            SummaryRow("คำอธิบาย", data.description.ifBlank { "ไม่มีคำอธิบาย" })

            Spacer(modifier = Modifier.height(24.dp))
            Text("ข้อมูลอ้างอิง", style = MaterialTheme.typography.bodyMedium, color = LightText, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            if (data.attachments.isEmpty()) {
                Text("ไม่มีไฟล์แนบ", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 4.dp)
                ) {
                    items(data.attachments) { img ->
                        val isPdf = img.name.lowercase().endsWith(".pdf") || img.type.name.equals("PDF", ignoreCase = true)

                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .border(1.dp, LightBorder, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isPdf) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color(0xFFE57373), modifier = Modifier.size(28.dp))
                                    Text(img.name, style = MaterialTheme.typography.labelSmall, color = LightText, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(horizontal = 4.dp))
                                }
                            } else {
                                val imageData = img.platformData as? ByteArray
                                if (imageData != null) {
                                    AsyncImage(model = imageData, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                                } else {
                                    Icon(Icons.Default.Image, contentDescription = null, tint = LightPrimary.copy(alpha = 0.3f), modifier = Modifier.size(28.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = LightText.copy(alpha = 0.8f))
        Text(value, style = MaterialTheme.typography.bodyMedium, color = LightText, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ShareSection(shareInfo: ShareTo?) {
    if (shareInfo == null) return

    val isEmpty = shareInfo.friend.isEmpty() && shareInfo.group.isEmpty() && shareInfo.email.isEmpty()

    if (isEmpty) {
        Text(
            text = "ไม่มีการแชร์ข้อมูล",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            textAlign = TextAlign.Center
        )
    } else {
        // 🌟 รวบทุกรายการไว้ใน Card สีขาวใบเดียว
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, LightBorder.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                var isFirst = true

                shareInfo.friend.forEach { friend ->
                    if (!isFirst) HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = LightBg)
                    UserShareCard(friend)
                    isFirst = false
                }

                shareInfo.group.forEach { group ->
                    if (!isFirst) HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = LightBg)
                    GroupShareCard(group)
                    isFirst = false
                }

                shareInfo.email.forEach { email ->
                    if (!isFirst) HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = LightBg)
                    EmailShareCard(email)
                    isFirst = false
                }
            }
        }
    }
}

// ==========================================
// 🌟 Custom UI Cards สำหรับการแชร์ตามภาพต้นแบบ
// ==========================================

@Composable
fun UserShareCard(friend: ShareInfo) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(46.dp).clip(CircleShape).background(LightBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.Person, contentDescription = null, tint = LightText.copy(alpha = 0.4f), modifier = Modifier.size(24.dp))
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = friend.name ?: "",
            style = MaterialTheme.typography.bodyLarge,
            color = LightText,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun GroupShareCard(group: ShareInfo) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(46.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFE0E0E0)),
            contentAlignment = Alignment.Center
        ) {
            // Placeholder กลุ่ม
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(group.name ?: "", style = MaterialTheme.typography.bodyLarge, color = LightText, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .background(LightBg, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.Group, contentDescription = null, tint = LightPrimary, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("5", style = MaterialTheme.typography.labelSmall, color = LightPrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun EmailShareCard(email: ShareInfo) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .border(1.dp, LightPrimary.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.Email, contentDescription = null, tint = LightPrimary, modifier = Modifier.size(24.dp))
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(email.name ?: "", style = MaterialTheme.typography.bodyLarge, color = LightText)
    }
}
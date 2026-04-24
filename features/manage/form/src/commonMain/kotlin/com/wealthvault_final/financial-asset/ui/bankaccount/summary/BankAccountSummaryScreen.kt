package com.wealthvault_final.`financial-asset`.ui.bankaccount.summary

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
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
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault_final.`financial-asset`.model.BankAccountModel
import com.wealthvault_final.`financial-asset`.model.ShareTo
import com.wealthvault_final.`financial-asset`.ui.components.ShareItemCard

val WealthVaultBrown = Color(0xFFB37E61)
val WealthVaultBackground = Color(0xFFFFF8F3)

data class BankAccountSummaryScreen(val request: BankAccountModel, val shareTo: ShareTo) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<BankAccountSummaryScreenModel>()

        // 🚩 ส่งข้อมูลเข้า ScreenModel
        LaunchedEffect(Unit) {
            screenModel.initData(request)
            screenModel.initShareInfo(shareTo)
        }

        val state by screenModel.state.collectAsState()

        SummaryContent(
            onBackClick = { navigator.pop() },
            onConfirmClick = {
                screenModel.submitBankAccount(
                    onSuccess = {
                        // 🌟 เปลี่ยนเป็น popUntilRoot เพื่อเด้งกลับหน้าลิสต์ Asset ทันที
                        navigator.popUntilRoot()
                    }
                )
            },
            data = state.bankAccountRequest,
            shareInfo = state.shareTo
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryContent(
    onBackClick: () -> Unit = {},
    onConfirmClick: () -> Unit = {},
    data: BankAccountModel?,
    shareInfo: ShareTo?
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        containerColor = WealthVaultBackground,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
                title = { Text("สรุปข้อมูล", color = WealthVaultBrown, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = WealthVaultBrown)
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = WealthVaultBackground,
                shadowElevation = 8.dp
            ) {
                Button(
                    onClick = onConfirmClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = WealthVaultBrown)
                ) {
                    Text("ยืนยันการบันทึก", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()) // เลื่อนทั้งหน้าจอ
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text("รายละเอียดทรัพย์สิน", color = WealthVaultBrown, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            SummaryCard(data ?: return@Column)

            Spacer(modifier = Modifier.height(24.dp))

            Text("การแชร์ข้อมูล", color = WealthVaultBrown, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            ShareSection(shareInfo)

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SummaryCard(data: BankAccountModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF3E9D8))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            SummaryRow("ชื่อบัญชี", data.name)
            SummaryRow("ประเภท", data.type)
            SummaryRow("ธนาคาร", data.bankName)
            SummaryRow("เลขที่บัญชี", data.bankId)
            SummaryRow("จำนวนเงิน", data.amount.toString())
            SummaryRow("หมายเหตุ", data.description.ifBlank { "-" })

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = WealthVaultBackground)
            Spacer(modifier = Modifier.height(16.dp))

            Text("หลักฐานอ้างอิง", color = WealthVaultBrown, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(12.dp))

            if (data.attachments.isEmpty()) {
                Text("ไม่มีไฟล์แนบ", color = Color.LightGray, fontSize = 12.sp)
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 4.dp)
                ) {
                    items(data.attachments) { img ->
                        val isPdf = img.name.lowercase().endsWith(".pdf") || img.type.name.equals("PDF", ignoreCase = true)

                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(WealthVaultBackground)
                                .border(1.dp, Color(0xFFF3E9D8), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isPdf) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color(0xFFE57373), modifier = Modifier.size(30.dp))
                                    Text(img.name, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(horizontal = 4.dp))
                                }
                            } else {
                                val imageData = img.platformData as? ByteArray
                                if (imageData != null) {
                                    AsyncImage(model = imageData, contentDescription = null, contentScale = ContentScale.Crop)
                                } else {
                                    Icon(Icons.Default.Image, contentDescription = null, tint = Color.LightGray)
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
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = WealthVaultBrown, fontSize = 14.sp)
        Text(value, color = Color.DarkGray, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ShareSection(shareInfo: ShareTo?) {
    if (shareInfo == null) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF3E5D8))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            val isEmpty = shareInfo.friend.isEmpty() && shareInfo.group.isEmpty() && shareInfo.email.isEmpty()

            if (isEmpty) {
                Text("ไม่ได้แชร์ข้อมูลให้ใคร", color = Color.Gray, modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp), textAlign = TextAlign.Center)
            } else {
                shareInfo.friend.forEach { ShareItemCard(name = it.name ?: "", date = it.date ?: "") }
                shareInfo.group.forEach { ShareItemCard(name = it.name ?: "", groupCount = "5", date = it.date ?: "") }

                if (shareInfo.email.isNotEmpty() && (shareInfo.friend.isNotEmpty() || shareInfo.group.isNotEmpty())) {
                    HorizontalDivider(color = WealthVaultBackground, thickness = 1.dp)
                }

                shareInfo.email.forEach { ShareItemCard(name = it.name ?: "", isEmail = true, date = it.date ?: "") }
            }
        }
    }
}
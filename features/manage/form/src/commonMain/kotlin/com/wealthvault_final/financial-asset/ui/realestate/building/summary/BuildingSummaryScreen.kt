package com.wealthvault_final.`financial-asset`.ui.realestate.building.summary

// 🌟 Import Theme และ Utils
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_back
import com.wealthvault.core.generated.resources.ic_common_pdf
import com.wealthvault.core.generated.resources.ic_form_email_outline
import com.wealthvault.core.generated.resources.ic_form_photo
import com.wealthvault.core.generated.resources.ic_nav_profile
import com.wealthvault.core.generated.resources.ic_nav_social
import com.wealthvault.core.theme.*
import com.wealthvault.core.utils.formatAmount
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault_final.`financial-asset`.model.BuildingModel
import com.wealthvault_final.`financial-asset`.model.ShareInfo
import com.wealthvault_final.`financial-asset`.model.ShareTo
import com.wealthvault_final.`financial-asset`.ui.components.maptype.buildingTypes // 🌟 Import Map
import org.jetbrains.compose.resources.painterResource

data class BuildingSummaryScreen(val request: BuildingModel, val shareTo: ShareTo) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<BuildingSummaryScreenModel>()

        LaunchedEffect(Unit) {
            screenModel.initData(request)
            screenModel.initShareInfo(shareTo)
        }

        val state by screenModel.state.collectAsState()

        SummaryContent(
            onBackClick = { navigator.pop() },
            onConfirmClick = {
                screenModel.submitBuilding(onSuccess = {
                    navigator.popUntilRoot()
                })
            },
            data = state.buildingRequest,
            shareInfo = state.shareTo,
            isSaving = state.isLoading
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryContent(
    onBackClick: () -> Unit = {},
    onConfirmClick: () -> Unit = {},
    data: BuildingModel?,
    shareInfo: ShareTo?,
    isSaving: Boolean = false
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = LightBg,
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 16.dp, top = 24.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_common_back),
                        contentDescription = "Back",
                        tint = LightPrimary,
                        modifier = Modifier.size(24.dp).clickable { onBackClick() }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = "สรุป", style = MaterialTheme.typography.titleLarge, color = LightPrimary)
                }
            }
        },
        bottomBar = {
            Box(modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 24.dp).padding(bottom = 24.dp)) {
                Button(
                    onClick = onConfirmClick,
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
                    enabled = !isSaving
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Text("ยืนยัน", color = Color.White, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 24.dp).verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text("ข้อมูลอาคาร ตึก", style = MaterialTheme.typography.titleMedium, color = LightPrimary)
            Spacer(modifier = Modifier.height(16.dp))

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
fun SummaryCard(data: BuildingModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, LightBorder.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // 🌟 1. Map ประเภทสิ่งปลูกสร้าง
            val mappedType = buildingTypes.find { it.first == data.type }?.second ?: data.type

            SummaryRow("ประเภท", mappedType)
            SummaryRow("ชื่ออาคาร", data.buildingName)
            SummaryRow("ขนาดพื้นที่", "${data.area} ตร.ม.")
            SummaryRow("มูลค่าประมาณการ", "${formatAmount(data.amount)} บาท")

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = LightBg)
            Spacer(modifier = Modifier.height(12.dp))

            val fullAddress = listOf(
                data.locationAddress, data.locationSubDistrict,
                data.locationDistrict, data.locationProvince, data.locationPostalCode
            ).filter { it.isNotBlank() }.joinToString(" ")

            SummaryRow("ที่อยู่", fullAddress.ifBlank { "-" })

            // --- ข้อมูลอ้างอิงที่ดิน ---
            if (data.referenceIds.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))
                Text("ที่ดินอ้างอิง", style = MaterialTheme.typography.bodyMedium, color = LightPrimary, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                data.referenceIds.forEach { ref ->
                    ReferenceItemCard(title = ref.areaName, subTitle = "เลขโฉนด: ${ref.areaId}")
                }
            }

            // --- ข้อมูลอ้างอิงประกัน ---
            if (data.insIds.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))
                Text("ประกันภัยอ้างอิง", style = MaterialTheme.typography.bodyMedium, color = LightPrimary, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                data.insIds.forEach { ins ->
                    ReferenceItemCard(title = ins.insName, subTitle = "เลขกรมธรรม์: ${ins.insId}")
                }
            }

            // --- 🌟 2. ส่วนคำอธิบายแบบ Master UI (มีกรอบและเลื่อนได้) ---
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "คำอธิบาย",
                style = MaterialTheme.typography.labelMedium,
                color = LightText.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 120.dp)
                    .background(LightSoftWhite, RoundedCornerShape(12.dp))
                    .border(1.dp, LightBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .verticalScroll(rememberScrollState())
                    .padding(12.dp)
            ) {
                Text(
                    text = data.description.ifBlank { "-" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (data.description.isNotBlank()) LightText else Color.Gray.copy(alpha = 0.6f)
                )
            }

            // --- รูปภาพหลักฐาน ---
            Spacer(modifier = Modifier.height(20.dp))
            Text("ข้อมูลอ้างอิง (ไฟล์แนบ)", style = MaterialTheme.typography.bodyMedium, color = LightPrimary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            if (data.attachments.isEmpty()) {
                Text("ไม่มีไฟล์แนบ", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 4.dp)
                ) {
                    items(data.attachments) { img ->
                        val isPdf = img.name.lowercase().endsWith(".pdf") || img.type.name.equals("PDF", ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(LightBg)
                                .border(1.dp, LightBorder, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isPdf) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(painter = painterResource(Res.drawable.ic_common_pdf), contentDescription = null, tint = Color(0xFFE57373), modifier = Modifier.size(28.dp))
                                    Text(img.name, style = MaterialTheme.typography.labelSmall, color = LightText, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(horizontal = 4.dp))
                                }
                            } else {
                                val imageData = img.platformData as? ByteArray
                                if (imageData != null) {
                                    AsyncImage(model = imageData, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                                } else {
                                    Icon(painter = painterResource(Res.drawable.ic_form_photo), contentDescription = null, tint = LightPrimary.copy(alpha = 0.3f), modifier = Modifier.size(28.dp))
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
        verticalAlignment = Alignment.Top // 🌟 รองรับ Value ยาวๆ ให้ชิดบน
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = LightText.copy(alpha = 0.7f),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            color = LightText,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(2f)
        )
    }
}

@Composable
fun ReferenceItemCard(title: String, subTitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .background(LightSoftWhite, RoundedCornerShape(12.dp))
            .border(1.dp, LightBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Info, contentDescription = null, tint = LightPrimary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = title, style = MaterialTheme.typography.bodyMedium, color = LightText, fontWeight = FontWeight.Bold)
            Text(text = subTitle, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
        }
    }
}

@Composable
fun ShareSection(shareInfo: ShareTo?) {
    if (shareInfo == null) return

    val isEmpty = shareInfo.friend.isEmpty() && shareInfo.group.isEmpty() && shareInfo.email.isEmpty()

    if (isEmpty) {
        Text("ไม่มีการแชร์ข้อมูล", style = MaterialTheme.typography.bodyMedium, color = Color.Gray, modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), textAlign = TextAlign.Center)
    } else {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, LightBorder.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                var isFirst = true
                shareInfo.friend.forEach {
                    if (!isFirst) HorizontalDivider(modifier = Modifier.padding(horizontal = 0.dp), color = LightBg)
                    SharedItemSummaryCard(it)
                    isFirst = false
                }
                shareInfo.group.forEach {
                    if (!isFirst) HorizontalDivider(modifier = Modifier.padding(horizontal = 0.dp), color = LightBg)
                    SharedItemSummaryCard(it)
                    isFirst = false
                }
                shareInfo.email.forEach {
                    if (!isFirst) HorizontalDivider(modifier = Modifier.padding(horizontal = 0.dp), color = LightBg)
                    SharedItemSummaryCard(it)
                    isFirst = false
                }
            }
        }
    }
}

@Composable
fun SharedItemSummaryCard(data: ShareInfo) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(LightBg),
            contentAlignment = Alignment.Center
        ) {
            if (!data.profileUrl.isNullOrBlank()) {
                AsyncImage(model = data.profileUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            } else {
                val icon = when (data.typeData) {
                    "E" -> painterResource(Res.drawable.ic_form_email_outline)
                    "G" -> painterResource(Res.drawable.ic_nav_social)
                    else -> painterResource(Res.drawable.ic_nav_profile)
                }
                Icon(painter = icon, contentDescription = null, tint = LightPrimary, modifier = Modifier.size(20.dp))
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = data.name ?: "", style = MaterialTheme.typography.bodyMedium, color = LightText, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if ((data.typeData != "E" && data.subText.isNotBlank()) || data.date != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    if (data.typeData != "E" && data.subText.isNotBlank()) {
                        Row(modifier = Modifier.weight(1f, fill = false).background(LightBg, RoundedCornerShape(200.dp)).padding(horizontal = 6.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                            val icon = if(data.typeData == "G") painterResource(Res.drawable.ic_nav_social) else painterResource(Res.drawable.ic_nav_profile)
                            Icon(painter = icon, contentDescription = null, tint = LightPrimary, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = data.subText, color = LightPrimary, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    } else { Spacer(modifier = Modifier.width(1.dp)) }

                    if (data.date != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = data.date!!, style = MaterialTheme.typography.labelSmall, color = Color.Gray, maxLines = 1)
                    }
                }
            }
        }
    }
}
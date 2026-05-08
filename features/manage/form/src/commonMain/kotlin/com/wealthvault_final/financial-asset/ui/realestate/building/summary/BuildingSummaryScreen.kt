package com.wealthvault_final.`financial-asset`.ui.realestate.building.summary

// 🌟 Import Theme และ Utils
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_back
import com.wealthvault.core.generated.resources.ic_common_pdf
import com.wealthvault.core.generated.resources.ic_form_email_outline
import com.wealthvault.core.generated.resources.ic_form_photo
import com.wealthvault.core.generated.resources.ic_nav_profile
import com.wealthvault.core.generated.resources.ic_nav_social
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault.core.theme.LightText
import com.wealthvault.core.utils.formatAmount
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault_final.`financial-asset`.model.BuildingModel
import com.wealthvault_final.`financial-asset`.model.ShareInfo
import com.wealthvault_final.`financial-asset`.model.ShareTo
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
                    // 🌟 1. ปรับ Padding ของ TopBar ขอบซ้าย-ขวา เป็น 24.dp
                    modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 16.dp, top = 24.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_common_back),
                        contentDescription = "Back",
                        tint = LightPrimary,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onBackClick() }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "สรุป",
                        style = MaterialTheme.typography.titleLarge,
                        color = LightPrimary
                    )
                }
            }
        },
        bottomBar = {
            // 🌟 ปรับ Padding ของปุ่มให้เป็น horizontal = 24.dp, bottom = 24.dp
            Box(modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 24.dp).padding(bottom = 24.dp)) {
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
                .padding(horizontal = 24.dp) // 🌟 ขอบซ้ายขวา 24.dp
                .verticalScroll(rememberScrollState())
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
        Column(modifier = Modifier.padding(20.dp)) {
            // --- ข้อมูลพื้นฐาน ---
            SummaryRow("ประเภท", data.type)
            SummaryRow("ชื่ออาคาร", data.buildingName)
            SummaryRow("ขนาดพื้นที่", "${data.area} ตร.ม.")
            SummaryRow("มูลค่าประมาณการ", "${formatAmount(data.amount)} บาท")

            // --- ที่ตั้ง ---
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = LightBg)
            Spacer(modifier = Modifier.height(12.dp))

            // 🌟 รวมที่อยู่ทุกช่องให้เป็นบรรทัดเดียว (ถ้าช่องไหนว่าง จะถูกข้ามไปอัตโนมัติ)
            val fullAddress = listOf(
                data.locationAddress,
                data.locationSubDistrict,
                data.locationDistrict,
                data.locationProvince,
                data.locationPostalCode
            ).filter { it.isNotBlank() }.joinToString(" ")

            SummaryRow("ที่อยู่", fullAddress.ifBlank { "-" })

            // --- ข้อมูลอ้างอิงที่ดิน ---
            if (data.referenceIds.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))
                Text("ที่ดินอ้างอิง", style = MaterialTheme.typography.bodyMedium, color = LightPrimary, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                data.referenceIds.forEach { ref ->
                    // 💡 โชว์เป็น รหัสอ้างอิง แทน เพราะข้อมูลที่พกมามีแค่ UUID
                    val shortId = if (ref.areaId.length > 8) ref.areaId.take(8).uppercase() else ref.areaId
                    ReferenceItemCard(title = ref.areaName, subTitle = "รหัสอ้างอิง: ${ref.deedNum}")
                }
            }

            // --- ข้อมูลอ้างอิงประกัน ---
            if (data.insIds.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))
                Text("ประกันภัยอ้างอิง", style = MaterialTheme.typography.bodyMedium, color = LightPrimary, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                data.insIds.forEach { ins ->
                    // 💡 โชว์เป็น รหัสอ้างอิง แทน เพราะข้อมูลที่พกมามีแค่ UUID
                    val shortId = if (ins.insId.length > 8) ins.insId.take(8).uppercase() else ins.insId
                    ReferenceItemCard(title = ins.insName, subTitle = "รหัสอ้างอิง: ${ins.policyNum}")
                }
            }

            SummaryRow("คำอธิบาย", data.description.ifBlank { "-" })

            // --- รูปภาพหลักฐาน ---
            Spacer(modifier = Modifier.height(20.dp))
            Text("ข้อมูลอ้างอิง (ไฟล์แนบ)", style = MaterialTheme.typography.bodyMedium, color = LightPrimary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            if (data.attachments.isEmpty()) {
                Text("ไม่มีไฟล์แนบ", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        // 🌟 เปลี่ยนจาก CenterVertically เป็น Top เพื่อให้ Label อยู่ด้านบนเสมอแม้ข้อความ Value จะยาวจนตัดบรรทัด
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = LightText.copy(alpha = 0.7f),
            modifier = Modifier.weight(1f) // 🌟 ให้ Label กินพื้นที่คงที่
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = LightText,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End, // 🌟 ชิดขวาตามดีไซน์เดิม
            modifier = Modifier.weight(2f), // 🌟 ให้พื้นที่ Value มากกว่าเพื่อให้ตัดบรรทัดได้สวย
            softWrap = true, // 🌟 อนุญาตให้ตัดบรรทัด (Default คือ true อยู่แล้ว)
            maxLines = 5 // 🌟 กำหนดบรรทัดสูงสุดเผื่อไว้ตามความเหมาะสม
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
                    if (!isFirst) HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = LightBg)
                    SharedItemSummaryCard(it)
                    isFirst = false
                }
                shareInfo.group.forEach {
                    if (!isFirst) HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = LightBg)
                    SharedItemSummaryCard(it)
                    isFirst = false
                }
                shareInfo.email.forEach {
                    if (!isFirst) HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = LightBg)
                    SharedItemSummaryCard(it)
                    isFirst = false
                }
            }
        }
    }
}

// ==========================================
// 🌟 Custom UI Component แบบหน้าแชร์ (ไม่มีปุ่มลบ)
// ==========================================
@Composable
fun SharedItemSummaryCard(data: ShareInfo) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile Image / Placeholder
        Box(
            modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(LightBg),
            contentAlignment = Alignment.Center
        ) {
            if (!data.profileUrl.isNullOrBlank()) {
                AsyncImage(
                    model = data.profileUrl,
                    contentDescription = "Profile",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                val icon = when (data.typeData) {
                    "E" -> painterResource(Res.drawable.ic_form_email_outline)
                    "G" -> painterResource(Res.drawable.ic_nav_social)
                    else -> painterResource(Res.drawable.ic_nav_profile)
                }
                Icon(painter = icon, contentDescription = null, tint = LightPrimary, modifier = Modifier.size(24.dp))
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = data.name ?: "",
                style = MaterialTheme.typography.bodyLarge,
                color = LightText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // 🌟 รวบเงื่อนไขให้ วันที่ ไปอยู่ฝั่งขวาเหมือนกันทั้งหมด
            if ((data.typeData != "E" && data.subText.isNotBlank()) || data.date != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween // ดันวันที่ไปชิดขวา
                ) {
                    // Badge (อยู่ซ้าย - ซ่อนถ้าเป็น Email)
                    if (data.typeData != "E" && data.subText.isNotBlank()) {
                        Row(
                            modifier = Modifier
                                .weight(1f, fill = false) // กันข้อความยาวเกิน
                                .background(LightBg, RoundedCornerShape(200.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val icon = if(data.typeData == "G") painterResource(Res.drawable.ic_nav_social) else painterResource(Res.drawable.ic_nav_profile)
                            Icon(painter = icon, contentDescription = null, tint = LightPrimary, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = data.subText,
                                color = LightPrimary,
                                style = MaterialTheme.typography.labelMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp)) // ใส่ช่องว่างเปล่าๆ เพื่อดันให้ SpaceBetween ดันวันที่ไปชิดขวาได้
                    }

                    // วันที่แชร์ล่วงหน้า (อยู่ขวา)
                    if (data.date != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = data.date!!,
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
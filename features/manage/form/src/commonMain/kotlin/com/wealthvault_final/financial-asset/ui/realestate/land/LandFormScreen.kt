package com.wealthvault_final.`financial-asset`.ui.realestate.land

// 🌟 Import Theme
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.building_api.model.GetBuildingData
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_back
import com.wealthvault.core.generated.resources.ic_common_bin
import com.wealthvault.core.generated.resources.ic_common_plus
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault.core.theme.LightText
import com.wealthvault.core.theme.RedErr
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault_final.`financial-asset`.Imagepicker.Attachment
import com.wealthvault_final.`financial-asset`.Imagepicker.rememberFilePicker
import com.wealthvault_final.`financial-asset`.model.LandModel
import com.wealthvault_final.`financial-asset`.model.RefModel
import com.wealthvault_final.`financial-asset`.ui.components.AssetTextField
import com.wealthvault_final.`financial-asset`.ui.components.ReferenceImagepicker
import com.wealthvault_final.`financial-asset`.ui.realestate.land.viewmodel.LandScreenModel
import com.wealthvault_final.`financial-asset`.ui.share.ShareAssetScreen
import org.jetbrains.compose.resources.painterResource

class LandFormScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<LandScreenModel>()

        // 🌟 1. ดึง State ออกมา
        val state by screenModel.state.collectAsState()
        val buildState by screenModel.BuildingState.collectAsState()

        LandInputForm(
            initialData = state, // 🌟 2. โยนค่าเริ่มต้นเข้าไปในฟอร์ม
            onBackClick = { navigator.pop() },
            onNextClick = { data ->
                screenModel.updateForm(data)
                // 🌟 ส่ง data ที่อัปเดตล่าสุดไปหน้าแชร์โดยตรง
                navigator.push(ShareAssetScreen(request = data))
            },
            buildingData = buildState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandInputForm(
    initialData: LandModel, // 🌟 รับ initialData เข้ามา
    onBackClick: () -> Unit = {},
    onNextClick: (LandModel) -> Unit,
    buildingData: List<GetBuildingData>
) {
    // 🌟 ดึงค่าจาก initialData มาใส่ตั้งต้น
    var deedNum by remember { mutableStateOf(initialData.deedNum) }
    var landName by remember { mutableStateOf(initialData.landName) }

    // เรื่องตัวเลข ถ้าเป็น 0.0 ให้แสดงหน้าว่างๆ
    var area by remember { mutableStateOf(if (initialData.area == 0.0) "" else initialData.area.toString()) }
    var amount by remember { mutableStateOf(if (initialData.amount == 0.0) "" else initialData.amount.toString()) }

    var description by remember { mutableStateOf(initialData.description) }
    var locationAddress by remember { mutableStateOf(initialData.locationAddress) }
    var locationSubDistrict by remember { mutableStateOf(initialData.locationSubDistrict) }
    var locationDistrict by remember { mutableStateOf(initialData.locationDistrict) }
    var locationProvince by remember { mutableStateOf(initialData.locationProvince) }
    var locationPostalCode by remember { mutableStateOf(initialData.locationPostalCode) }

    // 🌟 ดึงค่า List ต่างๆ กลับมาใส่ใน State
    val referenceIds = remember { mutableStateListOf<RefModel>().apply { addAll(initialData.referenceIds) } }
    val attachments = remember { mutableStateListOf<Attachment>().apply { addAll(initialData.attachments) } }
    val filePicker = rememberFilePicker { newFiles -> attachments.addAll(newFiles) }
    var showBuildingsheet by remember { mutableStateOf(false) }

    // 🌟 Validation: เช็กข้อมูลจำเป็น รวมที่อยู่ด้วย
    val isFormValid = deedNum.isNotBlank() &&
            landName.isNotBlank() &&
            area.isNotBlank() &&
            locationAddress.isNotBlank() &&
            locationSubDistrict.isNotBlank() &&
            locationDistrict.isNotBlank() &&
            locationProvince.isNotBlank() &&
            locationPostalCode.isNotBlank()

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
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onBackClick() }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "ข้อมูลโฉนดที่ดิน",
                        style = MaterialTheme.typography.titleLarge,
                        color = LightPrimary
                    )
                }
            }
        },
        bottomBar = {
            Box(modifier = Modifier.navigationBarsPadding().padding(horizontal = 24.dp).padding(bottom = 24.dp)) {
                Button(
                    onClick = {
                        val data = LandModel(
                            deedNum = deedNum,
                            landName = landName,
                            area = area.toDoubleOrNull() ?: 0.0,
                            amount = amount.toDoubleOrNull() ?: 0.0,
                            description = description,
                            attachments = attachments.toList(),
                            referenceIds = referenceIds.toList(),
                            locationAddress = locationAddress,
                            locationSubDistrict = locationSubDistrict,
                            locationDistrict = locationDistrict,
                            locationProvince = locationProvince,
                            locationPostalCode = locationPostalCode,
                        )
                        onNextClick(data)
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LightPrimary,
                        disabledContainerColor = LightBorder
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = isFormValid
                ) {
                    Text(
                        text = "ต่อไป",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isFormValid) Color.White else Color.Gray
                    )
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
            Spacer(modifier = Modifier.height(16.dp))

            AssetTextField(value = deedNum, onValueChange = { deedNum = it }, label = "เลขที่โฉนดที่ดิน*", placeholder = "ระบุเลขที่โฉนด")
            AssetTextField(value = landName, onValueChange = { landName = it }, label = "ชื่อเรียกที่ดิน*", placeholder = "เช่น ที่ดินเปล่าเชียงใหม่")

            AssetTextField(
                value = area,
                onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) area = it },
                label = "ขนาดพื้นที่ (ตร.ว.)*",
                placeholder = "0.00",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            AssetTextField(
                value = amount,
                onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) amount = it },
                label = "มูลค่าประเมิน (บาท)",
                placeholder = "0.00",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // 🌟 เติมดอกจันให้รู้ว่าบังคับกรอก
            AssetTextField(value = locationAddress, onValueChange = { locationAddress = it }, label = "ที่อยู่*", placeholder = "ซอย, ถนน")
            AssetTextField(value = locationSubDistrict, onValueChange = { locationSubDistrict = it }, label = "ตำบล / แขวง*", placeholder = "ระบุตำบล")
            AssetTextField(value = locationDistrict, onValueChange = { locationDistrict = it }, label = "อำเภอ / เขต*", placeholder = "ระบุอำเภอ")
            AssetTextField(value = locationProvince, onValueChange = { locationProvince = it }, label = "จังหวัด*", placeholder = "ระบุจังหวัด")
            AssetTextField(
                value = locationPostalCode,
                onValueChange = { if (it.isEmpty() || it.all { char -> char.isDigit() }) locationPostalCode = it },
                label = "รหัสไปรษณีย์*",
                placeholder = "ระบุรหัสไปรษณีย์",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                ReferenceSectionHeader(title = "อาคาร / สิ่งปลูกสร้างบนที่ดินนี้", onAddClick = { showBuildingsheet = true })
                Spacer(modifier = Modifier.height(8.dp))

                if (referenceIds.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .background(LightSoftWhite, RoundedCornerShape(12.dp))
                            .border(1.dp, LightBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .clickable { showBuildingsheet = true },
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "ยังไม่มีรายการ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.LightGray,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        referenceIds.forEach { build ->
                            val shortId = if (build.areaId.length > 8) build.areaId.take(8).uppercase() else build.areaId
                            ReferenceItemRow(name = build.areaName) {
                                referenceIds.remove(build)
                            }
                        }
                    }
                }
            }

            AssetTextField(value = description, onValueChange = { description = it }, label = "รายละเอียดเพิ่มเติม", placeholder = "ระบุรายละเอียดเพิ่มเติม", isMultiLine = true)

            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                ReferenceImagepicker(
                    attachments = attachments,
                    onAddImage = { filePicker.launchImage() },
                    onAddPdf = { filePicker.launchPdf() },
                    onRemove = { item -> attachments.remove(item) }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        if (showBuildingsheet) {
            BuildingSelectionBottomSheet(
                alreadySelected = referenceIds,
                onDismiss = { showBuildingsheet = false },
                onConfirm = { newSelection ->
                    referenceIds.clear()
                    referenceIds.addAll(newSelection)
                    showBuildingsheet = false
                },
                BuildingData = buildingData
            )
        }
    }
}

// 🌟 Reusable Components สำหรับส่วนอ้างอิง
@Composable
fun ReferenceSectionHeader(title: String, onAddClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.bodyMedium, color = LightPrimary)
        Icon(
            painter = painterResource(Res.drawable.ic_common_plus),
            contentDescription = null,
            tint = LightPrimary,
            modifier = Modifier
                .size(24.dp)
                .clickable { onAddClick() }
        )
    }
}

@Composable
fun ReferenceItemRow(name: String, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(LightSoftWhite, RoundedCornerShape(12.dp))
            .border(1.dp, LightBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(name, style = MaterialTheme.typography.bodyLarge, color = LightText)
        }
        Icon(
            painter = painterResource(Res.drawable.ic_common_bin),
            contentDescription = null,
            tint = RedErr.copy(alpha = 0.7f),
            modifier = Modifier
                .size(24.dp)
                .clickable { onRemove() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildingSelectionBottomSheet(
    alreadySelected: List<RefModel>,
    onDismiss: () -> Unit,
    onConfirm: (List<RefModel>) -> Unit,
    BuildingData: List<GetBuildingData>
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val buildings = remember(BuildingData) { BuildingData.map { RefModel(areaName = it.name ?: "ไม่มีชื่ออาคาร", areaId = it.id ?: "") } }
    val tempSelected = remember { mutableStateListOf<RefModel>().apply { addAll(alreadySelected) } }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = Color.White) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).navigationBarsPadding()) {
            Text("เลือกข้อมูลอาคาร/ตึกอ้างอิง", style = MaterialTheme.typography.titleMedium, color = LightPrimary)
            Spacer(modifier = Modifier.height(16.dp))

            if (buildings.isEmpty()) {
                Text("ไม่มีข้อมูลอาคารในระบบ", color = Color.Gray, modifier = Modifier.padding(vertical = 16.dp))
            } else {
                LazyColumn(modifier = Modifier.weight(1f, fill = false), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(buildings) { build ->
                        val isChecked = tempSelected.any { it.areaId == build.areaId }
                        Surface(
                            onClick = { if (isChecked) tempSelected.removeAll { it.areaId == build.areaId } else tempSelected.add(build) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isChecked) LightBg else Color.White,
                            border = BorderStroke(1.dp, if (isChecked) LightPrimary else LightBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(build.areaName, fontWeight = FontWeight.Medium)
                                }
                                Checkbox(checked = isChecked, onCheckedChange = null, colors = CheckboxDefaults.colors(checkedColor = LightPrimary))
                            }
                        }
                    }
                }
            }
            Button(onClick = { onConfirm(tempSelected.toList()) }, modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp).height(50.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = LightPrimary)) {
                Text("ตกลง (${tempSelected.size}) รายการ", color = Color.White)
            }
        }
    }
}
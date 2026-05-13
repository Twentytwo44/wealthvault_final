package com.wealthvault_final.`financial-asset`.ui.realestate.land

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
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
import com.wealthvault.core.theme.*
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

        val state by screenModel.state.collectAsState()
        val buildState by screenModel.BuildingState.collectAsState()

        LandInputForm(
            initialData = state,
            onBackClick = { navigator.pop() },
            onNextClick = { data ->
                screenModel.updateForm(data)
                navigator.push(ShareAssetScreen(request = data))
            },
            buildingData = buildState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandInputForm(
    initialData: LandModel,
    onBackClick: () -> Unit = {},
    onNextClick: (LandModel) -> Unit,
    buildingData: List<GetBuildingData>
) {
    var deedNum by remember { mutableStateOf(initialData.deedNum) }
    var landName by remember { mutableStateOf(initialData.landName) }
    var area by remember { mutableStateOf(if (initialData.area == 0.0) "" else initialData.area.toString()) }
    var amount by remember { mutableStateOf(if (initialData.amount == 0.0) "" else initialData.amount.toString()) }
    var description by remember { mutableStateOf(initialData.description) }
    var locationAddress by remember { mutableStateOf(initialData.locationAddress) }
    var locationSubDistrict by remember { mutableStateOf(initialData.locationSubDistrict) }
    var locationDistrict by remember { mutableStateOf(initialData.locationDistrict) }
    var locationProvince by remember { mutableStateOf(initialData.locationProvince) }
    var locationPostalCode by remember { mutableStateOf(initialData.locationPostalCode) }

    val referenceIds = remember { mutableStateListOf<RefModel>().apply { addAll(initialData.referenceIds) } }
    val attachments = remember { mutableStateListOf<Attachment>().apply { addAll(initialData.attachments) } }
    val filePicker = rememberFilePicker { newFiles -> attachments.addAll(newFiles) }
    var showBuildingsheet by remember { mutableStateOf(false) }

    val isFormValid = deedNum.isNotBlank() && landName.isNotBlank() && area.isNotBlank() &&
            locationAddress.isNotBlank() && locationSubDistrict.isNotBlank() &&
            locationDistrict.isNotBlank() && locationProvince.isNotBlank() &&
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
            Box(modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(24.dp)) {
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
                    modifier = Modifier.fillMaxWidth().height(46.dp), // 🌟 ปรับสูง 46.dp ตามต้นแบบ
                    colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
                    shape = RoundedCornerShape(12.dp),
                    enabled = isFormValid
                ) {
                    Text("ต่อไป", style = MaterialTheme.typography.bodyLarge, color = Color.White)
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

            // 🌟 ข้อมูลทั่วไป (ใช้ AssetTextField ที่แก้เป็น BasicTextField แล้ว)
            AssetTextField(value = deedNum, onValueChange = { deedNum = it }, label = "เลขที่โฉนดที่ดิน*", placeholder = "ระบุเลขที่โฉนด")
            AssetTextField(value = landName, onValueChange = { landName = it }, label = "ชื่อเรียกที่ดิน*", placeholder = "เช่น ที่ดินเปล่าเชียงใหม่")

            CustomTextField(
                value = area,
                onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) area = it },
                label = "ขนาดพื้นที่ (ตร.ว.)*",
                placeholder = "0.00",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            CustomTextField(
                value = amount,
                onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) amount = it },
                label = "มูลค่าประเมิน (บาท)",
                placeholder = "0.00",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // 🌟 ส่วนที่อยู่
            AssetTextField(value = locationAddress, onValueChange = { locationAddress = it }, label = "ที่อยู่*", placeholder = "ซอย, ถนน")
            AssetTextField(value = locationSubDistrict, onValueChange = { locationSubDistrict = it }, label = "ตำบล / แขวง*", placeholder = "ระบุตำบล")
            AssetTextField(value = locationDistrict, onValueChange = { locationDistrict = it }, label = "อำเภอ / เขต*", placeholder = "ระบุอำเภอ")
            AssetTextField(value = locationProvince, onValueChange = { locationProvince = it }, label = "จังหวัด*", placeholder = "ระบุจังหวัด")

            CustomTextField(
                value = locationPostalCode,
                onValueChange = { if (it.isEmpty() || it.all { char -> char.isDigit() }) locationPostalCode = it },
                label = "รหัสไปรษณีย์*",
                placeholder = "ระบุรหัสไปรษณีย์",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // 🌟 ส่วนอ้างอิงอาคาร
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                ReferenceSectionHeader(title = "อาคาร / สิ่งปลูกสร้างบนที่ดินนี้", onAddClick = { showBuildingsheet = true })
                Spacer(modifier = Modifier.height(8.dp))

                if (referenceIds.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp) // ปรับสูงเท่า TextField ปกติ
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
                            ReferenceItemRow(name = build.areaName) {
                                referenceIds.remove(build)
                            }
                        }
                    }
                }
            }

            AssetTextField(value = description, onValueChange = { description = it }, label = "รายละเอียดเพิ่มเติม", placeholder = "ระบุรายละเอียดเพิ่มเติม", isMultiLine = true)

            Spacer(modifier = Modifier.height(8.dp))

            ReferenceImagepicker(
                attachments = attachments,
                onAddImage = { filePicker.launchImage() },
                onAddPdf = { filePicker.launchPdf() },
                onRemove = { item -> attachments.remove(item) }
            )

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

// 🌟 Component ย่อย CustomTextField (มาตรฐาน Master UI)
@Composable
private fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    readOnly: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onClick: (() -> Unit)? = null
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = LightPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        Box {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                readOnly = readOnly,
                singleLine = true,
                keyboardOptions = keyboardOptions,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF3A2F2A)),
                cursorBrush = SolidColor(LightPrimary),
                modifier = Modifier.fillMaxWidth().height(44.dp),
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(LightSoftWhite, RoundedCornerShape(12.dp))
                            .border(1.dp, LightBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            if (value.isEmpty()) {
                                Text(text = placeholder, color = Color.LightGray, style = MaterialTheme.typography.bodyLarge)
                            }
                            innerTextField()
                        }
                        if (trailingIcon != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            trailingIcon()
                        }
                    }
                }
            )
            if (onClick != null) {
                Box(modifier = Modifier.matchParentSize().clip(RoundedCornerShape(12.dp)).clickable { onClick() })
            }
        }
    }
}

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
            modifier = Modifier.size(24.dp).clickable { onAddClick() }
        )
    }
}

@Composable
fun ReferenceItemRow(name: String, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp) // ปรับความสูงรายการที่เลือกแล้วให้เท่า TextField
            .background(LightSoftWhite, RoundedCornerShape(12.dp))
            .border(1.dp, LightBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(name, style = MaterialTheme.typography.bodyLarge, color = LightText, modifier = Modifier.weight(1f))
        Icon(
            painter = painterResource(Res.drawable.ic_common_bin),
            contentDescription = null,
            tint = RedErr.copy(alpha = 0.7f),
            modifier = Modifier.size(20.dp).clickable { onRemove() }
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
                                Text(build.areaName, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                                Checkbox(checked = isChecked, onCheckedChange = null, colors = CheckboxDefaults.colors(checkedColor = LightPrimary))
                            }
                        }
                    }
                }
            }
            Button(
                onClick = { onConfirm(tempSelected.toList()) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp).height(46.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LightPrimary)
            ) {
                Text("ตกลง (${tempSelected.size}) รายการ", color = Color.White)
            }
        }
    }
}
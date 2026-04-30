package com.wealthvault_final.`financial-asset`.ui.realestate.land

// 🌟 Import Theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.building_api.model.GetBuildingData
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

class LandFormScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<LandScreenModel>()
        val buildState by screenModel.BuildingState.collectAsState()

        LandInputForm(
            onBackClick = { navigator.pop() },
            onNextClick = { data ->
                screenModel.updateForm(data)
                // 🌟 ส่ง state ล่าสุดไปหน้าแชร์
                navigator.push(ShareAssetScreen(request = screenModel.state.value))
            },
            buildingData = buildState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandInputForm(
    onBackClick: () -> Unit = {},
    onNextClick: (LandModel) -> Unit,
    buildingData: List<GetBuildingData>
) {
    var deedNum by remember { mutableStateOf("") }
    var landName by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var locationAddress by remember { mutableStateOf("") }
    var locationSubDistrict by remember { mutableStateOf("") }
    var locationDistrict by remember { mutableStateOf("") }
    var locationProvince by remember { mutableStateOf("") }
    var locationPostalCode by remember { mutableStateOf("") }

    val referenceIds = remember { mutableStateListOf<RefModel>() }
    val attachments = remember { mutableStateListOf<Attachment>() }
    val filePicker = rememberFilePicker { newFiles -> attachments.addAll(newFiles) }
    var showBuildingsheet by remember { mutableStateOf(false) }

    // 🌟 Validation: เช็กข้อมูลจำเป็น
    val isFormValid = deedNum.isNotBlank() && landName.isNotBlank() && area.isNotBlank()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = LightBg,
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
                    title = { Text("ข้อมูลโฉนดที่ดิน", color = LightPrimary, style = MaterialTheme.typography.titleLarge) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null, tint = LightPrimary)
                        }
                    }
                )
            }
        },
        bottomBar = {
            Box(modifier = Modifier.navigationBarsPadding().padding(24.dp)) {
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
                    colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
                    shape = RoundedCornerShape(12.dp),
                    enabled = isFormValid
                ) {
                    Text("ต่อไป", style = MaterialTheme.typography.titleMedium, color = Color.White)
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

            // --- ส่วนที่ตั้ง ---
            Spacer(modifier = Modifier.height(16.dp))
            Text("สถานที่ตั้ง", style = MaterialTheme.typography.titleMedium, color = LightPrimary)
            AssetTextField(value = locationAddress, onValueChange = { locationAddress = it }, label = "ที่อยู่", placeholder = "ซอย, ถนน")
            AssetTextField(value = locationSubDistrict, onValueChange = { locationSubDistrict = it }, label = "ตำบล / แขวง", placeholder = "ระบุตำบล")
            AssetTextField(value = locationDistrict, onValueChange = { locationDistrict = it }, label = "อำเภอ / เขต", placeholder = "ระบุอำเภอ")
            AssetTextField(value = locationProvince, onValueChange = { locationProvince = it }, label = "จังหวัด", placeholder = "ระบุจังหวัด")
            AssetTextField(
                value = locationPostalCode,
                onValueChange = { if (it.isEmpty() || it.all { char -> char.isDigit() }) locationPostalCode = it },
                label = "รหัสไปรษณีย์",
                placeholder = "ระบุรหัสไปรษณีย์",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // --- ส่วนอ้างอิงข้อมูลอาคาร ---
            ReferenceSectionHeader(title = "อาคาร / สิ่งปลูกสร้างบนที่ดินนี้", onAddClick = { showBuildingsheet = true })
            if (referenceIds.isNotEmpty()) {
                referenceIds.forEach { build ->
                    ReferenceItemRow(name = build.areaName, subText = "ID อาคาร: ${build.areaId}") { referenceIds.remove(build) }
                }
            }

            AssetTextField(value = description, onValueChange = { description = it }, label = "รายละเอียดเพิ่มเติม", placeholder = "ระบุรายละเอียดเพิ่มเติม", isMultiLine = true)

            Spacer(modifier = Modifier.height(24.dp))

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

// 🌟 Reusable Components สำหรับส่วนอ้างอิง
@Composable
fun ReferenceSectionHeader(title: String, onAddClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.bodyMedium, color = LightPrimary, fontWeight = FontWeight.Bold)
        IconButton(onClick = onAddClick) {
            Icon(Icons.Default.Add, contentDescription = null, tint = LightPrimary)
        }
    }
}

@Composable
fun ReferenceItemRow(name: String, subText: String, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(LightSoftWhite, RoundedCornerShape(12.dp))
            .border(1.dp, LightBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(name, style = MaterialTheme.typography.bodyLarge, color = LightText)
            Text(subText, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
        }
        IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
            Icon(Icons.Default.Delete, contentDescription = null, tint = RedErr.copy(alpha = 0.7f))
        }
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
                                Text("ID อาคาร: ${build.areaId}", fontSize = 12.sp, color = Color.Gray)
                            }
                            Checkbox(checked = isChecked, onCheckedChange = null, colors = CheckboxDefaults.colors(checkedColor = LightPrimary))
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
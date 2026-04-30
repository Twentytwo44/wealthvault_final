package com.wealthvault.financiallist.ui.asset.form.building

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault.core.theme.LightText
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.insurance_api.model.GetInsuranceData
import com.wealthvault.land_api.model.GetLandData
import com.wealthvault_final.`financial-asset`.Imagepicker.Attachment
import com.wealthvault_final.`financial-asset`.Imagepicker.rememberFilePicker
import com.wealthvault_final.`financial-asset`.model.BuildingModel
import com.wealthvault_final.`financial-asset`.model.InsRefModel
import com.wealthvault_final.`financial-asset`.model.RefModel
import com.wealthvault_final.`financial-asset`.ui.components.AssetTextField
import com.wealthvault_final.`financial-asset`.ui.components.ReferenceImagepicker
import com.wealthvault_final.`financial-asset`.ui.components.maptype.DropdownInput
import com.wealthvault_final.`financial-asset`.ui.components.maptype.buildingTypes

class BuildingFormScreen(val id: String, val buildingData: BuildingModel) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<BuildingScreenModel>()
        val landState by screenModel.LandState.collectAsState()
        val insState by screenModel.InsState.collectAsState()

        BuildingInputForm(
            onBackClick = { navigator.pop() },
            onNextClick = { data, addedList, deletedList, addRef, deleteRef, addIns, deleteIns ->
                screenModel.updateForm(data)
                screenModel.updateAttachment(addedList, deletedList, addRef, deleteRef, addIns, deleteIns)
                screenModel.submitLand(id)
            },
            landData = landState,
            insData = insState,
            buildingData = buildingData
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildingInputForm(
    onBackClick: () -> Unit = {},
    onNextClick: (BuildingModel, List<Attachment>, List<Attachment>, List<RefModel>, List<RefModel>, List<InsRefModel>, List<InsRefModel>) -> Unit,
    insData: List<GetInsuranceData>,
    landData: List<GetLandData>,
    buildingData: BuildingModel
) {
    // 🌟 1. ตั้งค่าเริ่มต้นจากข้อมูลเก่า (Edit Mode Support)
    var type by remember { mutableStateOf(buildingData.type ?: "") }
    var buildingName by remember { mutableStateOf(buildingData.buildingName ?: "") }
    var area by remember { mutableStateOf(buildingData.area.toString()) }
    var amount by remember { mutableStateOf(buildingData.amount) }
    var description by remember { mutableStateOf(buildingData.description ?: "") }
    var locationAddress by remember { mutableStateOf(buildingData.locationAddress ?: "") }
    var locationSubDistrict by remember { mutableStateOf(buildingData.locationSubDistrict ?: "") }
    var locationDistrict by remember { mutableStateOf(buildingData.locationDistrict ?: "") }
    var locationProvince by remember { mutableStateOf(buildingData.locationProvince ?: "") }
    var locationPostalCode by remember { mutableStateOf(buildingData.locationPostalCode ?: "") }

    // image state
    val originalAssets = remember { mutableStateListOf<Attachment>().apply { addAll(buildingData.attachments) } }
    val currentAssets = remember { mutableStateListOf<Attachment>().apply { addAll(buildingData.attachments) } }
    val filePicker = rememberFilePicker { newFiles -> currentAssets.addAll(newFiles) }

    // Building/Land ref state
    val originalBuilding = remember { mutableStateListOf<RefModel>().apply { addAll(buildingData.referenceIds) } }
    val currentBuilding = remember { mutableStateListOf<RefModel>().apply { addAll(buildingData.referenceIds) } }

    // insurance ref state
    val originalInsBuilding = remember { mutableStateListOf<InsRefModel>().apply { addAll(buildingData.insIds) } }
    val currentInsBuilding = remember { mutableStateListOf<InsRefModel>().apply { addAll(buildingData.insIds) } }

    var showLandSheet by remember { mutableStateOf(false) }
    var showInsSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = LightBg,
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
                    title = { Text("ข้อมูลอาคาร ตึก", color = LightPrimary, style = MaterialTheme.typography.titleLarge) },
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
                        val data = BuildingModel(
                            type = type,
                            buildingName = buildingName,
                            area = area.toDoubleOrNull() ?: 0.0,
                            amount = amount,
                            description = description,
                            attachments = currentAssets,
                            referenceIds = currentBuilding,
                            locationAddress = locationAddress,
                            locationSubDistrict = locationSubDistrict,
                            locationDistrict = locationDistrict,
                            locationProvince = locationProvince,
                            locationPostalCode = locationPostalCode,
                            insIds = currentInsBuilding
                        )

                        val addList = currentAssets.filter { it.id.isNullOrEmpty() }
                        val deleteList = originalAssets.filter { old -> currentAssets.none { it.id == old.id } }

                        val addRefList = currentBuilding.filter { curr -> originalBuilding.none { it.areaId == curr.areaId } }
                        val deleteRefList = originalBuilding.filter { old -> currentBuilding.none { it.areaId == old.areaId } }

                        val addInsList = currentInsBuilding.filter { curr -> originalInsBuilding.none { it.insId == curr.insId } }
                        val deleteInsList = originalInsBuilding.filter { old -> currentInsBuilding.none { it.insId == old.insId } }

                        onNextClick(data, addList, deleteList, addRefList, deleteRefList, addInsList, deleteInsList)
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("ยืนยันการแก้ไข", style = MaterialTheme.typography.titleMedium, color = Color.White)
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

            DropdownInput(
                label = "ประเภทที่อยู่อาศัย",
                options = buildingTypes,
                selectedValue = type,
                onValueChange = { type = it },
                placeholder = "กรุณาเลือกประเภท"
            )

            AssetTextField(value = buildingName, onValueChange = { buildingName = it }, label = "ชื่ออาคาร ตึก*", placeholder = "ระบุชื่ออาคาร")
            AssetTextField(value = area, onValueChange = { area = it }, label = "ขนาดพื้นที่ (ตร.ว.)*", placeholder = "0.0")

            // --- ส่วนของข้อมูลอ้างอิงที่ดิน ---
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("อ้างอิงข้อมูลที่ดิน", style = MaterialTheme.typography.bodyMedium, color = LightPrimary)
                IconButton(onClick = { showLandSheet = true }) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = LightPrimary)
                }
            }

            if (currentBuilding.isNotEmpty()) {
                currentBuilding.forEach { land ->
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
                            Text(land.areaName, style = MaterialTheme.typography.bodyLarge, color = LightText)
                            Text("โฉนด: ${land.areaId}", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                        }
                        // 🌟 แก้ BUG: ลบออกจาก currentBuilding เพื่อให้ UI อัปเดตทันที
                        IconButton(onClick = { currentBuilding.remove(land) }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red.copy(alpha = 0.6f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("ที่ตั้งอาคาร", style = MaterialTheme.typography.titleMedium, color = LightPrimary)

            AssetTextField(value = locationAddress, onValueChange = { locationAddress = it }, label = "ที่อยู่", placeholder = "บ้านเลขที่, ซอย, ถนน")
            AssetTextField(value = locationSubDistrict, onValueChange = { locationSubDistrict = it }, label = "ตำบล / แขวง", placeholder = "ระบุตำบล")
            AssetTextField(value = locationDistrict, onValueChange = { locationDistrict = it }, label = "อำเภอ / เขต", placeholder = "ระบุอำเภอ")
            AssetTextField(value = locationProvince, onValueChange = { locationProvince = it }, label = "จังหวัด", placeholder = "ระบุจังหวัด")
            AssetTextField(value = locationPostalCode, onValueChange = { locationPostalCode = it }, label = "รหัสไปรษณีย์", placeholder = "ระบุรหัสไปรษณีย์")

            // --- ส่วนของข้อมูลประกัน ---
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("อ้างอิงข้อมูลประกัน", style = MaterialTheme.typography.bodyMedium, color = LightPrimary)
                IconButton(onClick = { showInsSheet = true }) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = LightPrimary)
                }
            }

            if (currentInsBuilding.isNotEmpty()) {
                currentInsBuilding.forEach { ins ->
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
                            Text(ins.insName, style = MaterialTheme.typography.bodyLarge, color = LightText)
                            Text("เลขกรมธรรม์: ${ins.insId}", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                        }
                        // 🌟 แก้ BUG: ลบออกจาก currentInsBuilding เพื่อให้ UI อัปเดตทันที
                        IconButton(onClick = { currentInsBuilding.remove(ins) }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red.copy(alpha = 0.6f))
                        }
                    }
                }
            }

            AssetTextField(value = description, onValueChange = { description = it }, label = "รายละเอียดเพิ่มเติม", placeholder = "ระบุรายละเอียดเพิ่มเติม", isMultiLine = true)

            Spacer(modifier = Modifier.height(24.dp))

            ReferenceImagepicker(
                attachments = currentAssets,
                onAddImage = { filePicker.launchImage() },
                onAddPdf = { filePicker.launchPdf() },
                onRemove = { item -> currentAssets.remove(item) }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Bottom Sheets
            if (showLandSheet) {
                LandSelectionBottomSheet(
                    alreadySelected = currentBuilding,
                    onDismiss = { showLandSheet = false },
                    onConfirm = { newSelection ->
                        currentBuilding.clear()
                        currentBuilding.addAll(newSelection)
                        showLandSheet = false
                    },
                    landData = landData
                )
            }

            if (showInsSheet) {
                InsSelectionBottomSheet(
                    availableIns = insData,
                    alreadySelected = currentInsBuilding,
                    onDismiss = { showInsSheet = false },
                    onConfirm = { newSelection ->
                        currentInsBuilding.clear()
                        currentInsBuilding.addAll(newSelection)
                        showInsSheet = false
                    }
                )
            }
        }
    }
}

// 🌟 ปรับปรุง BottomSheet ให้ใช้ Theme เดียวกัน
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandSelectionBottomSheet(
    alreadySelected: List<RefModel>,
    onDismiss: () -> Unit,
    onConfirm: (List<RefModel>) -> Unit,
    landData: List<GetLandData>
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val lands = remember(landData) { landData.map { RefModel(areaName = it.name ?: "", areaId = it.id ?: "") } }
    val tempSelected = remember { mutableStateListOf<RefModel>().apply { addAll(alreadySelected) } }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = Color.White) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).navigationBarsPadding()) {
            Text("เลือกที่ดินอ้างอิง", style = MaterialTheme.typography.titleMedium, color = LightPrimary)
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.weight(1f, fill = false), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(lands) { land ->
                    val isChecked = tempSelected.any { it.areaId == land.areaId }
                    Surface(
                        onClick = {
                            if (isChecked) tempSelected.removeAll { it.areaId == land.areaId }
                            else tempSelected.add(land)
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isChecked) LightBg else Color.White,
                        border = BorderStroke(1.dp, if (isChecked) LightPrimary else LightBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(land.areaName, fontWeight = FontWeight.Medium)
                                Text("เลขโฉนด: ${land.areaId}", fontSize = 12.sp, color = Color.Gray)
                            }
                            Checkbox(checked = isChecked, onCheckedChange = null, colors = CheckboxDefaults.colors(checkedColor = LightPrimary))
                        }
                    }
                }
            }

            Button(
                onClick = { onConfirm(tempSelected.toList()) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp).height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LightPrimary)
            ) {
                Text("ตกลง (${tempSelected.size})", color = Color.White)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsSelectionBottomSheet(
    availableIns: List<GetInsuranceData>,
    alreadySelected: List<InsRefModel>,
    onDismiss: () -> Unit,
    onConfirm: (List<InsRefModel>) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val insDatas = remember(availableIns) { availableIns.map { InsRefModel(insName = it.name ?: "", insId = it.id ?: "") } }
    val tempInsSelected = remember { mutableStateListOf<InsRefModel>().apply { addAll(alreadySelected) } }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = Color.White) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).navigationBarsPadding()) {
            Text("เลือกประกันอ้างอิง", style = MaterialTheme.typography.titleMedium, color = LightPrimary)
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.weight(1f, fill = false), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(insDatas) { ins ->
                    val isChecked = tempInsSelected.any { it.insId == ins.insId }
                    Surface(
                        onClick = {
                            if (isChecked) tempInsSelected.removeAll { it.insId == ins.insId }
                            else tempInsSelected.add(ins)
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isChecked) LightBg else Color.White,
                        border = BorderStroke(1.dp, if (isChecked) LightPrimary else LightBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(ins.insName, fontWeight = FontWeight.Medium)
                                Text("เลขกรมธรรม์: ${ins.insId}", fontSize = 12.sp, color = Color.Gray)
                            }
                            Checkbox(checked = isChecked, onCheckedChange = null, colors = CheckboxDefaults.colors(checkedColor = LightPrimary))
                        }
                    }
                }
            }

            Button(
                onClick = { onConfirm(tempInsSelected.toList()) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp).height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LightPrimary)
            ) {
                Text("ตกลง (${tempInsSelected.size})", color = Color.White)
            }
        }
    }
}
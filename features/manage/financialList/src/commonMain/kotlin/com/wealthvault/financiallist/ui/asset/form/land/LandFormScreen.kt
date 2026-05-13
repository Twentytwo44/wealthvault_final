package com.wealthvault.financiallist.ui.asset.form.land

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
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
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
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault_final.`financial-asset`.Imagepicker.Attachment
import com.wealthvault_final.`financial-asset`.Imagepicker.rememberFilePicker
import com.wealthvault_final.`financial-asset`.model.LandModel
import com.wealthvault_final.`financial-asset`.model.RefModel
import com.wealthvault_final.`financial-asset`.ui.components.AssetTextField
import com.wealthvault_final.`financial-asset`.ui.components.ReferenceImagepicker
import org.jetbrains.compose.resources.painterResource
import androidx.lifecycle.compose.LocalLifecycleOwner


class LandFormScreen(val id: String, val landData: LandModel) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<LandScreenModel>()

        // 🌟 1. ดึง Lifecycle มาจัดการความสดใหม่ของข้อมูลอ้างอิง
        val lifecycleOwner = LocalLifecycleOwner.current

        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    println("🔄 LandForm ตื่นแล้ว! อัปเดตรายการสิ่งปลูกสร้างสำหรับอ้างอิง...")
                    screenModel.fetchData() // ดึงรายการสิ่งปลูกสร้างใหม่เสมอ
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
        }

        // 🌟 2. ตั้งค่าข้อมูลที่ส่งมา (Initial Data) เข้าสู่ Model เพียงครั้งเดียว
        LaunchedEffect(Unit) {
            screenModel.updateForm(landData)
        }

        val buildState by screenModel.BuildingState.collectAsState()

        LandInputForm(
            onBackClick = { navigator.pop() },
            onNextClick = { data, addedList, deletedList, addRef, deleteRef ->
                // 🌟 บันทึกการเปลี่ยนแปลงและไฟล์แนบ
                screenModel.updateForm(data)
                screenModel.updateAttachment(addedList, deletedList, addRef, deleteRef)

                screenModel.submitLand(id,
                    onSuccess = {
                        // ✅ เด้งกลับหน้าก่อนหน้าเมื่อบันทึกสำเร็จ
                        navigator.pop()
                    })
            },
            landData = landData, // ข้อมูลเดิมสำหรับแสดงผลตั้งต้น
            buildingData = buildState // รายการสิ่งปลูกสร้างที่ดึงมาใหม่
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandInputForm(
    onBackClick: () -> Unit = {},
    onNextClick: (LandModel, List<Attachment>, List<Attachment>, List<RefModel>, List<RefModel>) -> Unit,
    landData: LandModel,
    buildingData: List<GetBuildingData>
) {
    // 🌟 1. State เริ่มต้นจากข้อมูลเก่า (Edit Mode)
    var deedNum by remember { mutableStateOf(landData.deedNum ?: "") }
    var landName by remember { mutableStateOf(landData.landName ?: "") }
    var area by remember { mutableStateOf(landData.area?.toString() ?: "") }
    var amount by remember { mutableStateOf(landData.amount?.toString() ?: "") }
    var description by remember { mutableStateOf(landData.description ?: "") }
    var locationAddress by remember { mutableStateOf(landData.locationAddress ?: "") }
    var locationSubDistrict by remember { mutableStateOf(landData.locationSubDistrict ?: "") }
    var locationDistrict by remember { mutableStateOf(landData.locationDistrict ?: "") }
    var locationProvince by remember { mutableStateOf(landData.locationProvince ?: "") }
    var locationPostalCode by remember { mutableStateOf(landData.locationPostalCode ?: "") }

    var showBuildingsheet by remember { mutableStateOf(false) }

    // image state (Added/Deleted Logic)
    val originalAssets = remember { mutableStateListOf<Attachment>().apply { addAll(landData.attachments) } }
    val currentAssets = remember { mutableStateListOf<Attachment>().apply { addAll(landData.attachments) } }
    val filePicker = rememberFilePicker { newFiles -> currentAssets.addAll(newFiles) }

    // Building ref state (Added/Deleted Logic)
    val originalBuilding = remember { mutableStateListOf<RefModel>().apply { addAll(landData.referenceIds) } }
    val currentBuilding = remember { mutableStateListOf<RefModel>().apply { addAll(landData.referenceIds) } }

    val isFormValid = deedNum.isNotBlank() && landName.isNotBlank() && area.isNotBlank()

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
                        text = "แก้ไขข้อมูลโฉนดที่ดิน",
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
                            attachments = currentAssets.toList(),
                            referenceIds = currentBuilding.toList(),
                            locationAddress = locationAddress,
                            locationSubDistrict = locationSubDistrict,
                            locationDistrict = locationDistrict,
                            locationProvince = locationProvince,
                            locationPostalCode = locationPostalCode,
                        )

                        // 🌟 คำนวณความแตกต่างของข้อมูล (Diff)
                        val addList = currentAssets.filter { it.id.isNullOrEmpty() }
                        val deleteList = originalAssets.filter { old -> currentAssets.none { it.id == old.id } }

                        val addRefList = currentBuilding.filter { curr -> originalBuilding.none { it.areaId == curr.areaId } }
                        val deleteRefList = originalBuilding.filter { old -> currentBuilding.none { it.areaId == old.areaId } }

                        onNextClick(data, addList, deleteList, addRefList, deleteRefList)
                    },
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
                    shape = RoundedCornerShape(12.dp),
                    enabled = isFormValid
                ) {
                    Text("ยืนยันการแก้ไข", style = MaterialTheme.typography.bodyLarge, color = Color.White)
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
            // --- ข้อมูลทั่วไป ---
            AssetTextField(value = deedNum, onValueChange = { deedNum = it }, label = "เลขโฉนดที่ดิน*", placeholder = "ระบุเลขที่โฉนด")
            AssetTextField(value = landName, onValueChange = { landName = it }, label = "ชื่อโฉนด*", placeholder = "ระบุชื่อเรียกที่ดิน")

            CustomNumericField(
                value = area,
                onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) area = it },
                label = "ขนาดพื้นที่ (ตร.ว.)*",
                placeholder = "0.0"
            )

            CustomNumericField(
                value = amount,
                onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) amount = it },
                label = "มูลค่าประเมิน (บาท)",
                placeholder = "0.00"
            )


            AssetTextField(value = locationAddress, onValueChange = { locationAddress = it }, label = "ที่อยู่", placeholder = "ซอย, ถนน")
            AssetTextField(value = locationSubDistrict, onValueChange = { locationSubDistrict = it }, label = "ตำบล / แขวง", placeholder = "ระบุตำบล")
            AssetTextField(value = locationDistrict, onValueChange = { locationDistrict = it }, label = "อำเภอ / เขต", placeholder = "ระบุอำเภอ")
            AssetTextField(value = locationProvince, onValueChange = { locationProvince = it }, label = "จังหวัด", placeholder = "ระบุจังหวัด")

            CustomNumericField(
                value = locationPostalCode,
                onValueChange = { if (it.isEmpty() || it.all { char -> char.isDigit() }) locationPostalCode = it },
                label = "รหัสไปรษณีย์",
                placeholder = "ระบุรหัสไปรษณีย์"
            )

            // --- ส่วนอ้างอิงอาคาร (Master UI Style) ---
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                ReferenceHeader(title = "อาคาร / สิ่งปลูกสร้างบนที่ดินนี้", onAddClick = { showBuildingsheet = true })
                Spacer(modifier = Modifier.height(8.dp))

                if (currentBuilding.isEmpty()) {
                    EmptyReferenceBox(onClick = { showBuildingsheet = true })
                } else {
                    currentBuilding.forEach { build ->
                        ReferenceItem(name = build.areaName) { currentBuilding.remove(build) }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }


            AssetTextField(value = description, onValueChange = { description = it }, label = "รายละเอียดเพิ่มเติม", placeholder = "ระบุรายละเอียดเพิ่มเติม", isMultiLine = true)

            Spacer(modifier = Modifier.height(8.dp))

            ReferenceImagepicker(
                attachments = currentAssets,
                onAddImage = { filePicker.launchImage() },
                onAddPdf = { filePicker.launchPdf() },
                onRemove = { item -> currentAssets.remove(item) }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }

        if (showBuildingsheet) {
            BuildingSelectionBottomSheet(
                alreadySelected = currentBuilding,
                onDismiss = { showBuildingsheet = false },
                onConfirm = { newSelection ->
                    currentBuilding.clear()
                    currentBuilding.addAll(newSelection)
                    showBuildingsheet = false
                },
                BuildingData = buildingData
            )
        }
    }
}

// 🌟 Component ย่อยมาตรฐาน Master UI (BasicTextField)
@Composable
private fun CustomNumericField(value: String, onValueChange: (String) -> Unit, label: String, placeholder: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = LightPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                        if (value.isEmpty()) Text(placeholder, color = Color.LightGray, style = MaterialTheme.typography.bodyLarge)
                        innerTextField()
                    }
                }
            }
        )
    }
}

@Composable
private fun ReferenceHeader(title: String, onAddClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, style = MaterialTheme.typography.bodyMedium, color = LightPrimary)
        Icon(painterResource(Res.drawable.ic_common_plus), null, tint = LightPrimary, modifier = Modifier.size(24.dp).clickable { onAddClick() })
    }
}

@Composable
private fun EmptyReferenceBox(onClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().height(44.dp).background(LightSoftWhite, RoundedCornerShape(12.dp))
            .border(1.dp, LightBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp)).clickable { onClick() },
        contentAlignment = Alignment.CenterStart
    ) {
        Text("ยังไม่มีรายการ", color = Color.LightGray, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(horizontal = 16.dp))
    }
}

@Composable
private fun ReferenceItem(name: String, onRemove: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().height(44.dp).background(LightSoftWhite, RoundedCornerShape(12.dp))
            .border(1.dp, LightBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp)).padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
    ) {
        Text(name, style = MaterialTheme.typography.bodyLarge, color = LightText, modifier = Modifier.weight(1f))
        Icon(painterResource(Res.drawable.ic_common_bin), null, tint = Color(0xFFDC4A3C).copy(alpha = 0.6f), modifier = Modifier.size(20.dp).clickable { onRemove() })
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
    val buildings = remember(BuildingData) { BuildingData.map { RefModel(areaName = it.name?:"", areaId = it.id?:"") } }
    val tempSelected = remember { mutableStateListOf<RefModel>().apply { addAll(alreadySelected) } }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = Color.White) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).navigationBarsPadding()) {
            Text("เลือกอาคาร/ตึกอ้างอิง", style = MaterialTheme.typography.titleMedium, color = LightPrimary)
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

            Button(
                onClick = { onConfirm(tempSelected.toList()) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp).height(46.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LightPrimary)
            ) {
                Text("ตกลง (${tempSelected.size})", color = Color.White)
            }
        }
    }
}

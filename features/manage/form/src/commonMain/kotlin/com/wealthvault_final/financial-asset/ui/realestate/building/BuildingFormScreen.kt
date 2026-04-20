package com.wealthvault_final.`financial-asset`.ui.realestate.building

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.wealthvault_final.`financial-asset`.ui.realestate.building.viewmodel.BuildingScreenModel
import com.wealthvault_final.`financial-asset`.ui.share.ShareAssetScreen


class BuildingFormScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
//        val screenModel = rememberScreenModel { BuildingScreenModel() }
        val screenModel = getScreenModel<BuildingScreenModel>()
        val landState by screenModel.LandState.collectAsState()
        val insState by screenModel.InsState.collectAsState()

        BuildingInputForm(
            onBackClick = { navigator.pop() } ,
            onNextClick = { data ->
                println("data asset input: ${data.attachments}")
                // 1. อัปเดตข้อมูลที่รับมาจาก Form เข้าไปใน Model ก่อน
                screenModel.updateForm(data)
                // 2. 🚩 แก้จุดนี้: แปลง Model ให้เป็น Request ก่อนส่ง
//                val requestForSummary = screenModel.asRequest()
                // 3. ส่ง "ก้อนข้อมูล" ไปแทนการส่ง "Model"
                navigator.push(ShareAssetScreen(
                    request = screenModel.state.value
                ))

            },
            landData = landState,
            insData = insState
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildingInputForm(
    onBackClick: () -> Unit = {},
    onNextClick: (BuildingModel) -> Unit,
    insData: List<GetInsuranceData>,
    landData: List<GetLandData>
) {

    var type by remember { mutableStateOf("") }
    var buildingName by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf(0.0) }
    var description by remember { mutableStateOf("") }
    var locationAddress by remember { mutableStateOf("") }
    var locationSubDistrict by remember { mutableStateOf("") }
    var locationDistrict by remember { mutableStateOf("") }
    var locationProvince by remember { mutableStateOf("") }
    var locationPostalCode by remember { mutableStateOf("") }
    val insIds = remember { mutableStateListOf<InsRefModel>() }
    val attachments = remember { mutableStateListOf<Attachment>() }
    val referenceIds = remember { mutableStateListOf<RefModel>() }


    val filePicker = rememberFilePicker { newFiles ->
        attachments.addAll(newFiles)
    }

    var showLandSheet by remember { mutableStateOf(false) }
    var showInsSheet by remember { mutableStateOf(false) }



    Scaffold(
        containerColor = Color(0xFFFFF8F3),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                ),
                title = {
                    Text(
                        "ข้อมูลอาคาร ตึก",
                        color = Color(0xFF8D6E63),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color(0xFF8D6E63))
                    }
                }
            )
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
                onValueChange = { type = it }
            )
            AssetTextField(
                value = buildingName,
                onValueChange = { buildingName = it },
                label = "ชื่ออาคาร ตึก",
                placeholder = "ชื่ออาคาร ตึก"
            )
            AssetTextField(
                value = area,
                onValueChange = { area = it }, // ✅ แก้บั๊กให้เป็น area = it
                label = "ขนาดพื้นที่",
                placeholder = "ขนาดพื้นที่ (เช่น ตร.ม., ตร.ว.)"
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "เพิ่มข้อมูลอ้างอิง",
                    fontSize = 16.sp,
                    color = Color.DarkGray
                )
                IconButton(onClick = { showLandSheet = true }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Reference",
                        tint = Color.DarkGray
                    )
                }
            }

            // 🔹 แสดงรายการที่ถูกเลือกแล้ว บนหน้าจอหลัก
            if (referenceIds.isNotEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    referenceIds.forEach { land ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFFF8F3), RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFFF0DFD3), RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(land.areaName, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                Text("โฉนด: ${land.areaId}", fontSize = 12.sp, color = Color.Gray)
                            }
                            // ปุ่มลบออกจากรายการที่เลือก
                            IconButton(
                                onClick = { referenceIds.removeAll { it.areaId == land.areaId } },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red.copy(alpha = 0.6f))
                            }
                        }
                    }
                }
            }

            AssetTextField(
                value = locationAddress,
                onValueChange = { locationAddress = it },
                label = "ที่อยู่ (บ้านเลขที่, ซอย, ถนน)",
                placeholder = "ที่อยู่ (บ้านเลขที่, ซอย, ถนน)"
            )
            AssetTextField(
                value = locationSubDistrict,
                onValueChange = { locationSubDistrict = it },
                label = "ตำบล / แขวง",
                placeholder = "ตำบล / แขวง"
            )
            AssetTextField(
                value = locationDistrict,
                onValueChange = { locationDistrict = it },
                label = "อำเภอ / เขต",
                placeholder = "อำเภอ / เขต"
            )
            AssetTextField(
                value = locationProvince,
                onValueChange = { locationProvince = it },
                label = "จังหวัด",
                placeholder = "จังหวัด"
            )
            AssetTextField(
                value = locationPostalCode,
                onValueChange = { locationPostalCode = it },
                label = "รหัสไปรษณีย์",
                placeholder = "รหัสไปรษณีย์"
            )


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "เพิ่มข้อมูลประกัน",
                    fontSize = 16.sp,
                    color = Color.DarkGray
                )
                IconButton(onClick = { showInsSheet = true }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Insurace",
                        tint = Color.DarkGray
                    )
                }
            }

            // 🔹 แสดงรายการที่ถูกเลือกแล้ว บนหน้าจอหลัก
            if (insIds.isNotEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    insIds.forEach { ins ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFFF8F3), RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFFF0DFD3), RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(ins.insName, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                Text("เลขประกัน: ${ins.insId}", fontSize = 12.sp, color = Color.Gray)
                            }
                            // ปุ่มลบออกจากรายการที่เลือก
                            IconButton(
                                onClick = { insIds.removeAll { it.insId == ins.insId } },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red.copy(alpha = 0.6f))
                            }
                        }
                    }
                }
            }

            AssetTextField(
                value = description,
                onValueChange = { description = it },
                label = "รายละเอียดเพิ่มเติม",
                placeholder = "รายละเอียดเพิ่มเติม"
            )
            Spacer(modifier = Modifier.height(24.dp))

            // 3. ส่ง State และคำสั่ง Launch เข้าไปใน ReferenceSection
            ReferenceImagepicker(
                attachments = attachments,
                onAddImage = { filePicker.launchImage() },
                onAddPdf = { filePicker.launchPdf() },
                onRemove = { item -> attachments.remove(item) }
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(

                onClick = {
                    val data = BuildingModel(
                        type = type,
                        buildingName = buildingName,
                        area = area.toDoubleOrNull() ?: 0.0,
                        amount = amount.toString().toDoubleOrNull() ?: 0.0,
                        description = description,
                        attachments = attachments,
                        referenceIds = referenceIds,
                        locationAddress = locationAddress,
                        locationSubDistrict = locationSubDistrict,
                        locationDistrict = locationDistrict,
                        locationProvince = locationProvince,
                        locationPostalCode = locationPostalCode,
                        insIds = insIds
                    )
                    println("data attachemnt: ${data.attachments}")
                    onNextClick(data)

                }, // อนาคตสามารถโยนค่า attachments ไปให้ ViewModel ตรงนี้ได้เลย
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFB08968)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("ต่อไป", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (showLandSheet) {
                LandSelectionBottomSheet(
                    alreadySelected = referenceIds, // โยนข้อมูลที่เคยเลือกแล้วเข้าไป
                    onDismiss = { showLandSheet = false },
                    onConfirm = { newSelection ->
                        // อัปเดต List ใหม่เมื่อกดยืนยัน
                        referenceIds.clear()
                        referenceIds.addAll(newSelection)
                        showLandSheet = false
                    },
                    LandData = landData
                )
            }

            if (showInsSheet) {
                InsSelectionBottomSheet(
                    availableIns = insData, // โยนข้อมูลจาก API เข้าไป
                    alreadySelected = insIds, // โยนข้อมูลที่เคยเลือกแล้วเข้าไป
                    onDismiss = { showInsSheet = false },
                    onConfirm = { newSelection ->
                        // อัปเดต List ใหม่เมื่อกดยืนยัน
                        insIds.clear()
                        insIds.addAll(newSelection)
                        showInsSheet = false
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandSelectionBottomSheet(
    alreadySelected: List<RefModel>,
    onDismiss: () -> Unit,
    onConfirm: (List<RefModel>) -> Unit,
    LandData: List<GetLandData>
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val Lands = remember(LandData) {
        LandData.map { RefModel(areaName = it.name ?: "", areaId = it.id?: "") }
    }

    // สร้าง List ชั่วคราวไว้จัดการตอนติ๊ก Checkbox (ยังไม่เซฟจนกว่าจะกดยืนยัน)
    val tempSelected = remember {
        mutableStateListOf<RefModel>().apply { addAll(alreadySelected) }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .navigationBarsPadding() // เว้นที่ขอบล่างจอ
        ) {
            Text(
                text = "เลือกข้อมูลที่ดินอ้างอิง",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFC08064)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // ลิสต์รายการที่ดิน
            LazyColumn(
                modifier = Modifier.weight(1f, fill = false),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(Lands) { land ->
                    val isChecked = tempSelected.any { it.areaId == land.areaId }

                    Surface(
                        onClick = {
                            if (isChecked) tempSelected.removeAll { it.areaId == land.areaId }
                            else tempSelected.add(land)
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isChecked) Color(0xFFF2E8E1) else Color.White,
                        border = BorderStroke(1.dp, if (isChecked) Color(0xFFC08064) else Color(0xFFEEEEEE)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(land.areaName, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                                Text("เลขที่โฉนด: ${land.areaId}", fontSize = 14.sp, color = Color.Gray)
                            }
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = null, // ให้ Surface จัดการเรื่องกดแทน
                                colors = CheckboxDefaults.colors(checkedColor = Color(0xFFC08064))
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ปุ่มยืนยัน
            Button(
                onClick = { onConfirm(tempSelected.toList()) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC08064))
            ) {
                Text("ยืนยัน (${tempSelected.size}) รายการ", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsSelectionBottomSheet(
    availableIns: List<GetInsuranceData>,   // 💡 เปลี่ยนชื่อจาก availableLands
    alreadySelected: List<InsRefModel>,
    onDismiss: () -> Unit,
    onConfirm: (List<InsRefModel>) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val tempInsSelected = remember {
        mutableStateListOf<InsRefModel>().apply { addAll(alreadySelected) }
    }

    val insDatas = remember(availableIns) {
        availableIns.map { InsRefModel(insName = it.name?: "", insId = it.id?: "") }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .navigationBarsPadding()
        ) {
            Text(
                text = "เลือกข้อมูลประกันอ้างอิง", // 💡 แก้จาก "ที่ดิน" เป็น "ประกัน"
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFC08064)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // ลิสต์รายการประกัน
            LazyColumn(
                modifier = Modifier.weight(1f, fill = false),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(insDatas) { ins -> // 💡 เปลี่ยนชื่อตัวแปรรันลูปจาก land เป็น ins
                    val isChecked = tempInsSelected.any { it.insId == ins.insId }

                    Surface(
                        onClick = {
                            if (isChecked) tempInsSelected.removeAll { it.insId == ins.insId }
                            else tempInsSelected.add(ins)
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isChecked) Color(0xFFF2E8E1) else Color.White,
                        border = BorderStroke(1.dp, if (isChecked) Color(0xFFC08064) else Color(0xFFEEEEEE)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                // 💡 สลับเอาชื่อ (insName) ขึ้นเป็นหัวข้อหลัก และเอา ID ไปไว้เป็นตัวรอง
                                Text(ins.insName, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                                Text("เลขกรมธรรม์: ${ins.insId}", fontSize = 14.sp, color = Color.Gray)
                            }
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = null,
                                colors = CheckboxDefaults.colors(checkedColor = Color(0xFFC08064))
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ปุ่มยืนยัน
            Button(
                onClick = { onConfirm(tempInsSelected.toList()) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC08064))
            ) {
                Text("ยืนยัน (${tempInsSelected.size}) รายการ", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}

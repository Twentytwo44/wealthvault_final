package com.wealthvault.financiallist.ui.asset.form.land

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.building_api.model.GetBuildingData
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault_final.`financial-asset`.Imagepicker.Attachment
import com.wealthvault_final.`financial-asset`.Imagepicker.rememberFilePicker
import com.wealthvault_final.`financial-asset`.model.LandModel
import com.wealthvault_final.`financial-asset`.model.RefModel
import com.wealthvault_final.`financial-asset`.ui.components.AssetTextField
import com.wealthvault_final.`financial-asset`.ui.components.ReferenceImagepicker


class LandFormScreen(val id:String,val landData: LandModel) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<LandScreenModel>()
        LaunchedEffect(Unit) {
            screenModel.fetchData()

        }
        val buildState by screenModel.BuildingState.collectAsState()

        LandInputForm(
            onBackClick = { navigator.pop() } ,
            onNextClick = { data,addedList,deletedList,addRef,deleteRef ->
                println("data asset input: ${data.attachments}")
                screenModel.updateForm(data)
                screenModel.updateAttachment(addedList,deletedList,addRef,deleteRef)
                screenModel.submitLand(id)
            },

            landData = landData,
            buildingData = buildState
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandInputForm(
    onBackClick: () -> Unit = {},
    onNextClick: (LandModel,List<Attachment>,List<Attachment>,List<RefModel>,List<RefModel>) -> Unit,
    landData: LandModel,
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

    var showBuildingsheet by remember { mutableStateOf(false) }

    val originalAssets = remember {
        mutableStateListOf<Attachment>().apply {
            addAll(landData.attachments)
        }
    }

    val currentAssets = remember {
        mutableStateListOf<Attachment>().apply {
            addAll(landData.attachments)
        }
    }
    val attachments = remember { mutableStateListOf<Attachment>() }

    val filePicker = rememberFilePicker { newFiles ->
        currentAssets.addAll(newFiles)
    }
    // Building refids
    val referenceIds = remember { mutableStateListOf<RefModel>() }
    val originalBuilding = remember {
        mutableStateListOf<RefModel>().apply {
            addAll(landData.referenceIds )
        }
    }

    val currentBuilding = remember {
        mutableStateListOf<RefModel>().apply {
            addAll(landData.referenceIds )
        }
    }




    Scaffold(
        containerColor = Color(0xFFFFF8F3),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                ),
                title = {
                    Text(
                        "ข้อมูลโฉนดที่ดิน",
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


            AssetTextField(
                value = deedNum,
                onValueChange = { deedNum = it },
                label = "เลขโฉนดที่ดิน",
                placeholder = landData.deedNum ?: "" // ✅ แก้ไข Placeholder
            )
            AssetTextField(
                value = landName,
                onValueChange = { landName = it },
                label = "ชื่อโฉนด",
                placeholder = landData.landName ?: "" // ✅ แก้ไข Placeholder
            )

            AssetTextField(
                value = area,
                onValueChange = { newValue ->
                    // อนุญาตให้มีจุดทศนิยมได้ 1 จุด เผื่อมีเศษตารางวา
                    if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                        area = newValue
                    }
                },
                label = "ขนาดพื้นที่",
                placeholder = landData.area?.toString() ?: "", // ✅ แก้ไข Placeholder
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // ==========================================
            // 💡 ส่วนของการเพิ่มข้อมูลอ้างอิง (คงไว้เหมือนเดิม 100%)
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
                IconButton(onClick = { showBuildingsheet = true }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Reference",
                        tint = Color.DarkGray
                    )
                }
            }

            if (currentBuilding.isNotEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    currentBuilding.forEach { land ->
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
                                Text("อาคาร: ${land.areaId}", fontSize = 12.sp, color = Color.Gray)
                            }
                            IconButton(
                                onClick = { currentBuilding.removeAll { it.areaId == land.areaId } },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red.copy(alpha = 0.6f))
                            }
                        }
                    }
                }
            }
            // ==========================================

            AssetTextField(
                value = locationAddress,
                onValueChange = { locationAddress = it },
                label = "ที่อยู่ (บ้านเลขที่, ซอย, ถนน)",
                placeholder = landData.locationAddress ?: "" // ✅ แก้ไข Placeholder
            )
            AssetTextField(
                value = locationSubDistrict,
                onValueChange = { locationSubDistrict = it },
                label = "ตำบล / แขวง",
                placeholder = landData.locationSubDistrict ?: "" // ✅ แก้ไข Placeholder
            )
            AssetTextField(
                value = locationDistrict,
                onValueChange = { locationDistrict = it },
                label = "อำเภอ / เขต",
                placeholder = landData.locationDistrict ?: "" // ✅ แก้ไข Placeholder
            )
            AssetTextField(
                value = locationProvince,
                onValueChange = { locationProvince = it },
                label = "จังหวัด",
                placeholder = landData.locationProvince ?: "" // ✅ แก้ไข Placeholder
            )

            AssetTextField(
                value = locationPostalCode,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                        locationPostalCode = newValue
                    }
                },
                label = "รหัสไปรษณีย์",
                placeholder = landData.locationPostalCode ?: "", // ✅ แก้ไข Placeholder
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            AssetTextField(
                value = description,
                onValueChange = { description = it },
                label = "รายละเอียดเพิ่มเติม",
                placeholder = landData.description ?: "", // ✅ แก้ไข Placeholder
                isMultiLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))


            // 3. ส่ง State และคำสั่ง Launch เข้าไปใน ReferenceSection
            ReferenceImagepicker(
                attachments = currentAssets, // ใช้ตัวแปรตัวที่ 2 (image2)
                onAddImage = { filePicker.launchImage() },
                onAddPdf = { filePicker.launchPdf() },
                onRemove = { item -> currentAssets.remove(item) } // ลบออกจากรายการที่โชว์
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(

                onClick = {
                    val data = LandModel(
                        deedNum = deedNum,
                        landName = landName,

                        // 🚨 อัปเกรด: ป้องกันแอปแครชถ้าช่องนี้เป็นค่าว่าง
                        area = area.toDoubleOrNull() ?: 0.0,
                        amount = amount.toDoubleOrNull() ?: 0.0,

                        description = description,
                        attachments = attachments,
                        referenceIds = referenceIds,
                        locationAddress = locationAddress,
                        locationSubDistrict = locationSubDistrict,
                        locationDistrict = locationDistrict,
                        locationProvince = locationProvince,
                        locationPostalCode = locationPostalCode,
                    )
                    val addList = currentAssets.filter { it.id.isNullOrEmpty() }

                    val deleteList = originalAssets.filter { originalItem ->
                        currentAssets.none { it.id == originalItem.id }
                    }

                    val addRefList = currentBuilding.filter { it.areaId.isNotEmpty() }

                    val deleteRefList = originalBuilding.filter { originalItem ->
                        currentBuilding.none { it.areaId == originalItem.areaId }
                    }

                    println("Added List Size: ${addList.size} รายการ")
                    println("Delete List Size: ${deleteList.size} รายการ")
                    println("Delete ID: ${deleteList.map { it.id }}")
                    println("Data Attachment: ${data.attachments}")
                    onNextClick(data,addList,deleteList,addRefList,deleteRefList)

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
        }

        if (showBuildingsheet) {
            BuildingSelectionBottomSheet(
                alreadySelected = referenceIds, // โยนข้อมูลที่เคยเลือกแล้วเข้าไป
                onDismiss = { showBuildingsheet = false },
                onConfirm = { newSelection ->
                    // อัปเดต List ใหม่เมื่อกดยืนยัน
                    currentBuilding.addAll(newSelection)
                    showBuildingsheet = false
                },
                BuildingData = buildingData
            )
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

    val Buildings = remember(BuildingData) {
        BuildingData.map { RefModel(areaName = it.name, areaId = it.id) }
    }
    println("----------------------------------------")
    println("BuildingData: $BuildingData")
    println("----------------------------------------")

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
                text = "เลือกข้อมูลอาคาร ตึก",
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
                items(Buildings) { land ->
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
                                Text("อาคาร: ${land.areaId}", fontSize = 14.sp, color = Color.Gray)
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


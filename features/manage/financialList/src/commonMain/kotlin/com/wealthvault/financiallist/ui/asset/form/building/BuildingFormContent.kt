package com.wealthvault.financiallist.ui.asset.form.building

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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
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
import com.wealthvault.domain.portfolio.GetInsuranceData
import com.wealthvault.domain.portfolio.GetLandData
import com.wealthvault.core.model.Attachment
import com.wealthvault.financiallist.ui.form.*
import com.wealthvault.domain.portfolio.BuildingModel
import com.wealthvault.core.model.Money
import com.wealthvault.domain.portfolio.InsRefModel
import com.wealthvault.domain.portfolio.RefModel
import com.wealthvault.financiallist.ui.form.FormErrorBanner
import com.wealthvault.financiallist.ui.form.formErrorMessage
import org.jetbrains.compose.resources.painterResource

@Composable
fun BuildingInputForm(
    onBackClick: () -> Unit = {},
    onNextClick: (BuildingModel, List<Attachment>, List<Attachment>, List<RefModel>, List<RefModel>, List<InsRefModel>, List<InsRefModel>) -> Unit,
    insData: List<GetInsuranceData>,
    landData: List<GetLandData>,
    buildingData: BuildingModel,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    title: String = "แก้ไขข้อมูลอาคาร ตึก",
    submitLabel: String = "ยืนยันการแก้ไข",
) {
    // 🌟 1. State เริ่มต้นจากข้อมูลเก่า (Edit Mode Support)
    var type by remember { mutableStateOf(buildingData.type) }
    var buildingName by remember { mutableStateOf(buildingData.buildingName) }
    var area by remember { mutableStateOf(buildingData.area.toString()) }
    var amount by remember { mutableStateOf(buildingData.amount.decimalString()) }
    var description by remember { mutableStateOf(buildingData.description) }
    var locationAddress by remember { mutableStateOf(buildingData.locationAddress) }
    var locationSubDistrict by remember { mutableStateOf(buildingData.locationSubDistrict) }
    var locationDistrict by remember { mutableStateOf(buildingData.locationDistrict) }
    var locationProvince by remember { mutableStateOf(buildingData.locationProvince) }
    var locationPostalCode by remember { mutableStateOf(buildingData.locationPostalCode) }

    var showLandSheet by remember { mutableStateOf(false) }
    var showInsSheet by remember { mutableStateOf(false) }

    // image diff state
    val originalAssets = remember { mutableStateListOf<Attachment>().apply { addAll(buildingData.attachments) } }
    val currentAssets = remember { mutableStateListOf<Attachment>().apply { addAll(buildingData.attachments) } }
    val filePicker = rememberFilePicker { newFiles -> currentAssets.addAll(newFiles) }

    // Land ref diff state
    val originalBuilding = remember { mutableStateListOf<RefModel>().apply { addAll(buildingData.referenceIds) } }
    val currentBuilding = remember { mutableStateListOf<RefModel>().apply { addAll(buildingData.referenceIds) } }

    // Insurance ref diff state
    val originalInsBuilding = remember { mutableStateListOf<InsRefModel>().apply { addAll(buildingData.insIds) } }
    val currentInsBuilding = remember { mutableStateListOf<InsRefModel>().apply { addAll(buildingData.insIds) } }

    val isFormValid = type.isNotBlank() && buildingName.isNotBlank() && area.isNotBlank()

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
                    Text(text = title, style = MaterialTheme.typography.titleLarge, color = LightPrimary)
                }
            }
        },
        bottomBar = {
            Box(modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(24.dp)) {
                Button(
                    onClick = {
                        val data = BuildingModel(
                            type = type, buildingName = buildingName,
                            area = area.toDoubleOrNull() ?: 0.0, amount = Money.fromDecimal(amount) ?: Money(0),
                            description = description, attachments = currentAssets.toList(),
                            referenceIds = currentBuilding.toList(), locationAddress = locationAddress,
                            locationSubDistrict = locationSubDistrict, locationDistrict = locationDistrict,
                            locationProvince = locationProvince, locationPostalCode = locationPostalCode,
                            insIds = currentInsBuilding.toList()
                        )

                        // 🌟 คำนวณ Diff เพื่อส่งให้ API
                        val addList = currentAssets.filter { it.id.isNullOrEmpty() }
                        val deleteList = originalAssets.filter { old -> currentAssets.none { it.id == old.id } }
                        val addRefList = currentBuilding.filter { curr -> originalBuilding.none { it.areaId == curr.areaId } }
                        val deleteRefList = originalBuilding.filter { old -> currentBuilding.none { it.areaId == old.areaId } }
                        val addInsList = currentInsBuilding.filter { curr -> originalInsBuilding.none { it.insId == curr.insId } }
                        val deleteInsList = originalInsBuilding.filter { old -> currentInsBuilding.none { it.insId == old.insId } }

                        onNextClick(data, addList, deleteList, addRefList, deleteRefList, addInsList, deleteInsList)
                    },
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
                    shape = RoundedCornerShape(12.dp),
                    enabled = isFormValid && !isLoading
                ) {
                    Text(if (isLoading) "กำลังบันทึก..." else submitLabel, style = MaterialTheme.typography.bodyLarge, color = Color.White)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 24.dp).verticalScroll(rememberScrollState())
        ) {
            FormErrorBanner(errorMessage)
            // --- ข้อมูลทั่วไป ---
            DropdownInput(
                label = "ประเภทที่อยู่อาศัย", options = buildingTypes,
                selectedValue = type, onValueChange = { type = it }, placeholder = "กรุณาเลือกประเภท"
            )

            AssetTextField(value = buildingName, onValueChange = { buildingName = it }, label = "ชื่ออาคาร ตึก*", placeholder = "ระบุชื่ออาคาร")

            CustomNumericField(
                value = area, onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) area = it },
                label = "ขนาดพื้นที่ (ตร.ม.)*", placeholder = "0.0"
            )

            CustomNumericField(
                value = amount, onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) amount = it },
                label = "มูลค่าประมาณการ (บาท)", placeholder = "0.00"
            )


            AssetTextField(value = locationAddress, onValueChange = { locationAddress = it }, label = "ที่อยู่", placeholder = "บ้านเลขที่, ซอย, ถนน")
            AssetTextField(value = locationSubDistrict, onValueChange = { locationSubDistrict = it }, label = "ตำบล / แขวง", placeholder = "ระบุตำบล")
            AssetTextField(value = locationDistrict, onValueChange = { locationDistrict = it }, label = "อำเภอ / เขต", placeholder = "ระบุอำเภอ")
            AssetTextField(value = locationProvince, onValueChange = { locationProvince = it }, label = "จังหวัด", placeholder = "ระบุจังหวัด")

            CustomNumericField(
                value = locationPostalCode,
                onValueChange = { if (it.isEmpty() || it.all { char -> char.isDigit() }) locationPostalCode = it },
                label = "รหัสไปรษณีย์", placeholder = "ระบุรหัสไปรษณีย์"
            )

            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                BuildingReferenceHeader(title = "ที่ดินอ้างอิง (โฉนด)", onAddClick = { showLandSheet = true })
                Spacer(modifier = Modifier.height(8.dp))
                if (currentBuilding.isEmpty()) {
                    BuildingEmptyReferenceBox(text = "ยังไม่มีรายการที่ดิน", onClick = { showLandSheet = true })
                } else {
                    currentBuilding.forEach { land ->
                        BuildingReferenceItem(name = land.areaName, subText = "โฉนด: ${land.areaId}") { currentBuilding.remove(land) }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            // --- ส่วนอ้างอิงประกัน ---
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                BuildingReferenceHeader(title = "ประกันภัยอ้างอิง", onAddClick = { showInsSheet = true })
                Spacer(modifier = Modifier.height(8.dp))
                if (currentInsBuilding.isEmpty()) {
                    BuildingEmptyReferenceBox(text = "ยังไม่มีรายการประกัน", onClick = { showInsSheet = true })
                } else {
                    currentInsBuilding.forEach { ins ->
                        BuildingReferenceItem(name = ins.insName, subText = "กรมธรรม์: ${ins.insId}") { currentInsBuilding.remove(ins) }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            AssetTextField(value = description, onValueChange = { description = it }, label = "รายละเอียดเพิ่มเติม", placeholder = "ระบุรายละเอียดเพิ่มเติม", isMultiLine = true)

            Spacer(modifier = Modifier.height(8.dp))
            ReferenceImagepicker(
                attachments = currentAssets, onAddImage = { filePicker.launchImage() },
                onAddPdf = { filePicker.launchPdf() }, onRemove = { item -> currentAssets.remove(item) }
            )
            Spacer(modifier = Modifier.height(32.dp))
        }

        // --- Bottom Sheets ---
        if (showLandSheet) {
            LandSelectionBottomSheet(
                alreadySelected = currentBuilding, onDismiss = { showLandSheet = false },
                onConfirm = { new -> currentBuilding.clear(); currentBuilding.addAll(new); showLandSheet = false },
                landData = landData
            )
        }
        if (showInsSheet) {
            InsSelectionBottomSheet(
                availableIns = insData, alreadySelected = currentInsBuilding, onDismiss = { showInsSheet = false },
                onConfirm = { new -> currentInsBuilding.clear(); currentInsBuilding.addAll(new); showInsSheet = false }
            )
        }
    }
}

// 🌟 Common Master UI Components
@Composable
private fun CustomNumericField(value: String, onValueChange: (String) -> Unit, label: String, placeholder: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = LightPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        BasicTextField(
            value = value, onValueChange = onValueChange, singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF3A2F2A)),
            cursorBrush = SolidColor(LightPrimary),
            modifier = Modifier.fillMaxWidth().height(44.dp),
            decorationBox = { inner ->
                Row(
                    modifier = Modifier.fillMaxSize().background(LightSoftWhite, RoundedCornerShape(12.dp))
                        .border(1.dp, LightBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp)).padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        if (value.isEmpty()) Text(placeholder, color = Color.LightGray, style = MaterialTheme.typography.bodyLarge)
                        inner()
                    }
                }
            }
        )
    }
}

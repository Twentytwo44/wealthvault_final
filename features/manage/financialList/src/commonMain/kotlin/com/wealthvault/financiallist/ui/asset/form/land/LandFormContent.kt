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
import com.wealthvault.domain.portfolio.GetBuildingData
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
import com.wealthvault.core.model.Attachment
import com.wealthvault.financiallist.ui.form.*
import com.wealthvault.domain.portfolio.LandModel
import com.wealthvault.core.model.Money
import com.wealthvault.domain.portfolio.RefModel
import com.wealthvault.financiallist.ui.form.FormErrorBanner
import com.wealthvault.financiallist.ui.form.formErrorMessage
import org.jetbrains.compose.resources.painterResource

@Composable
fun LandInputForm(
    onBackClick: () -> Unit = {},
    onNextClick: (LandModel, List<Attachment>, List<Attachment>, List<RefModel>, List<RefModel>) -> Unit,
    landData: LandModel,
    buildingData: List<GetBuildingData>,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    title: String = "แก้ไขข้อมูลโฉนดที่ดิน",
    submitLabel: String = "ยืนยันการแก้ไข",
) {
    // 🌟 1. State เริ่มต้นจากข้อมูลเก่า (Edit Mode)
    var deedNum by remember { mutableStateOf(landData.deedNum) }
    var landName by remember { mutableStateOf(landData.landName) }
    var area by remember { mutableStateOf(landData.area.toString()) }
    var amount by remember { mutableStateOf(landData.amount.decimalString()) }
    var description by remember { mutableStateOf(landData.description) }
    var locationAddress by remember { mutableStateOf(landData.locationAddress) }
    var locationSubDistrict by remember { mutableStateOf(landData.locationSubDistrict) }
    var locationDistrict by remember { mutableStateOf(landData.locationDistrict) }
    var locationProvince by remember { mutableStateOf(landData.locationProvince) }
    var locationPostalCode by remember { mutableStateOf(landData.locationPostalCode) }

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
                        text = title,
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
                            amount = Money.fromDecimal(amount) ?: Money(0),
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
                    enabled = isFormValid && !isLoading
                ) {
                    Text(if (isLoading) "กำลังบันทึก..." else submitLabel, style = MaterialTheme.typography.bodyLarge, color = Color.White)
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
            FormErrorBanner(errorMessage)
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
                LandReferenceHeader(title = "อาคาร / สิ่งปลูกสร้างบนที่ดินนี้", onAddClick = { showBuildingsheet = true })
                Spacer(modifier = Modifier.height(8.dp))

                if (currentBuilding.isEmpty()) {
                    LandEmptyReferenceBox(onClick = { showBuildingsheet = true })
                } else {
                    currentBuilding.forEach { build ->
                        LandReferenceItem(name = build.areaName) { currentBuilding.remove(build) }
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

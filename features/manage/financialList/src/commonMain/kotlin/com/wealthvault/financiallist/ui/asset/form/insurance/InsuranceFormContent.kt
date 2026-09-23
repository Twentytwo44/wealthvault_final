package com.wealthvault.financiallist.ui.asset.form.insurance

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_back
import com.wealthvault.core.generated.resources.ic_common_calendar
import com.wealthvault.core.theme.*
import com.wealthvault.core.utils.formatThaiDate
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.core.model.Attachment
import com.wealthvault.financiallist.ui.form.*
import com.wealthvault.domain.portfolio.InsuranceModel
import com.wealthvault.core.model.Money
import com.wealthvault.financiallist.ui.form.FormErrorBanner
import com.wealthvault.financiallist.ui.form.formErrorMessage
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsuranceInputForm(
    onBackClick: () -> Unit = {},
    onNextClick: (InsuranceModel, List<Attachment>, List<Attachment>) -> Unit,
    insuranceData: InsuranceModel,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    title: String = "แก้ไขข้อมูลประกัน",
    submitLabel: String = "ยืนยันการแก้ไข",
) {
    // 🌟 1. ตั้งค่าเริ่มต้น (Support Edit Mode)
    var name by remember { mutableStateOf(insuranceData.name) }
    var policyNumber by remember { mutableStateOf(insuranceData.policyNumber) }
    var type by remember { mutableStateOf(insuranceData.type) }
    var companyName by remember { mutableStateOf(insuranceData.companyName) }
    var coverageAmount by remember { mutableStateOf(if (insuranceData.coverageAmount.minorUnits == 0L) "" else insuranceData.coverageAmount.decimalString()) }
    var coveragePeriod by remember { mutableStateOf(insuranceData.coveragePeriod) }
    var description by remember { mutableStateOf(insuranceData.description) }

    // Date States
    var apiConDate by remember { mutableStateOf(insuranceData.conDate) }
    var conDateDisplay by remember { mutableStateOf(if (insuranceData.conDate.isNotBlank()) formatThaiDate(insuranceData.conDate) else "") }
    var showConDatePicker by remember { mutableStateOf(false) }
    val conDatePickerState = rememberDatePickerState()

    var apiExpDate by remember { mutableStateOf(insuranceData.expDate) }
    var expDateDisplay by remember { mutableStateOf(if (insuranceData.expDate.isNotBlank()) formatThaiDate(insuranceData.expDate) else "") }
    var showExpDatePicker by remember { mutableStateOf(false) }
    val expDatePickerState = rememberDatePickerState()

    // Attachment States (Added/Deleted Logic)
    val originalAssets = remember { mutableStateListOf<Attachment>().apply { addAll(insuranceData.attachments) } }
    val currentAssets = remember { mutableStateListOf<Attachment>().apply { addAll(insuranceData.attachments) } }
    val filePicker = rememberFilePicker { newFiles -> currentAssets.addAll(newFiles) }

    val isFormValid = name.isNotBlank() && policyNumber.isNotBlank() && companyName.isNotBlank() && coverageAmount.isNotBlank() && apiConDate.isNotBlank() && apiExpDate.isNotBlank()

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
            Box(modifier = Modifier.navigationBarsPadding().padding(24.dp)) {
                Button(
                    onClick = {
                        val data = InsuranceModel(
                            policyNumber = policyNumber, type = type, companyName = companyName,
                            coverageAmount = Money.fromDecimal(coverageAmount) ?: Money(0), coveragePeriod = coveragePeriod,
                            expDate = apiExpDate, conDate = apiConDate, description = description,
                            name = name, attachments = currentAssets.toList()
                        )

                        val addList = currentAssets.filter { it.id.isNullOrEmpty() }
                        val deleteList = originalAssets.filter { old -> currentAssets.none { it.id == old.id } }

                        onNextClick(data, addList, deleteList)
                    },
                    modifier = Modifier.fillMaxWidth().height(46.dp), // 🌟 มาตรฐาน 46.dp
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
            Spacer(modifier = Modifier.height(16.dp))

            DropdownInput(
                label = "ประเภทประกัน*", options = insuranceTypes,
                selectedValue = type, onValueChange = { type = it }
            )

            AssetTextField(value = name, onValueChange = { name = it }, label = "ชื่อเรียกประกัน*", placeholder = "ระบุชื่อเรียกประกัน")
            AssetTextField(value = policyNumber, onValueChange = { policyNumber = it }, label = "เลขกรมธรรม์*", placeholder = "ระบุเลขที่กรมธรรม์")
            AssetTextField(value = companyName, onValueChange = { companyName = it }, label = "ชื่อบริษัทประกัน*", placeholder = "ระบุชื่อบริษัท")

            CustomTextField(
                value = coverageAmount,
                onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*\$"))) coverageAmount = it },
                label = "จำนวนวงเงินคุ้มครอง*", placeholder = "0.00", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            AssetTextField(value = coveragePeriod, onValueChange = { coveragePeriod = it }, label = "ระยะเวลาคุ้มครอง", placeholder = "เช่น 10 ปี, ตลอดชีพ")

            // 🌟 Date Selectors (สูง 44.dp มาตรฐาน)
            CustomTextField(
                value = conDateDisplay, onValueChange = { }, label = "วันที่เริ่มสัญญา*", placeholder = "เลือกวันที่", readOnly = true,
                trailingIcon = { Icon(painterResource(Res.drawable.ic_common_calendar), null, tint = LightPrimary, modifier = Modifier.size(24.dp)) },
                onClick = { showConDatePicker = true }
            )

            CustomTextField(
                value = expDateDisplay, onValueChange = { }, label = "วันหมดอายุ*", placeholder = "เลือกวันที่", readOnly = true,
                trailingIcon = { Icon(painterResource(Res.drawable.ic_common_calendar), null, tint = LightPrimary, modifier = Modifier.size(24.dp)) },
                onClick = { showExpDatePicker = true }
            )

            AssetTextField(value = description, onValueChange = { description = it }, label = "รายละเอียดเพิ่มเติม", placeholder = "ระบุรายละเอียดเพิ่มเติม", isMultiLine = true)

            Spacer(modifier = Modifier.height(8.dp))
            ReferenceImagepicker(
                attachments = currentAssets, onAddImage = { filePicker.launchImage() },
                onAddPdf = { filePicker.launchPdf() }, onRemove = { item -> currentAssets.remove(item) }
            )
            Spacer(modifier = Modifier.height(32.dp))
        }

        // --- DatePicker Dialogs ---
        if (showConDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showConDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        conDatePickerState.selectedDateMillis?.let { millis ->
                            val localDate = Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.UTC)
                            apiConDate = "${localDate.year}-${(localDate.month.ordinal + 1).toString().padStart(2, '0')}-${localDate.day.toString().padStart(2, '0')}"
                            conDateDisplay = formatThaiDate(apiConDate)
                        }
                        showConDatePicker = false
                    }) { Text("ตกลง", color = LightPrimary) }
                },
                dismissButton = { TextButton(onClick = { showConDatePicker = false }) { Text("ยกเลิก", color = Color.Gray) } }
            ) {
                DatePicker(state = conDatePickerState, colors = DatePickerDefaults.colors(selectedDayContainerColor = LightPrimary, todayDateBorderColor = LightPrimary, todayContentColor = LightPrimary))
            }
        }

        if (showExpDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showExpDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        expDatePickerState.selectedDateMillis?.let { millis ->
                            val localDate = Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.UTC)
                            apiExpDate = "${localDate.year}-${(localDate.month.ordinal + 1).toString().padStart(2, '0')}-${localDate.day.toString().padStart(2, '0')}"
                            expDateDisplay = formatThaiDate(apiExpDate)
                        }
                        showExpDatePicker = false
                    }) { Text("ตกลง", color = LightPrimary) }
                },
                dismissButton = { TextButton(onClick = { showExpDatePicker = false }) { Text("ยกเลิก", color = Color.Gray) } }
            ) {
                DatePicker(state = expDatePickerState, colors = DatePickerDefaults.colors(selectedDayContainerColor = LightPrimary, todayDateBorderColor = LightPrimary, todayContentColor = LightPrimary))
            }
        }
    }
}

// 🌟 Common Master UI Component (BasicTextField 44.dp)
@Composable
private fun CustomTextField(
    value: String, onValueChange: (String) -> Unit, label: String, placeholder: String,
    readOnly: Boolean = false, trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default, onClick: (() -> Unit)? = null
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = LightPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        Box {
            BasicTextField(
                value = value, onValueChange = onValueChange, readOnly = readOnly,
                singleLine = true, keyboardOptions = keyboardOptions,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF3A2F2A)),
                cursorBrush = SolidColor(LightPrimary),
                modifier = Modifier.fillMaxWidth().height(44.dp),
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier.fillMaxSize().background(LightSoftWhite, RoundedCornerShape(12.dp))
                            .border(1.dp, LightBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            if (value.isEmpty()) Text(text = placeholder, color = Color.LightGray, style = MaterialTheme.typography.bodyLarge)
                            innerTextField()
                        }
                        if (trailingIcon != null) { Spacer(modifier = Modifier.width(8.dp)); trailingIcon() }
                    }
                }
            )
            if (onClick != null) Box(modifier = Modifier.matchParentSize().clip(RoundedCornerShape(12.dp)).clickable { onClick() })
        }
    }
}

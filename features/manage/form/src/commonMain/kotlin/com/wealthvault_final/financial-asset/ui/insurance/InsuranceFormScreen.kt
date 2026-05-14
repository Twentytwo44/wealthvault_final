package com.wealthvault_final.`financial-asset`.ui.insurance

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
import androidx.compose.ui.text.font.FontWeight
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
import com.wealthvault_final.`financial-asset`.Imagepicker.Attachment
import com.wealthvault_final.`financial-asset`.Imagepicker.rememberFilePicker
import com.wealthvault_final.`financial-asset`.model.InsuranceModel
import com.wealthvault_final.`financial-asset`.ui.components.AssetTextField
import com.wealthvault_final.`financial-asset`.ui.components.ReferenceImagepicker
import com.wealthvault_final.`financial-asset`.ui.components.maptype.DropdownInput
import com.wealthvault_final.`financial-asset`.ui.components.maptype.insuranceTypes
import com.wealthvault_final.`financial-asset`.ui.insurance.viewmodel.InsuranceScreenModel
import com.wealthvault_final.`financial-asset`.ui.share.ShareAssetScreen
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource

class InsuranceFormScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<InsuranceScreenModel>()

        val state by screenModel.state.collectAsStateWithLifecycle()

        InsuranceInputForm(
            initialData = state,
            onBackClick = { navigator.pop() },
            onNextClick = { data ->
                screenModel.updateForm(data)
                navigator.push(ShareAssetScreen(request = data))
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsuranceInputForm(
    initialData: InsuranceModel,
    onBackClick: () -> Unit = {},
    onNextClick: (InsuranceModel) -> Unit
) {
    var name by remember { mutableStateOf(initialData.name) }
    var type by remember { mutableStateOf(initialData.type) }
    var policyNumber by remember { mutableStateOf(initialData.policyNumber) }
    var companyName by remember { mutableStateOf(initialData.companyName) }
    var coverageAmount by remember { mutableStateOf(if (initialData.coverageAmount == 0.0) "" else initialData.coverageAmount.toString()) }
    var coveragePeriod by remember { mutableStateOf(initialData.coveragePeriod) }
    var description by remember { mutableStateOf(initialData.description) }

    var apiConDate by remember { mutableStateOf(initialData.conDate) }
    var conDate by remember { mutableStateOf(if (initialData.conDate.isNotBlank()) formatThaiDate(initialData.conDate) else "") }
    var showConDatePicker by remember { mutableStateOf(false) }
    val conDatePickerState = rememberDatePickerState()

    var apiExpDate by remember { mutableStateOf(initialData.expDate) }
    var expDate by remember { mutableStateOf(if (initialData.expDate.isNotBlank()) formatThaiDate(initialData.expDate) else "") }
    var showExpDatePicker by remember { mutableStateOf(false) }
    val expDatePickerState = rememberDatePickerState()

    val attachments = remember { mutableStateListOf<Attachment>().apply { addAll(initialData.attachments) } }
    val filePicker = rememberFilePicker { newFiles -> attachments.addAll(newFiles) }

    val isFormValid = name.isNotBlank() && type.isNotBlank() && policyNumber.isNotBlank() &&
            companyName.isNotBlank() && coverageAmount.isNotBlank() &&
            apiConDate.isNotBlank() && apiExpDate.isNotBlank()

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
                    Text(text = "ข้อมูลประกัน", style = MaterialTheme.typography.titleLarge, color = LightPrimary)
                }
            }
        },
        bottomBar = {
            Box(modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(24.dp)) {
                Button(
                    onClick = {
                        val data = InsuranceModel(
                            policyNumber = policyNumber, type = type, companyName = companyName,
                            coverageAmount = coverageAmount.toDoubleOrNull() ?: 0.0, coveragePeriod = coveragePeriod,
                            expDate = apiExpDate, conDate = apiConDate, description = description,
                            name = name, attachments = attachments.toList()
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
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 24.dp).verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            DropdownInput(
                label = "ประเภทประกัน*", options = insuranceTypes,
                selectedValue = type, onValueChange = { type = it }
            )

            // 🌟 ใช้ AssetTextField สำหรับข้อความทั่วไป
            AssetTextField(value = name, onValueChange = { name = it }, label = "ชื่อเรียกประกัน*", placeholder = "กรอกชื่อเรียกประกัน")
            AssetTextField(value = policyNumber, onValueChange = { policyNumber = it }, label = "เลขกรมธรรม์*", placeholder = "กรอกเลขประกัน")
            AssetTextField(value = companyName, onValueChange = { companyName = it }, label = "ชื่อบริษัทประกัน*", placeholder = "กรอกชื่อบริษัทประกัน")

            // 🌟 ใช้ CustomTextField สำหรับตัวเลข (BasicTextField 44.dp)
            CustomTextField(
                value = coverageAmount,
                onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*\$"))) coverageAmount = it },
                label = "จำนวนวงเงินคุ้มครอง*", placeholder = "0.00", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            AssetTextField(value = coveragePeriod, onValueChange = { coveragePeriod = it }, label = "ระยะเวลาคุ้มครอง", placeholder = "เช่น 10 ปี, ตลอดชีพ")

            // 🌟 ใช้ CustomTextField สำหรับปฏิทิน
            CustomTextField(
                value = conDate, onValueChange = { }, label = "วันที่เริ่มสัญญา*", placeholder = "เลือกวันที่", readOnly = true,
                trailingIcon = { Icon(painterResource(Res.drawable.ic_common_calendar), null, tint = LightPrimary, modifier = Modifier.size(24.dp)) },
                onClick = { showConDatePicker = true }
            )

            CustomTextField(
                value = expDate, onValueChange = { }, label = "วันหมดอายุ*", placeholder = "เลือกวันที่", readOnly = true,
                trailingIcon = { Icon(painterResource(Res.drawable.ic_common_calendar), null, tint = LightPrimary, modifier = Modifier.size(24.dp)) },
                onClick = { showExpDatePicker = true }
            )

            AssetTextField(value = description, onValueChange = { description = it }, label = "คำอธิบาย", placeholder = "รายละเอียดเพิ่มเติม", isMultiLine = true)

            Spacer(modifier = Modifier.height(8.dp))
            ReferenceImagepicker(
                attachments = attachments, onAddImage = { filePicker.launchImage() },
                onAddPdf = { filePicker.launchPdf() }, onRemove = { item -> attachments.remove(item) }
            )
            Spacer(modifier = Modifier.height(32.dp))
        }

        // --- DatePickers ---
        if (showConDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showConDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        conDatePickerState.selectedDateMillis?.let { millis ->
                            val instant = Instant.fromEpochMilliseconds(millis)
                            val localDate = instant.toLocalDateTime(TimeZone.UTC)
                            apiConDate = "${localDate.year}-${localDate.monthNumber.toString().padStart(2, '0')}-${localDate.dayOfMonth.toString().padStart(2, '0')}"
                            conDate = formatThaiDate(apiConDate)
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
                            val instant = Instant.fromEpochMilliseconds(millis)
                            val localDate = instant.toLocalDateTime(TimeZone.UTC)
                            apiExpDate = "${localDate.year}-${localDate.monthNumber.toString().padStart(2, '0')}-${localDate.dayOfMonth.toString().padStart(2, '0')}"
                            expDate = formatThaiDate(apiExpDate)
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

// 🌟 Common Component: CustomTextField (มาตรฐาน Master UI)
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

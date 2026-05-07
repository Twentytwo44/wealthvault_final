package com.wealthvault_final.`financial-asset`.ui.insurance

// 🌟 Import Theme ของแอป
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_back
import com.wealthvault.core.generated.resources.ic_common_calendar
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault.core.utils.formatThaiDate
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault_final.`financial-asset`.Imagepicker.Attachment
import com.wealthvault_final.`financial-asset`.Imagepicker.rememberFilePicker
import com.wealthvault_final.`financial-asset`.model.InsuranceModel
import com.wealthvault_final.`financial-asset`.ui.components.ReferenceImagepicker
import com.wealthvault_final.`financial-asset`.ui.components.maptype.DropdownInput
import com.wealthvault_final.`financial-asset`.ui.components.maptype.insuranceTypes
import com.wealthvault_final.`financial-asset`.ui.insurance.viewmodel.InsuranceScreenModel
import com.wealthvault_final.`financial-asset`.ui.share.ShareAssetScreen
import com.wealthvault_final.`financial-obligations`.ui.expense.CustomTextField
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource

class InsuranceFormScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<InsuranceScreenModel>()

        // 🌟 1. ดึง State ปัจจุบันออกมา
        val state by screenModel.state.collectAsState()

        InsuranceInputForm(
            initialData = state, // 🌟 2. โยนค่าเดิมเข้าไปตั้งต้นให้ฟอร์ม
            onBackClick = { navigator.pop() },
            onNextClick = { data ->
                println("data asset input: ${data.attachments}")
                screenModel.updateForm(data)

                // 🌟 ส่งข้อมูลก้อนที่อัปเดตแล้ว (data) ไปให้ ShareAssetScreen โดยตรง
                navigator.push(ShareAssetScreen(request = data))
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsuranceInputForm(
    initialData: InsuranceModel, // 🌟 รับค่าเริ่มต้น
    onBackClick: () -> Unit = {},
    onNextClick: (InsuranceModel) -> Unit
) {
    // 🌟 ดึงค่าจาก initialData มาใส่ตอนเริ่มต้น
    var name by remember { mutableStateOf(initialData.name) }
    var type by remember { mutableStateOf(initialData.type) }
    var policyNumber by remember { mutableStateOf(initialData.policyNumber) }
    var companyName by remember { mutableStateOf(initialData.companyName) }
    var coverageAmount by remember { mutableStateOf(if (initialData.coverageAmount == 0.0) "" else initialData.coverageAmount.toString()) }
    var coveragePeriod by remember { mutableStateOf(initialData.coveragePeriod) }
    var description by remember { mutableStateOf(initialData.description) }

    // 🌟 จัดการ State ปฏิทิน สำหรับวันที่ทำสัญญา
    var apiConDate by remember { mutableStateOf(initialData.conDate) } // ส่ง Backend
    var conDate by remember { mutableStateOf(if (initialData.conDate.isNotBlank()) formatThaiDate(initialData.conDate) else "") } // โชว์บน UI
    var showConDatePicker by remember { mutableStateOf(false) }
    val conDatePickerState = rememberDatePickerState()

    // 🌟 จัดการ State ปฏิทิน สำหรับวันหมดอายุ
    var apiExpDate by remember { mutableStateOf(initialData.expDate) } // ส่ง Backend
    var expDate by remember { mutableStateOf(if (initialData.expDate.isNotBlank()) formatThaiDate(initialData.expDate) else "") } // โชว์บน UI
    var showExpDatePicker by remember { mutableStateOf(false) }
    val expDatePickerState = rememberDatePickerState()

    val attachments = remember { mutableStateListOf<Attachment>().apply { addAll(initialData.attachments) } }
    val filePicker = rememberFilePicker { newFiles -> attachments.addAll(newFiles) }

    // 🌟 เช็คข้อมูลจำเป็น
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
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onBackClick() }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "ข้อมูลประกัน",
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
                        val data = InsuranceModel(
                            policyNumber = policyNumber,
                            type = type,
                            companyName = companyName,
                            coverageAmount = coverageAmount.toDoubleOrNull() ?: 0.0,
                            coveragePeriod = coveragePeriod,
                            expDate = apiExpDate,
                            conDate = apiConDate,
                            description = description,
                            name = name,
                            attachments = attachments.toList()
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

            DropdownInput(
                label = "ประเภทประกัน*",
                options = insuranceTypes,
                selectedValue = type,
                onValueChange = { type = it }
            )

            CustomTextField(value = name, onValueChange = { name = it }, label = "ชื่อเรียกประกัน*", placeholder = "กรอกชื่อเรียกประกัน")
            CustomTextField(value = policyNumber, onValueChange = { policyNumber = it }, label = "เลขกรมธรรม์*", placeholder = "กรอกเลขประกัน")
            CustomTextField(value = companyName, onValueChange = { companyName = it }, label = "ชื่อบริษัทประกัน*", placeholder = "กรอกชื่อบริษัทประกัน")

            CustomTextField(
                value = coverageAmount,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*\$"))) {
                        coverageAmount = newValue
                    }
                },
                label = "จำนวนวงเงินคุ้มครอง*",
                placeholder = "0.00",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            CustomTextField(value = coveragePeriod, onValueChange = { coveragePeriod = it }, label = "ระยะเวลาคุ้มครอง", placeholder = "เช่น 10 ปี, ตลอดชีพ")

            CustomTextField(
                value = conDate,
                onValueChange = { },
                label = "วันที่เริ่มสัญญา*",
                placeholder = "เลือกวันที่",
                readOnly = true,
                trailingIcon = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_common_calendar),
                        contentDescription = "Calendar",
                        tint = LightPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                },
                onClick = { showConDatePicker = true }
            )

            CustomTextField(
                value = expDate,
                onValueChange = { },
                label = "วันหมดอายุ*",
                placeholder = "เลือกวันที่",
                readOnly = true,
                trailingIcon = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_common_calendar),
                        contentDescription = "Calendar",
                        tint = LightPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                },
                onClick = { showExpDatePicker = true }
            )

            CustomTextField(value = description, onValueChange = { description = it }, label = "คำอธิบาย", placeholder = "รายละเอียดเพิ่มเติม", isMultiLine = true)

            Spacer(modifier = Modifier.height(24.dp))

            ReferenceImagepicker(
                attachments = attachments,
                onAddImage = { filePicker.launchImage() },
                onAddPdf = { filePicker.launchPdf() },
                onRemove = { item -> attachments.remove(item) }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }

        // =====================================
        // 📅 DatePicker วันที่เริ่มสัญญา
        // =====================================
        if (showConDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showConDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        conDatePickerState.selectedDateMillis?.let { millis ->
                            val instant = Instant.fromEpochMilliseconds(millis)
                            val localDate = instant.toLocalDateTime(TimeZone.UTC)
                            val day = localDate.dayOfMonth.toString().padStart(2, '0')
                            val month = localDate.monthNumber.toString().padStart(2, '0')
                            val engYear = localDate.year.toString()

                            apiConDate = "$engYear-$month-$day"
                            conDate = formatThaiDate(apiConDate)
                        }
                        showConDatePicker = false
                    }) {
                        Text("ตกลง", color = LightPrimary)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConDatePicker = false }) {
                        Text("ยกเลิก", color = Color.Gray)
                    }
                }
            ) {
                DatePicker(
                    state = conDatePickerState,
                    colors = DatePickerDefaults.colors(
                        selectedDayContainerColor = LightPrimary,
                        todayDateBorderColor = LightPrimary,
                        todayContentColor = LightPrimary
                    )
                )
            }
        }

        // =====================================
        // 📅 DatePicker วันหมดอายุ
        // =====================================
        if (showExpDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showExpDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        expDatePickerState.selectedDateMillis?.let { millis ->
                            val instant = Instant.fromEpochMilliseconds(millis)
                            val localDate = instant.toLocalDateTime(TimeZone.UTC)
                            val day = localDate.dayOfMonth.toString().padStart(2, '0')
                            val month = localDate.monthNumber.toString().padStart(2, '0')
                            val engYear = localDate.year.toString()

                            apiExpDate = "$engYear-$month-$day"
                            expDate = formatThaiDate(apiExpDate)
                        }
                        showExpDatePicker = false
                    }) {
                        Text("ตกลง", color = LightPrimary)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showExpDatePicker = false }) {
                        Text("ยกเลิก", color = Color.Gray)
                    }
                }
            ) {
                DatePicker(
                    state = expDatePickerState,
                    colors = DatePickerDefaults.colors(
                        selectedDayContainerColor = LightPrimary,
                        todayDateBorderColor = LightPrimary,
                        todayContentColor = LightPrimary
                    )
                )
            }
        }
    }
}

// 🌟 Component ย่อย CustomTextField
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    readOnly: Boolean = false,
    isMultiLine: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onClick: (() -> Unit)? = null
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = LightPrimary)
        Spacer(modifier = Modifier.height(8.dp))

        Box {
            TextField(
                value = value,
                onValueChange = onValueChange,
                readOnly = readOnly,
                placeholder = { Text(placeholder, color = Color.LightGray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (isMultiLine) Modifier.height(120.dp) else Modifier)
                    .border(1.dp, LightBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = LightSoftWhite,
                    unfocusedContainerColor = LightSoftWhite,
                    disabledContainerColor = LightSoftWhite,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedTextColor = Color(0xFF3A2F2A),
                    unfocusedTextColor = Color(0xFF3A2F2A)
                ),
                keyboardOptions = keyboardOptions,
                singleLine = !isMultiLine,
                trailingIcon = trailingIcon
            )

            // 🌟 ดักคลิกถ้ามีการส่ง onClick เข้ามา (สำหรับเปิดปฏิทิน)
            if (onClick != null) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { onClick() }
                )
            }
        }
    }
}
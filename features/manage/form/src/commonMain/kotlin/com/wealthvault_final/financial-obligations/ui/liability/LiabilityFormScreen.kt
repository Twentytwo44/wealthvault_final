package com.wealthvault_final.`financial-obligations`.ui.liability

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
import cafe.adriel.voyager.core.model.rememberScreenModel
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
import com.wealthvault_final.`financial-asset`.Imagepicker.Attachment
import com.wealthvault_final.`financial-asset`.Imagepicker.rememberFilePicker
import com.wealthvault_final.`financial-asset`.ui.components.AssetTextField
import com.wealthvault_final.`financial-asset`.ui.components.ReferenceImagepicker
import com.wealthvault_final.`financial-asset`.ui.share.ShareAssetScreen
import com.wealthvault_final.`financial-obligations`.model.LiabilityModel
import com.wealthvault_final.`financial-obligations`.ui.liability.viewmodel.LiabilityScreenModel
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource

class LiabilityFormScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { LiabilityScreenModel() }

        // 🌟 1. ดึง State ออกมา
        val state by screenModel.state.collectAsState()

        LiabilityInputForm(
            initialData = state, // 🌟 2. โยนค่าเริ่มต้นเข้าไปในฟอร์ม
            onBackClick = { navigator.pop() },
            onNextClick = { data ->
                screenModel.updateForm(data)
                // 🌟 ส่ง Request ไปหน้าถัดไปโดยใช้ data ที่อัปเดตแล้ว
                navigator.push(ShareAssetScreen(request = data))
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiabilityInputForm(
    initialData: LiabilityModel, // 🌟 รับ initialData เข้ามา
    onBackClick: () -> Unit = {},
    onNextClick: (LiabilityModel) -> Unit
) {
    // 🌟 ดึงค่าจาก initialData มาใส่ตั้งต้น
    var name by remember { mutableStateOf(initialData.name) }

    // ยอดเงิน และ ดอกเบี้ย
    var principal by remember { mutableStateOf(if (initialData.principal == 0.0) "" else initialData.principal.toString()) }
    var interestRate by remember { mutableStateOf(initialData.interestRate) }
    var creditor by remember { mutableStateOf(initialData.creditor) }
    var description by remember { mutableStateOf(initialData.description) }

    // 🌟 จัดการ State ปฏิทิน วันที่เริ่มต้น
    var apiStartedAt by remember { mutableStateOf(initialData.startedAt) } // ส่ง Backend
    var statedAt by remember { mutableStateOf(if (initialData.startedAt.isNotBlank()) formatThaiDate(initialData.startedAt) else "") } // โชว์บน UI
    var showStartDatePicker by remember { mutableStateOf(false) }
    val startDatePickerState = rememberDatePickerState()

    // 🌟 จัดการ State ปฏิทิน วันที่สิ้นสุด
    var apiEndedAt by remember { mutableStateOf(initialData.endedAt) } // ส่ง Backend
    var endedAt by remember { mutableStateOf(if (initialData.endedAt.isNotBlank()) formatThaiDate(initialData.endedAt) else "") } // โชว์บน UI
    var showEndDatePicker by remember { mutableStateOf(false) }
    val endDatePickerState = rememberDatePickerState()

    // 🌟 ดึงข้อมูลไฟล์แนบเดิมกลับมา
    val attachments = remember { mutableStateListOf<Attachment>().apply { addAll(initialData.attachments) } }
    val filePicker = rememberFilePicker { newFiles -> attachments.addAll(newFiles) }

    // 🌟 เช็คข้อมูลจำเป็น
    val isFormValid = name.isNotBlank() && principal.isNotBlank() && interestRate.isNotBlank() && apiStartedAt.isNotBlank()

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
                        text = "ข้อมูลหนี้สิน",
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
                        val data = LiabilityModel(
                            name = name,
                            type = "LIABILITY_TYPE_LOAN",
                            principal = principal.toDoubleOrNull() ?: 0.0,
                            interestRate = interestRate,
                            startedAt = apiStartedAt, // ส่ง YYYY-MM-DD ให้ API
                            endedAt = apiEndedAt,     // ส่ง YYYY-MM-DD ให้ API
                            creditor = creditor,
                            description = description,
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

            AssetTextField(value = name, onValueChange = { name = it }, label = "ชื่อรายการ*", placeholder = "กรอกชื่อรายการ")

            CustomTextField(
                value = interestRate,
                onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*\$"))) interestRate = it },
                label = "ดอกเบี้ย*",
                placeholder = "0.00",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                trailingIcon = { Text("%", color = LightPrimary, modifier = Modifier.padding(end = 16.dp)) }
            )

            CustomTextField(
                value = principal,
                onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*\$"))) principal = it },
                label = "จำนวน*",
                placeholder = "0.00",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            CustomTextField(
                value = statedAt,
                onValueChange = { },
                label = "วันที่เริ่มต้น*",
                placeholder = "เลือกวันที่",
                readOnly = true,
                trailingIcon = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_common_calendar),
                        contentDescription = "Calendar",
                        tint = LightPrimary,
                        modifier = Modifier
                            .size(24.dp)
                    )
                },
                onClick = { showStartDatePicker = true }
            )

            CustomTextField(
                value = endedAt,
                onValueChange = { },
                label = "วันที่สิ้นสุด",
                placeholder = "เลือกวันที่",
                readOnly = true,
                trailingIcon = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_common_calendar),
                        contentDescription = "Calendar",
                        tint = LightPrimary,
                        modifier = Modifier
                            .size(24.dp)
                    )
                },
                onClick = { showEndDatePicker = true }
            )

            AssetTextField(value = creditor, onValueChange = { creditor = it }, label = "ผู้รับผิดชอบ", placeholder = "กรอกผู้รับผิดชอบ")
            AssetTextField(value = description, onValueChange = { description = it }, label = "คำอธิบาย", placeholder = "รายละเอียดเพิ่มเติม", isMultiLine = true)

            Spacer(modifier = Modifier.height(24.dp))

            ReferenceImagepicker(
                attachments = attachments,
                onAddImage = { filePicker.launchImage() },
                onAddPdf = { filePicker.launchPdf() },
                onRemove = { item -> attachments.remove(item) }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }

        // 📅 DatePicker วันที่เริ่มต้น
        if (showStartDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showStartDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        startDatePickerState.selectedDateMillis?.let { millis ->
                            val instant = Instant.fromEpochMilliseconds(millis)
                            val localDate = instant.toLocalDateTime(TimeZone.UTC)

                            val day = localDate.dayOfMonth.toString().padStart(2, '0')
                            val month = localDate.monthNumber.toString().padStart(2, '0')
                            val engYear = localDate.year.toString()

                            apiStartedAt = "$engYear-$month-$day"
                            statedAt = formatThaiDate(apiStartedAt)
                        }
                        showStartDatePicker = false
                    }) {
                        Text("ตกลง", color = LightPrimary)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showStartDatePicker = false }) {
                        Text("ยกเลิก", color = Color.Gray)
                    }
                }
            ) {
                DatePicker(
                    state = startDatePickerState,
                    colors = DatePickerDefaults.colors(
                        selectedDayContainerColor = LightPrimary,
                        todayDateBorderColor = LightPrimary,
                        todayContentColor = LightPrimary
                    )
                )
            }
        }

        // 📅 DatePicker วันที่สิ้นสุด
        if (showEndDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showEndDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        endDatePickerState.selectedDateMillis?.let { millis ->
                            val instant = Instant.fromEpochMilliseconds(millis)
                            val localDate = instant.toLocalDateTime(TimeZone.UTC)

                            val day = localDate.dayOfMonth.toString().padStart(2, '0')
                            val month = localDate.monthNumber.toString().padStart(2, '0')
                            val engYear = localDate.year.toString()

                            apiEndedAt = "$engYear-$month-$day"
                            endedAt = formatThaiDate(apiEndedAt)
                        }
                        showEndDatePicker = false
                    }) {
                        Text("ตกลง", color = LightPrimary)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEndDatePicker = false }) {
                        Text("ยกเลิก", color = Color.Gray)
                    }
                }
            ) {
                DatePicker(
                    state = endDatePickerState,
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
                singleLine = true,
                trailingIcon = trailingIcon
            )

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
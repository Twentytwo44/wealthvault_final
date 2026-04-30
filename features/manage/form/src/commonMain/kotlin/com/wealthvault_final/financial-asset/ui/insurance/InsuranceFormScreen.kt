package com.wealthvault_final.`financial-asset`.ui.insurance

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.utils.getScreenModel

// 🌟 Import Theme ของแอป
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightSoftWhite

import com.wealthvault_final.`financial-asset`.Imagepicker.Attachment
import com.wealthvault_final.`financial-asset`.Imagepicker.rememberFilePicker
import com.wealthvault_final.`financial-asset`.model.InsuranceModel
import com.wealthvault_final.`financial-asset`.ui.components.ReferenceImagepicker
import com.wealthvault_final.`financial-asset`.ui.components.maptype.DropdownInput
import com.wealthvault_final.`financial-asset`.ui.components.maptype.insuranceTypes
import com.wealthvault_final.`financial-asset`.ui.insurance.viewmodel.InsuranceScreenModel
import com.wealthvault_final.`financial-asset`.ui.share.ShareAssetScreen
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class InsuranceFormScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<InsuranceScreenModel>()

        InsuranceInputForm(
            onBackClick = { navigator.pop() },
            onNextClick = { data ->
                println("data asset input: ${data.attachments}")
                screenModel.updateForm(data)

                // 🌟 แก้ BUG: ส่งข้อมูลก้อนที่อัปเดตแล้ว (data) ไปให้ ShareAssetScreen โดยตรง
                navigator.push(ShareAssetScreen(request = data))
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsuranceInputForm(
    onBackClick: () -> Unit = {},
    onNextClick: (InsuranceModel) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var policyNumber by remember { mutableStateOf("") }
    var companyName by remember { mutableStateOf("") }
    var coverageAmount by remember { mutableStateOf("") }
    var coveragePeriod by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // 🌟 จัดการ State ปฏิทิน สำหรับวันที่ทำสัญญา
    var conDate by remember { mutableStateOf("") } // โชว์บน UI (พ.ศ.)
    var apiConDate by remember { mutableStateOf("") } // ส่ง Backend (YYYY-MM-DD)
    var showConDatePicker by remember { mutableStateOf(false) }
    val conDatePickerState = rememberDatePickerState()

    // 🌟 จัดการ State ปฏิทิน สำหรับวันหมดอายุ
    var expDate by remember { mutableStateOf("") } // โชว์บน UI (พ.ศ.)
    var apiExpDate by remember { mutableStateOf("") } // ส่ง Backend (YYYY-MM-DD)
    var showExpDatePicker by remember { mutableStateOf(false) }
    val expDatePickerState = rememberDatePickerState()

    val attachments = remember { mutableStateListOf<Attachment>() }
    val filePicker = rememberFilePicker { newFiles -> attachments.addAll(newFiles) }

    // 🌟 เช็คข้อมูลจำเป็น
    val isFormValid = name.isNotBlank() && type.isNotBlank() && policyNumber.isNotBlank() &&
            companyName.isNotBlank() && coverageAmount.isNotBlank() &&
            apiConDate.isNotBlank() && apiExpDate.isNotBlank()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = LightBg, // 🌟 ใช้ LightBg
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) { // 🌟 กันขอบขาวด้านบน
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
                    title = {
                        Text("ข้อมูลประกัน", color = LightPrimary, style = MaterialTheme.typography.titleLarge)
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null, tint = LightPrimary)
                        }
                    }
                )
            }
        },
        bottomBar = {
            // 🌟 ย้ายปุ่มมาไว้ BottomBar ให้ใช้งานง่ายขึ้น
            Box(modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(24.dp)) {
                Button(
                    onClick = {
                        val data = InsuranceModel(
                            policyNumber = policyNumber,
                            type = type,
                            companyName = companyName,
                            coverageAmount = coverageAmount.toDoubleOrNull() ?: 0.0,
                            coveragePeriod = coveragePeriod,
                            expDate = apiExpDate, // 🌟 ส่งตัว YYYY-MM-DD ไป
                            conDate = apiConDate, // 🌟 ส่งตัว YYYY-MM-DD ไป
                            description = description,
                            name = name,
                            attachments = attachments.toList() // ใช้ toList เพื่อความปลอดภัย
                        )
                        onNextClick(data)
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
                    shape = RoundedCornerShape(12.dp),
                    enabled = isFormValid // 🌟 ปุ่มจะสว่างเมื่อกรอกครบ
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

            // 🌟 วันที่เริ่มสัญญา
            CustomTextField(
                value = conDate,
                onValueChange = { },
                label = "วันที่เริ่มสัญญา*",
                placeholder = "วว/ดด/ปปปป",
                readOnly = true,
                trailingIcon = {
                    Icon(Icons.Default.DateRange, contentDescription = "Calendar", tint = LightPrimary)
                },
                onClick = { showConDatePicker = true }
            )

            // 🌟 วันหมดอายุ
            CustomTextField(
                value = expDate,
                onValueChange = { },
                label = "วันหมดอายุ*",
                placeholder = "วว/ดด/ปปปป",
                readOnly = true,
                trailingIcon = {
                    Icon(Icons.Default.DateRange, contentDescription = "Calendar", tint = LightPrimary)
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

                            apiConDate = "$engYear-$month-$day" // สำหรับส่ง Backend
                            conDate = "$day/$month/${localDate.year + 543}" // โชว์บน UI เป็น พ.ศ.
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

                            apiExpDate = "$engYear-$month-$day" // สำหรับส่ง Backend
                            expDate = "$day/$month/${localDate.year + 543}" // โชว์บน UI เป็น พ.ศ.
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

// 🌟 Component ย่อย CustomTextField (นำมาไว้ในไฟล์นี้เพื่อใช้สร้างกล่องพิมพ์ข้อความ)
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
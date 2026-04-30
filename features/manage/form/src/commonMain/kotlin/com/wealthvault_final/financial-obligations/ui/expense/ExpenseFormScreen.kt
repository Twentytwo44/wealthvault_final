package com.wealthvault_final.`financial-obligations`.ui.expense

// 🌟 Import Theme ของแอป

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import com.wealthvault_final.`financial-asset`.Imagepicker.Attachment
import com.wealthvault_final.`financial-asset`.Imagepicker.rememberFilePicker
import com.wealthvault_final.`financial-asset`.ui.components.ReferenceImagepicker
import com.wealthvault_final.`financial-asset`.ui.components.maptype.DropdownInput
import com.wealthvault_final.`financial-asset`.ui.components.maptype.expenseTypes
import com.wealthvault_final.`financial-asset`.ui.share.ShareAssetScreen
import com.wealthvault_final.`financial-obligations`.model.ExpenseModel
import com.wealthvault_final.`financial-obligations`.ui.expense.viewmodel.ExpenseScreenModel
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource

class ExpenseFormScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { ExpenseScreenModel() }

        ExpenseInputForm(
            onBackClick = { navigator.pop() },
            onNextClick = { data ->
                // 1. อัปเดตข้อมูลลง ScreenModel
                screenModel.updateForm(data)

                // 🌟 2. แก้ BUG: ส่ง `data` (ExpenseModel) ไปตรงๆ แทนการส่ง state.value
                navigator.push(ShareAssetScreen(request = data))
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseInputForm(
    onBackClick: () -> Unit = {},
    onNextClick: (ExpenseModel) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var principal by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // 🌟 จัดการ State ปฏิทิน
    var statedAt by remember { mutableStateOf("") } // โชว์บน UI (พ.ศ.)
    var apiStartedAt by remember { mutableStateOf("") } // ส่ง Backend (YYYY-MM-DD)
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    val attachments = remember { mutableStateListOf<Attachment>() }
    val filePicker = rememberFilePicker { newFiles -> attachments.addAll(newFiles) }

    // 🌟 เช็คข้อมูลจำเป็น
    val isFormValid = name.isNotBlank() && type.isNotBlank() && principal.isNotBlank() && apiStartedAt.isNotBlank()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = LightBg,
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
                    title = {
                        Text(
                            "ข้อมูลค่าใช้จ่ายระยะยาว",
                            color = LightPrimary,
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_common_back),
                                contentDescription = "Back",
                                tint = LightPrimary,
                                modifier = Modifier
                                    .size(24.dp)
                            )
                        }
                    }
                )
            }
        },
        bottomBar = {
            Box(modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(24.dp)) {
                Button(
                    onClick = {
                        val data = ExpenseModel(
                            name = name,
                            type = type,
                            principal = principal.toDoubleOrNull() ?: 0.0,
                            interestRate = "", // ค่าใช้จ่ายอาจจะไม่มีดอกเบี้ย ส่งว่างไว้ตามโครงสร้างเดิม
                            startedAt = apiStartedAt, // 🌟 ส่งตัว YYYY-MM-DD ให้ Backend
                            endedAt = "",
                            creditor = "",
                            description = description,
                            attachments = attachments.toList()
                        )
                        onNextClick(data)
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
                    shape = RoundedCornerShape(12.dp),
                    enabled = isFormValid // 🌟 เปิดปุ่มเฉพาะตอนกรอกครบ
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
                label = "ประเภทรายจ่าย*",
                options = expenseTypes,
                selectedValue = type,
                onValueChange = { type = it }
            )

            CustomTextField(value = name, onValueChange = { name = it }, label = "ผู้ให้บริการ / ชื่อรายการ*", placeholder = "กรอกชื่อรายการ")

            // 🌟 จำนวนเงิน (กรอกได้แค่ตัวเลขและทศนิยม)
            CustomTextField(
                value = principal,
                onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*\$"))) principal = it },
                label = "ยอดชำระต่อรอบ*",
                placeholder = "0.00",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // 🌟 วันที่เริ่มต้น
            CustomTextField(
                value = statedAt,
                onValueChange = { },
                label = "วันที่เริ่มสัญญา*",
                placeholder = "วว/ดด/ปปปป",
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
                onClick = { showDatePicker = true } // เปิดปฏิทินเมื่อกด
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
        // 📅 DatePicker
        // =====================================
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val instant = Instant.fromEpochMilliseconds(millis)
                            val localDate = instant.toLocalDateTime(TimeZone.UTC)

                            val day = localDate.dayOfMonth.toString().padStart(2, '0')
                            val month = localDate.monthNumber.toString().padStart(2, '0')
                            val engYear = localDate.year.toString()

                            apiStartedAt = "$engYear-$month-$day" // สำหรับส่ง Backend
                            statedAt = "$day/$month/${localDate.year + 543}" // โชว์เป็น พ.ศ.
                        }
                        showDatePicker = false
                    }) {
                        Text("ตกลง", color = LightPrimary)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("ยกเลิก", color = Color.Gray)
                    }
                }
            ) {
                DatePicker(
                    state = datePickerState,
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

// 🌟 Component ย่อย CustomTextField สร้างไว้ในไฟล์เพื่อป้องกันผลกระทบกับไฟล์อื่น
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
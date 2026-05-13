package com.wealthvault.financiallist.ui.debt.form.expense

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
import com.wealthvault_final.`financial-asset`.ui.components.AssetTextField
import com.wealthvault_final.`financial-asset`.ui.components.ReferenceImagepicker
import com.wealthvault_final.`financial-obligations`.model.ExpenseModel
import com.wealthvault_final.`financial-obligations`.ui.expense.viewmodel.ExpenseScreenModel
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource

class ExpenseFormScreen(val id:String,val debtData: ExpenseModel) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<com.wealthvault.financiallist.ui.debt.form.expense.ExpenseScreenModel>()

        ExpenseInputForm(
            onBackClick = { navigator.pop() } ,
            onNextClick = { data,addedList,deletedList ->
                println("data asset input: ${data.attachments}")
                screenModel.updateForm(data)
                screenModel.updateAttachment(addedList,deletedList)
                screenModel.submitLiability(id,
                    onSuccess = {
                        // 💡 หลังจากแก้ไขสำเร็จ จะส่งกลับหน้าลิสต์
                        navigator.pop()
                    })
            },
            debtData = debtData
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseInputForm(
    onBackClick: () -> Unit = {},
    onNextClick: (ExpenseModel, List<Attachment>, List<Attachment>) -> Unit,
    debtData: ExpenseModel
) {
    // 🌟 1. State เริ่มต้นจากข้อมูลเก่า (Edit Mode)
    var name by remember { mutableStateOf(debtData.name) }
    var principal by remember { mutableStateOf(if (debtData.principal == 0.0) "" else debtData.principal.toString()) }
    var creditor by remember { mutableStateOf(debtData.creditor) }
    var description by remember { mutableStateOf(debtData.description) }

    // Date States
    var apiStartedAt by remember { mutableStateOf(debtData.startedAt) }
    var statedAtDisplay by remember { mutableStateOf(if (debtData.startedAt.isNotBlank()) formatThaiDate(debtData.startedAt) else "") }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    // image diff state
    val originalAssets = remember { mutableStateListOf<Attachment>().apply { addAll(debtData.attachments) } }
    val currentAssets = remember { mutableStateListOf<Attachment>().apply { addAll(debtData.attachments) } }
    val filePicker = rememberFilePicker { newFiles -> currentAssets.addAll(newFiles) }

    val isFormValid = name.isNotBlank() && principal.isNotBlank() && apiStartedAt.isNotBlank()

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
                        null, tint = LightPrimary,
                        modifier = Modifier.size(24.dp).clickable { onBackClick() }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = "แก้ไขข้อมูลค่าใช้จ่าย", style = MaterialTheme.typography.titleLarge, color = LightPrimary)
                }
            }
        },
        bottomBar = {
            Box(modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(24.dp)) {
                Button(
                    onClick = {
                        val data = ExpenseModel(
                            name = name, type = "LIABILITY_TYPE_EXPENSE",
                            principal = principal.toDoubleOrNull() ?: 0.0,
                            interestRate = "", startedAt = apiStartedAt,
                            endedAt = "", creditor = creditor,
                            description = description, attachments = currentAssets.toList()
                        )
                        val addList = currentAssets.filter { it.id.isNullOrEmpty() }
                        val deleteList = originalAssets.filter { old -> currentAssets.none { it.id == old.id } }

                        onNextClick(data, addList, deleteList)
                    },
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
                    shape = RoundedCornerShape(12.dp),
                    enabled = isFormValid
                ) {
                    Text("ยืนยันการแก้ไข", style = MaterialTheme.typography.bodyLarge, color = Color.White)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 24.dp).verticalScroll(rememberScrollState())
        ) {
            AssetTextField(value = name, onValueChange = { name = it }, label = "ผู้ให้บริการ / ชื่อรายการ*", placeholder = "กรอกชื่อรายการ")

            com.wealthvault_final.`financial-obligations`.ui.expense.CustomTextField(
                value = principal, onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*\$"))) principal = it },
                label = "ยอดชำระต่อรอบ*", placeholder = "0.00", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            com.wealthvault_final.`financial-obligations`.ui.expense.CustomTextField(
                value = statedAtDisplay, onValueChange = { }, label = "วันที่เริ่มสัญญา*", placeholder = "เลือกวันที่", readOnly = true,
                trailingIcon = { Icon(painterResource(Res.drawable.ic_common_calendar), null, tint = LightPrimary, modifier = Modifier.size(24.dp)) },
                onClick = { showDatePicker = true }
            )

            AssetTextField(value = creditor, onValueChange = { creditor = it }, label = "ผู้รับผิดชอบ", placeholder = "กรอกชื่อผู้รับผิดชอบ")
            AssetTextField(value = description, onValueChange = { description = it }, label = "คำอธิบาย", placeholder = "รายละเอียดเพิ่มเติม", isMultiLine = true)

            Spacer(modifier = Modifier.height(8.dp))
            ReferenceImagepicker(
                attachments = currentAssets, onAddImage = { filePicker.launchImage() },
                onAddPdf = { filePicker.launchPdf() }, onRemove = { item -> currentAssets.remove(item) }
            )
            Spacer(modifier = Modifier.height(32.dp))
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val localDate = Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.UTC)
                            apiStartedAt = "${localDate.year}-${localDate.monthNumber.toString().padStart(2, '0')}-${localDate.dayOfMonth.toString().padStart(2, '0')}"
                            statedAtDisplay = formatThaiDate(apiStartedAt)
                        }
                        showDatePicker = false
                    }) { Text("ตกลง", color = LightPrimary) }
                },
                dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("ยกเลิก", color = Color.Gray) } }
            ) { DatePicker(state = datePickerState, colors = DatePickerDefaults.colors(selectedDayContainerColor = LightPrimary)) }
        }
    }
}
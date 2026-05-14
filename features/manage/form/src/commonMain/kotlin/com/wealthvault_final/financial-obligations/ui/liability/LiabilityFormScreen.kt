package com.wealthvault_final.`financial-obligations`.ui.liability

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
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_back
import com.wealthvault.core.generated.resources.ic_common_calendar
import com.wealthvault.core.theme.*
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
        val state by screenModel.state.collectAsStateWithLifecycle()

        LiabilityInputForm(
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
fun LiabilityInputForm(
    initialData: LiabilityModel,
    onBackClick: () -> Unit = {},
    onNextClick: (LiabilityModel) -> Unit
) {
    var name by remember { mutableStateOf(initialData.name) }
    var principal by remember { mutableStateOf(if (initialData.principal == 0.0) "" else initialData.principal.toString()) }
    var interestRate by remember { mutableStateOf(initialData.interestRate) }
    var creditor by remember { mutableStateOf(initialData.creditor) }
    var description by remember { mutableStateOf(initialData.description) }

    // Date States
    var apiStartedAt by remember { mutableStateOf(initialData.startedAt) }
    var startedAtDisplay by remember { mutableStateOf(if (initialData.startedAt.isNotBlank()) formatThaiDate(initialData.startedAt) else "") }
    var showStartDatePicker by remember { mutableStateOf(false) }
    val startDatePickerState = rememberDatePickerState()

    var apiEndedAt by remember { mutableStateOf(initialData.endedAt) }
    var endedAtDisplay by remember { mutableStateOf(if (initialData.endedAt.isNotBlank()) formatThaiDate(initialData.endedAt) else "") }
    var showEndDatePicker by remember { mutableStateOf(false) }
    val endDatePickerState = rememberDatePickerState()

    val attachments = remember { mutableStateListOf<Attachment>().apply { addAll(initialData.attachments) } }
    val filePicker = rememberFilePicker { newFiles -> attachments.addAll(newFiles) }

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
                        null, tint = LightPrimary,
                        modifier = Modifier.size(24.dp).clickable { onBackClick() }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = "ข้อมูลหนี้สิน", style = MaterialTheme.typography.titleLarge, color = LightPrimary)
                }
            }
        },
        bottomBar = {
            Box(modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(24.dp)) {
                Button(
                    onClick = {
                        val data = LiabilityModel(
                            name = name, type = "LIABILITY_TYPE_LOAN",
                            principal = principal.toDoubleOrNull() ?: 0.0,
                            interestRate = interestRate, startedAt = apiStartedAt,
                            endedAt = apiEndedAt, creditor = creditor,
                            description = description, attachments = attachments.toList()
                        )
                        onNextClick(data)
                    },
                    modifier = Modifier.fillMaxWidth().height(46.dp),
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
            AssetTextField(value = name, onValueChange = { name = it }, label = "ชื่อรายการ*", placeholder = "กรอกชื่อรายการ")

            CustomTextField(
                value = principal, onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*\$"))) principal = it },
                label = "จำนวนเงิน*", placeholder = "0.00", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            CustomTextField(
                value = interestRate, onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*\$"))) interestRate = it },
                label = "ดอกเบี้ยต่อปี (%)*", placeholder = "0.00", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            CustomTextField(
                value = startedAtDisplay, onValueChange = { }, label = "วันที่เริ่มต้นสัญญา*", placeholder = "เลือกวันที่", readOnly = true,
                trailingIcon = { Icon(painterResource(Res.drawable.ic_common_calendar), null, tint = LightPrimary, modifier = Modifier.size(24.dp)) },
                onClick = { showStartDatePicker = true }
            )

            CustomTextField(
                value = endedAtDisplay, onValueChange = { }, label = "วันที่สิ้นสุดสัญญา", placeholder = "เลือกวันที่", readOnly = true,
                trailingIcon = { Icon(painterResource(Res.drawable.ic_common_calendar), null, tint = LightPrimary, modifier = Modifier.size(24.dp)) },
                onClick = { showEndDatePicker = true }
            )

            AssetTextField(value = creditor, onValueChange = { creditor = it }, label = "เจ้าหนี้ / สถาบันการเงิน", placeholder = "กรอกชื่อเจ้าหนี้")
            AssetTextField(value = description, onValueChange = { description = it }, label = "คำอธิบาย", placeholder = "รายละเอียดเพิ่มเติม", isMultiLine = true)

            Spacer(modifier = Modifier.height(8.dp))
            ReferenceImagepicker(
                attachments = attachments, onAddImage = { filePicker.launchImage() },
                onAddPdf = { filePicker.launchPdf() }, onRemove = { item -> attachments.remove(item) }
            )
            Spacer(modifier = Modifier.height(32.dp))
        }

        // --- DatePicker Dialogs ---
        if (showStartDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showStartDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        startDatePickerState.selectedDateMillis?.let { millis ->
                            val localDate = Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.UTC)
                            apiStartedAt = "${localDate.year}-${localDate.monthNumber.toString().padStart(2, '0')}-${localDate.dayOfMonth.toString().padStart(2, '0')}"
                            startedAtDisplay = formatThaiDate(apiStartedAt)
                        }
                        showStartDatePicker = false
                    }) { Text("ตกลง", color = LightPrimary) }
                },
                dismissButton = { TextButton(onClick = { showStartDatePicker = false }) { Text("ยกเลิก", color = Color.Gray) } }
            ) { DatePicker(state = startDatePickerState, colors = DatePickerDefaults.colors(selectedDayContainerColor = LightPrimary, todayDateBorderColor = LightPrimary, todayContentColor = LightPrimary)) }
        }

        if (showEndDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showEndDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        endDatePickerState.selectedDateMillis?.let { millis ->
                            val localDate = Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.UTC)
                            apiEndedAt = "${localDate.year}-${localDate.monthNumber.toString().padStart(2, '0')}-${localDate.dayOfMonth.toString().padStart(2, '0')}"
                            endedAtDisplay = formatThaiDate(apiEndedAt)
                        }
                        showEndDatePicker = false
                    }) { Text("ตกลง", color = LightPrimary) }
                },
                dismissButton = { TextButton(onClick = { showEndDatePicker = false }) { Text("ยกเลิก", color = Color.Gray) } }
            ) { DatePicker(state = endDatePickerState, colors = DatePickerDefaults.colors(selectedDayContainerColor = LightPrimary, todayDateBorderColor = LightPrimary, todayContentColor = LightPrimary)) }
        }
    }
}

// 🌟 Common Component: CustomTextField (สูง 44dp)
@Composable
fun CustomTextField(
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

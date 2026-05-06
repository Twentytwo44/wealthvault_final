package com.wealthvault.financiallist.ui.shareasset.component

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.utils.formatThaiDate
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePickerDialog(
    onDismiss: () -> Unit,
    onDateConfirm: (String, String) -> Unit // รับ 2 ค่า: apiDate และ thaiDate
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        colors = DatePickerDefaults.colors(containerColor = Color.White),
        confirmButton = {
            TextButton(onClick = {
                val millis = datePickerState.selectedDateMillis
                if (millis != null) {
                    val localDate = Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.UTC)
                    val day = localDate.dayOfMonth.toString().padStart(2, '0')
                    val month = localDate.monthNumber.toString().padStart(2, '0')
                    val engYear = localDate.year.toString()

                    val apiDateStr = "$engYear-$month-$day"
                    val thaiDateStr = formatThaiDate(apiDateStr)

                    onDateConfirm(apiDateStr, thaiDateStr)
                } else {
                    onDismiss()
                }
            }) {
                Text("ตกลง", color = LightPrimary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
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
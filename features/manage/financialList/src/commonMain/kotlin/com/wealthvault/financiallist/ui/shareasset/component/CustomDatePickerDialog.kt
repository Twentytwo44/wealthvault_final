package com.wealthvault.financiallist.ui.shareasset.component


import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePickerDialog(
    onDismiss: () -> Unit,
    onDateConfirm: (String) -> Unit
) {
    // ย้าย DatePickerState มาไว้ที่นี่ ทำให้ State ถูก Reset ใหม่ทุกครั้งที่เปิดป๊อปอัพ
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val millis = datePickerState.selectedDateMillis
                if (millis != null) {
                    val dateStr = Instant.fromEpochMilliseconds(millis)
                        .toLocalDateTime(TimeZone.currentSystemDefault())
                        .date.toString()

                    onDateConfirm(dateStr) // ส่งวันที่กลับไปให้หน้าจอหลัก
                } else {
                    onDismiss()
                }
            }) {
                Text("ตกลง", color = Color(0xFFC08064))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("ยกเลิก", color = Color.Gray)
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

package com.wealthvault.financiallist.ui.shareasset.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
        // 🌟 ปรับความโค้งมนของ Dialog ให้เป็น 12.dp ตาม Master UI
        shape = RoundedCornerShape(12.dp),
        colors = DatePickerDefaults.colors(
            containerColor = Color.White
        ),
        confirmButton = {
            TextButton(
                onClick = {
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
                }
            ) {
                Text(
                    text = "ตกลง",
                    style = MaterialTheme.typography.labelLarge,
                    color = LightPrimary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "ยกเลิก",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Gray
                )
            }
        }
    ) {
        // 🌟 ปรับแต่งสีภายใน DatePicker ให้สม่ำเสมอ
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                containerColor = Color.White,
                titleContentColor = LightPrimary,
                headlineContentColor = LightPrimary,
                selectedDayContainerColor = LightPrimary,
                selectedDayContentColor = Color.White,
                todayDateBorderColor = LightPrimary,
                todayContentColor = LightPrimary,
                dayContentColor = Color(0xFF3A2F2A) // สีตัวเลขวันที่ (Master Text Color)
            )
        )
    }
}
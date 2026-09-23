package com.wealthvault.profile.ui

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
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EditProfileDatePicker(
    onDismiss: () -> Unit,
    onDateSelected: (String) -> Unit,
) {
    val state = rememberDatePickerState()
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                state.selectedDateMillis?.let { millis ->
                    val date = Instant.fromEpochMilliseconds(millis)
                        .toLocalDateTime(TimeZone.UTC)
                    val day = date.day.toString().padStart(2, '0')
                    val month = (date.month.ordinal + 1).toString().padStart(2, '0')
                    onDateSelected("${date.year}-$month-$day")
                }
                onDismiss()
            }) {
                Text("ตกลง", color = LightPrimary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("ยกเลิก", color = Color.Gray)
            }
        },
    ) {
        DatePicker(
            state = state,
            colors = DatePickerDefaults.colors(
                selectedDayContainerColor = LightPrimary,
                todayDateBorderColor = LightPrimary,
                todayContentColor = LightPrimary,
            ),
        )
    }
}

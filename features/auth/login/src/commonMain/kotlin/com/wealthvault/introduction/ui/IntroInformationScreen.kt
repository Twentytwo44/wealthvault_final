package com.wealthvault.introduction.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.navigation.SharedScreen
import com.wealthvault.core.utils.formatThaiDate
import com.wealthvault.core.utils.getScreenModel
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class IntroQuestionScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<IntroScreenModel>()
        val uiState by screenModel.uiState.collectAsStateWithLifecycle()
        val mainScreen = rememberScreen(SharedScreen.Main)

        IntroQuestionContent(
            userName = uiState.userName,
            onUserNameChange = { screenModel.onAction(IntroUiAction.UserNameChanged(it)) },
            firstName = uiState.firstName,
            onFirstNameChange = { screenModel.onAction(IntroUiAction.FirstNameChanged(it)) },
            lastName = uiState.lastName,
            onLastNameChange = { screenModel.onAction(IntroUiAction.LastNameChanged(it)) },
            phoneNum = uiState.phoneNum,
            onPhoneNumChange = { screenModel.onAction(IntroUiAction.PhoneChanged(it)) },
            dob = uiState.birthday,
            picture = uiState.picture,
            onPictureChange = { screenModel.onAction(IntroUiAction.PictureChanged(it)) },
            onDobChange = { screenModel.onAction(IntroUiAction.BirthdayChanged(it)) },
            onBackClick = { navigator.pop() },
            onNextClick = {
                screenModel.updateProfile {
                    navigator.replaceAll(mainScreen)
                }
            },
        )

        if (uiState.errorMessage != null) {
            AlertDialog(
                onDismissRequest = screenModel::clearError,
                confirmButton = {
                    TextButton(onClick = screenModel::clearError) {
                        Text("ตกลง", color = LightPrimary)
                    }
                },
                title = { Text("ไม่สามารถบันทึกข้อมูลได้") },
                text = { Text(uiState.errorMessage.orEmpty()) },
            )
        }

        if (uiState.isLoading) {
            Dialog(onDismissRequest = {}) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .background(Color.White, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = LightPrimary)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntroQuestionContent(
    userName: String,
    onUserNameChange: (String) -> Unit,
    firstName: String,
    onFirstNameChange: (String) -> Unit,
    lastName: String,
    onLastNameChange: (String) -> Unit,
    phoneNum: String,
    onPhoneNumChange: (String) -> Unit,
    dob: String,
    onDobChange: (String) -> Unit,
    picture: ByteArray?,
    onPictureChange: (ByteArray?) -> Unit,
    onBackClick: () -> Unit,
    onNextClick: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val imagePicker = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Single,
        scope = scope,
        onResult = { byteArrays ->
            byteArrays.firstOrNull()?.let {
                onPictureChange(it)
            }
        }
    )
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    var birthday by remember { mutableStateOf("") }

    LaunchedEffect(dob) {
        if (dob.isNotBlank() && birthday.isBlank()) {
            birthday = formatThaiDate(dob)
        }
    }

    // 🌟 ดักการกรอกข้อมูล: ตรวจสอบว่ากรอกครบทุกช่องหรือยัง
    val isFormValid = validateIntroInput(
        userName = userName,
        firstName = firstName,
        lastName = lastName,
        phoneNum = phoneNum,
        birthday = dob,
    ) == null

    IntroForm(
        userName = userName,
        onUserNameChange = onUserNameChange,
        firstName = firstName,
        onFirstNameChange = onFirstNameChange,
        lastName = lastName,
        onLastNameChange = onLastNameChange,
        phoneNum = phoneNum,
        onPhoneNumChange = onPhoneNumChange,
        birthday = birthday,
        picture = picture,
        isFormValid = isFormValid,
        onBackClick = onBackClick,
        onPickPicture = { imagePicker.launch() },
        onShowDatePicker = { showDatePicker = true },
        onNextClick = onNextClick,
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val instant = Instant.fromEpochMilliseconds(millis)
                        val localDate = instant.toLocalDateTime(TimeZone.UTC).date

                        val day = localDate.day.toString().padStart(2, '0')
                        val month = (localDate.month.ordinal + 1).toString().padStart(2, '0')
                        val engYear = localDate.year.toString()

                        val isoDate = "$engYear-$month-$day"
                        onDobChange(isoDate)
                        birthday = formatThaiDate(isoDate)
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

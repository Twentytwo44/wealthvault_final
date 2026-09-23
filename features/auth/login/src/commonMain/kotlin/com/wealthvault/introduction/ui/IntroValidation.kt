package com.wealthvault.introduction.ui

/** Keeps onboarding required-field validation identical in UI and ScreenModel. */
internal fun validateIntroInput(
    userName: String,
    firstName: String,
    lastName: String,
    phoneNum: String,
    birthday: String,
): String? = if (
    userName.isBlank() || firstName.isBlank() || lastName.isBlank() ||
    phoneNum.isBlank() || birthday.isBlank()
) {
    "กรุณากรอกข้อมูลให้ครบถ้วน"
} else {
    null
}

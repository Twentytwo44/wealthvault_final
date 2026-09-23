package com.wealthvault.register.ui

private val registrationEmailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,}$")

/** Shared validation for UI submits and direct ScreenModel actions. */
internal fun validateRegistrationInput(
    username: String,
    password: String,
    confirmPassword: String,
): String? = when {
    username.isBlank() || password.isBlank() || confirmPassword.isBlank() -> "กรุณากรอกข้อมูลให้ครบถ้วน"
    !registrationEmailPattern.matches(username) -> "รูปแบบอีเมลไม่ถูกต้อง"
    password != confirmPassword -> "รหัสผ่านไม่ตรงกัน"
    else -> null
}

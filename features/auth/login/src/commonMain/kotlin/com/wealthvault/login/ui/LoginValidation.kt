package com.wealthvault.login.ui

private val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,}$")

/**
 * Validates credentials before starting a network request.
 *
 * Keeping this rule outside Compose makes the login contract deterministic and
 * lets every entry point (password login, tests, and future desktop clients)
 * use the same validation without allocating a regular expression per tap.
 */
internal fun validateLoginInput(username: String, password: String): String? = when {
    username.isBlank() || password.isBlank() -> "กรุณากรอกข้อมูลให้ครบถ้วน"
    !emailPattern.matches(username) -> "รูปแบบอีเมลไม่ถูกต้อง"
    else -> null
}

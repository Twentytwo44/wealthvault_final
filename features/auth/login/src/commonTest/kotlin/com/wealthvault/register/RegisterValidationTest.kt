package com.wealthvault.register

import com.wealthvault.register.ui.validateRegistrationInput
import kotlin.test.Test
import kotlin.test.assertEquals

class RegisterValidationTest {
    @Test
    fun validationCoversRequiredEmailAndPasswordRules() {
        assertEquals(
            "กรุณากรอกข้อมูลให้ครบถ้วน",
            validateRegistrationInput("", "secret", "secret"),
        )
        assertEquals(
            "รูปแบบอีเมลไม่ถูกต้อง",
            validateRegistrationInput("not-an-email", "secret", "secret"),
        )
        assertEquals(
            "รหัสผ่านไม่ตรงกัน",
            validateRegistrationInput("user@example.com", "secret", "different"),
        )
        assertEquals(null, validateRegistrationInput("user@example.com", "secret", "secret"))
    }
}

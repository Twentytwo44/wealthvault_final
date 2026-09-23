package com.wealthvault.introduction

import com.wealthvault.introduction.ui.validateIntroInput
import kotlin.test.Test
import kotlin.test.assertEquals

class IntroValidationTest {
    @Test
    fun onboardingRequiresAllProfileFields() {
        assertEquals(
            "กรุณากรอกข้อมูลให้ครบถ้วน",
            validateIntroInput("user", "First", "Last", "", "2000-01-01"),
        )
        assertEquals(
            null,
            validateIntroInput("user", "First", "Last", "0800000000", "2000-01-01"),
        )
    }
}

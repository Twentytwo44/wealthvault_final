package com.wealthvault.financiallist

import com.wealthvault.financiallist.ui.form.isDecimalInput
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FormInputValidationTest {
    @Test
    fun acceptsDecimalTypingStates() {
        assertTrue(isDecimalInput(""))
        assertTrue(isDecimalInput("12"))
        assertTrue(isDecimalInput("12."))
        assertTrue(isDecimalInput(".50"))
    }

    @Test
    fun rejectsMalformedDecimalInput() {
        assertFalse(isDecimalInput("12.3.4"))
        assertFalse(isDecimalInput("12 THB"))
        assertFalse(isDecimalInput("-12"))
    }
}

package com.wealthvault.profile.ui

import kotlin.test.Test
import kotlin.test.assertEquals

class EditProfileDateTest {
    @Test
    fun thaiDisplayDateConvertsToIsoDate() {
        assertEquals("1988-02-03", formatToApiDate("03/02/2531"))
    }

    @Test
    fun isoDateIsTrimmedToBackendDateOnly() {
        assertEquals("2026-09-22", formatToApiDate("2026-09-22T12:30:00Z"))
    }

    @Test
    fun blankAndUnparseableValuesRemainSafe() {
        assertEquals("", formatToApiDate("   "))
        assertEquals("03/02/not-a-year", formatToApiDate("03/02/not-a-year"))
    }
}

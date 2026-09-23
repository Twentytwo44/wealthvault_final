package com.wealthvault.core

import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.toAppError
import com.wealthvault.core.architecture.toAppResult
import com.wealthvault.core.observability.redactLogMessage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import com.wealthvault.core.architecture.runSuspendAppCatching

class AppResultAndRedactionTest {
    @Test
    fun mapsSuccessfulAndFailedResultsToAppResult() {
        assertEquals(AppResult.Success(42), Result.success(42).toAppResult())

        val failure = Result.failure<Int>(IllegalStateException("HTTP 401"))
            .toAppResult()
        assertEquals(AppResult.Failure(AppError.Unauthorized), failure)
    }

    @Test
    fun cancellationIsNeverConvertedToAUserError() {
        assertFailsWith<kotlinx.coroutines.CancellationException> {
            kotlinx.coroutines.CancellationException("cancelled").toAppError()
        }
    }

    @Test
    fun suspendCatchingPropagatesCancellation() = runTest {
        assertFailsWith<kotlinx.coroutines.CancellationException> {
            runSuspendAppCatching<Int> {
                throw kotlinx.coroutines.CancellationException("screen disposed")
            }
        }
    }

    @Test
    fun redactsBearerAndSecretFields() {
        val redacted = redactLogMessage(
            "authorization: Bearer abc123 access_token=secret refresh-token:r-secret password=pass email=user@example.com",
        )

        assertTrue(redacted.contains("authorization=[REDACTED]"))
        assertTrue(redacted.contains("access_token=[REDACTED]"))
        assertTrue(redacted.contains("refresh-token=[REDACTED]"))
        assertTrue(redacted.contains("password=[REDACTED]"))
        assertFalse(redacted.contains("abc123"))
        assertFalse(redacted.contains("secret"))
        assertFalse(redacted.contains("r-secret"))
        assertFalse(redacted.contains("user@example.com"))
        assertTrue(redacted.contains("[REDACTED_EMAIL]"))
    }

    @Test
    fun redactsJsonAndQuerySecretFields() {
        val redacted = redactLogMessage(
            "body={\"access_token\":\"json-secret\",\"password\":\"pw\"} " +
                "?refresh_token=query-secret&email=user@example.com",
        )

        assertFalse(redacted.contains("json-secret"))
        assertFalse(redacted.contains("query-secret"))
        assertFalse(redacted.contains("pw"))
        assertTrue(redacted.contains("access_token\":\"[REDACTED]"))
        assertTrue(redacted.contains("refresh_token=[REDACTED]"))
    }

    @Test
    fun classifiesNetworkAndUnknownErrors() {
        assertIs<AppError.Network>(IllegalStateException("Connect timeout").toAppError())
        assertIs<AppError.Unknown>(IllegalStateException("bad input").toAppError())
    }
}

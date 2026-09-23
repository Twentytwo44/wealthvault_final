package com.wealthvault.core

import com.wealthvault.core.concurrency.SingleFlight
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class SingleFlightTest {
    @Test
    fun concurrentCallersShareOneComputationPerKey() = runBlocking {
        val singleFlight = SingleFlight<String>()
        var executions = 0

        val values = (1..20).map {
            async {
                singleFlight.execute("dashboard") {
                    executions += 1
                    delay(10)
                    "cached-value"
                }
            }
        }.awaitAll()

        assertEquals(1, executions)
        assertEquals(listOf("cached-value"), values.distinct())
    }

    @Test
    fun differentKeysDoNotShareResults() = runBlocking {
        val singleFlight = SingleFlight<String>()

        val values = listOf(
            async { singleFlight.execute("a") { "first" } },
            async { singleFlight.execute("b") { "second" } },
        ).awaitAll()

        assertEquals(listOf("first", "second"), values)
    }
}

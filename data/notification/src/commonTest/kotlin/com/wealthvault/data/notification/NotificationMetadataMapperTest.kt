package com.wealthvault.data.notification

import com.wealthvault.data.notification.transport.notification.parseNotificationCompleted
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class NotificationMetadataMapperTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun mapsCompletionFlagAtTheDataBoundary() {
        assertEquals(true, parseNotificationCompleted("{\"is_completed\":true}", json))
        assertEquals(false, parseNotificationCompleted("{\"is_completed\":false}", json))
    }

    @Test
    fun malformedOrMissingMetadataDoesNotBreakNotificationMapping() {
        assertNull(parseNotificationCompleted("not-json", json))
        assertNull(parseNotificationCompleted("{}", json))
        assertNull(parseNotificationCompleted(null, json))
    }
}

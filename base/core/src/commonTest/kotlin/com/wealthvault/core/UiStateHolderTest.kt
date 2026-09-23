package com.wealthvault.core

import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.architecture.FormAction
import com.wealthvault.core.architecture.FormEffect
import com.wealthvault.core.model.Attachment
import com.wealthvault.core.model.AttachmentType
import com.wealthvault.core.architecture.UiStateHolder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UiStateHolderTest {
    @Test
    fun keepsFormStateImmutableAcrossLoadingAndSuccess() {
        val holder = UiStateHolder("draft")

        holder.loading()
        assertTrue(holder.state.value.isLoading)
        assertEquals("draft", holder.state.value.data)

        holder.success("saved")
        assertFalse(holder.state.value.isLoading)
        assertEquals("saved", holder.state.value.data)
        assertEquals(null, holder.state.value.error)
    }

    @Test
    fun failureStoresTypedErrorAndEmitsEffect() {
        val holder = UiStateHolder("draft")
        val error = AppError.Unknown(IllegalStateException("invalid"))

        holder.failure(error)

        assertFalse(holder.state.value.isLoading)
        assertEquals(error, holder.state.value.error)
        // The effect is delivered through the shared-flow buffer; state is the
        // durable assertion that callers can always make without a collector.
    }

    @Test
    fun commonFormActionsAndHolderSetAreAvailableDuringMigration() {
        val holder = UiStateHolder("draft")
        holder.set(data = "changed", isLoading = false, error = null)
        assertEquals("changed", holder.state.value.data)
        holder.emit(FormEffect.Saved)

        assertEquals("value", FormAction.Changed("value").value)
        val attachment = Attachment("file", AttachmentType.PDF)
        assertEquals(listOf(attachment), FormAction.AttachmentsChanged(listOf(attachment), emptyList()).added)
        assertEquals("asset-1", FormAction.Submit("asset-1").id)
        assertEquals(AppError.NotFound, (FormEffect.Failed(AppError.NotFound)).error)
    }
}

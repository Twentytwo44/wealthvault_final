package com.wealthvault.financialcommon.architecture

import com.wealthvault.core.architecture.UiAction
import com.wealthvault.domain.social.ShareTo

/**
 * Common actions for the legacy summary flows while they are migrated to UDF.
 * Keeping the action shape shared prevents each summary screen from inventing
 * a different state/update protocol.
 */
sealed interface SummaryAction<out T> : UiAction {
    data class Changed<T>(val value: T) : SummaryAction<T>
    data class ShareChanged(val value: ShareTo) : SummaryAction<Nothing>
    data object Submit : SummaryAction<Nothing>
}

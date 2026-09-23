package com.wealthvault.domain.portfolio

import com.wealthvault.core.model.Attachment
import com.wealthvault.core.model.Money

data class ExpenseModel(
    val type: String,
    val name: String,
    val principal: Money,
    /** Text input is converted to a fixed-scale rate at the command boundary. */
    val interestRate: String,
    val description: String,
    val startedAt: String,
    val endedAt: String,
    val creditor: String,
    val attachments: List<Attachment>,
)

data class LiabilityModel(
    val type: String,
    val name: String,
    val principal: Money,
    /** Text input is converted to a fixed-scale rate at the command boundary. */
    val interestRate: String,
    val description: String,
    val startedAt: String,
    val endedAt: String,
    val creditor: String,
    val attachments: List<Attachment>,
)

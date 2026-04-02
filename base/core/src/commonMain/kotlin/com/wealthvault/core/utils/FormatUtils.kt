package com.wealthvault.core.utils

import kotlin.math.roundToLong

fun formatAmount(amount: Number): String {
    val amountDouble = amount.toDouble()
    val isNegative = amountDouble < 0
    val absAmount = kotlin.math.abs(amountDouble)

    val totalCents = (absAmount * 100).roundToLong()
    val intPart = totalCents / 100
    val decPart = (totalCents % 100).toInt()

    val intString = intPart.toString().reversed().chunked(3).joinToString(",").reversed()
    val sign = if (isNegative) "-" else ""

    return if (decPart == 0) {
        "$sign$intString"
    } else {
        val decString = decPart.toString().padStart(2, '0').trimEnd('0')
        "$sign$intString.$decString"
    }
}

// แปลง 2000-02-03 -> 03/02/2543
fun formatThaiDate(dateString: String?): String {
    if (dateString.isNullOrEmpty() || dateString.length < 10) return "-"

    val datePart = dateString.take(10)
    val parts = datePart.split("-")

    if (parts.size == 3) {
        val year = (parts[0].toIntOrNull() ?: 0) + 543 // บวก 543 เป็น พ.ศ.
        val month = parts[1]
        val day = parts[2]
        return "$day/$month/$year"
    }

    return dateString
}
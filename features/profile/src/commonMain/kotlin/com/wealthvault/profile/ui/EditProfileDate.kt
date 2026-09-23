package com.wealthvault.profile.ui

private val isoDatePrefix = Regex("^\\d{4}-\\d{2}-\\d{2}.*$")

/** Converts the Thai display date back to the backend's ISO date format. */
fun formatToApiDate(displayDate: String): String {
    if (displayDate.isBlank()) return ""

    if (displayDate.length >= 10 && isoDatePrefix.matches(displayDate)) {
        return displayDate.take(10)
    }

    if (displayDate.contains("/")) {
        val parts = displayDate.split("/")
        if (parts.size == 3) {
            val day = parts[0].padStart(2, '0')
            val month = parts[1].padStart(2, '0')
            val thaiYear = parts[2].toIntOrNull() ?: return displayDate
            val engYear = thaiYear - 543

            return "$engYear-$month-$day"
        }
    }

    return displayDate
}

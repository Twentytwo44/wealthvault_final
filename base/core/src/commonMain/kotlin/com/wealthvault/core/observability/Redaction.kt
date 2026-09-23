package com.wealthvault.core.observability

private val SECRET_PATTERN = Regex(
    "(?i)((?:[\\\"']?(?:authorization|access[_-]?token|refresh[_-]?token|id[_-]?token|password|client[_-]?secret|token)[\\\"']?\\s*[:=]\\s*[\\\"']?(?:Bearer\\s+)?))([^,\\s&}\\\"']+)([\\\"']?)",
)
private val BEARER_PATTERN = Regex("(?i)Bearer\\s+[^,\\s]+")
private val EMAIL_PATTERN = Regex("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\\b")

/** Prevents credentials and common secret fields from reaching platform logs. */
internal fun redactLogMessage(message: String): String =
    EMAIL_PATTERN.replace(
        BEARER_PATTERN.replace(
            SECRET_PATTERN.replace(message) { match ->
                val prefix = match.groupValues[1]
                val trimmedPrefix = prefix.trimStart()
                if (trimmedPrefix.startsWith('"') || trimmedPrefix.startsWith('\'')) {
                    prefix + "[REDACTED]" + match.groupValues[3]
                } else {
                    prefix.substringBefore(':').substringBefore('=').trim() + "=[REDACTED]"
                }
            },
        ) { "Bearer [REDACTED]" },
    ) { "[REDACTED_EMAIL]" }

package com.wealthvault.core.observability

import android.os.Trace

internal actual fun beginPlatformTrace(name: String) {
    Trace.beginSection(name.take(127))
}

internal actual fun endPlatformTrace() {
    Trace.endSection()
}

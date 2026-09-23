package com.wealthvault.data_store

import kotlinx.coroutines.flow.StateFlow

enum class SessionState {
    Loading,
    Authenticated,
    SignedOut,
}

/** Stable session boundary exposed to UI and networking composition roots. */
interface SessionManager : SessionStore {
    val sessionState: StateFlow<SessionState>
}

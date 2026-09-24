package com.wealthvault.data.auth.google

import platform.UIKit.UIViewController

internal actual class GoogleAuthFactory(
    private val controller: UIViewController
) {
    actual fun create(): GoogleAuth {
        return GoogleAuthIOS(controller)
    }
}

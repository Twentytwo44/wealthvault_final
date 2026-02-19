package com.example.google_auth

import platform.UIKit.UIViewController

actual class GoogleAuthFactory(
    private val controller: UIViewController
) {
    actual fun create(): GoogleAuth {
        return GoogleAuthIOS(controller)
    }
}

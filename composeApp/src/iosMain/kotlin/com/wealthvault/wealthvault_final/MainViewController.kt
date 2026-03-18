package com.wealthvault.wealthvault_final

import androidx.compose.ui.window.ComposeUIViewController
import com.wealthvault.wealthvault_final.di.initKoin
import platform.UIKit.UIViewController


fun MainViewController(): UIViewController {
    val controller = ComposeUIViewController {
        App()
    }

    initKoin(controller) // 👈 start ที่นี่เลย

    return controller
}

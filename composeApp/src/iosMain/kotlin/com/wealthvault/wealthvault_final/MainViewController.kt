package com.wealthvault.app

import androidx.compose.ui.window.ComposeUIViewController
import com.wealthvault.app.di.initKoin
import com.wealthvault.app.di.registerAppNavigation
import com.wealthvault.security.line.SwiftLineAuth
import platform.UIKit.UIViewController



fun MainViewController(lineAuth: SwiftLineAuth): UIViewController {
    registerAppNavigation()

    val controller = ComposeUIViewController {
        App()
    }

    // 🟢 เช็คด้วยตัวเองเลยว่า Koin เคยรันหรือยัง
    initKoin(controller,lineAuth)

    return controller


}

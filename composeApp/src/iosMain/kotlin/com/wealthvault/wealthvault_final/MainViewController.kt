package com.wealthvault.wealthvault_final

import androidx.compose.ui.window.ComposeUIViewController
import com.example.wealthvault_final.di.initKoin
import com.wealthvault_final.line_auth.SwiftLineAuth
import platform.UIKit.UIViewController



fun MainViewController(lineAuth: SwiftLineAuth): UIViewController {

    val controller = ComposeUIViewController {
        App()
    }

    // 🟢 เช็คด้วยตัวเองเลยว่า Koin เคยรันหรือยัง
    initKoin(controller,lineAuth)

    return controller


}

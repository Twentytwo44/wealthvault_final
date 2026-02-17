package com.wealthvault.core.utils

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import org.koin.compose.getKoin
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier

@Composable
inline fun <reified T : ScreenModel> Screen.getScreenModel(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
): T {
    val koin = getKoin() // ดึง Koin instance ปัจจุบัน

    return rememberScreenModel(tag = qualifier?.value) {
        koin.get(qualifier, parameters) // สร้าง ViewModel ผ่าน Koin
    }
}

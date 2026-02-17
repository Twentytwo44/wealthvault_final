package com.wealthvault.wealthvault_final

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.Navigator
import com.wealthvault.login.ui.LoginScreen
import com.wealthvault.navigation.MainScreen

@Composable
@Preview
fun App() {
    MaterialTheme {
//        MainScreen()
        Navigator(LoginScreen())
    }
}

package com.wealthvault.splashscreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.introduction.ui.IntroScreen
import com.wealthvault.login.ui.LoginScreen
import com.wealthvault.navigation.MainScreen


class SplashScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<SplashScreenModel>()
        val state by screenModel.state.collectAsState()

        LaunchedEffect(state) {
            when (state) {
                is SplashState.GoToLogin -> navigator.replaceAll(LoginScreen())
                is SplashState.GoToIntro -> navigator.replaceAll(IntroScreen())
                is SplashState.GoToMain -> navigator.replaceAll(MainScreen())
                else -> {} // Loading
            }
        }

        // แสดงโลโก้หรือตัวโหลด
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

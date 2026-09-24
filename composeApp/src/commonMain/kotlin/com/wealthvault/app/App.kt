package com.wealthvault.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import com.wealthvault.core.theme.WealthVaultTheme
import com.wealthvault.core.utils.LocalRootNavigator
import com.wealthvault.splashscreen.SplashScreen
import com.wealthvault.app.navigation.AppCoordinator
import com.wealthvault.core.security.rememberLineSignInProvider
import com.wealthvault.profile.ui.LineSignInProviderFactory
import com.wealthvault.profile.ui.LocalLineSignInProviderFactory
import org.koin.compose.koinInject

//val LocalRootNavigator = staticCompositionLocalOf<Navigator> {
//    error("ยังไม่ได้ Provide Root Navigator!")
//}

/**
 * Composition-root implementation for the feature-owned LINE capability.
 * Keeping this as a concrete object avoids exporting a nested Kotlin lambda
 * through the iOS framework while preserving the feature/security boundary.
 */
private class AppLineSignInProviderFactory : LineSignInProviderFactory {
    @Composable
    override fun rememberProvider(
        onSuccess: (com.wealthvault.domain.profile.LineUser) -> Unit,
        onError: (String) -> Unit,
    ) = rememberLineSignInProvider(
        onSuccess = onSuccess,
        onError = onError,
    )
}

@Composable
@Preview
fun App() {
    MaterialTheme {
        WealthVaultTheme {
            // 🚩 เปลี่ยนจาก LoginScreen() เป็น SplashScreen()
            Navigator(SplashScreen()) { navigator ->
                val lineSignInProviderFactory = remember {
                    AppLineSignInProviderFactory()
                }

                CompositionLocalProvider(
                    LocalRootNavigator provides navigator,
                    LocalLineSignInProviderFactory provides lineSignInProviderFactory,
                ) {

                    val appCoordinator = koinInject<AppCoordinator>()

                    LaunchedEffect(navigator) {
                        appCoordinator.observe(navigator)
                    }

                    CurrentScreen()
                }
            }
        }
    }
}

package com.wealthvault.navigation.tabs

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_nav_profile
import com.wealthvault.login.ui.LoginScreen
import com.wealthvault.navigation.MainAppDestination
import com.wealthvault.profile.ui.EditProfileScreen
import com.wealthvault.profile.ui.MenuProfileSettingScreen
import com.wealthvault.profile.ui.ProfileScreen
import com.wealthvault.profile.ui.ShareSettingScreen
import org.jetbrains.compose.resources.painterResource

object ProfileTab : Tab {
    override val options: TabOptions
        @Composable
        get() = TabOptions(
            index = 3u,
            title = "โปรไฟล์",
            icon = painterResource(Res.drawable.ic_nav_profile)
        )

    @Composable
    override fun Content() {
        val rootNavigator = LocalNavigator.currentOrThrow.parent ?: LocalNavigator.currentOrThrow

        Navigator(
            ProfileScreen(
                onSettingsClick = {
                    rootNavigator.push(MenuProfileSettingDestination(rootNavigator))
                }
            )
        )
    }
}

// ==========================================
// 🚀 โซนสร้าง Screen (เส้นทาง) สำหรับให้ Voyager รู้จัก
// ==========================================

class MenuProfileSettingDestination(private val rootNavigator: Navigator) : Screen {
    @Composable
    override fun Content() {
        MenuProfileSettingScreen(
            onBackClick = { rootNavigator.pop() },
            onEditProfileClick = { rootNavigator.push(EditProfileDestination(rootNavigator)) },
            onShareSettingClick = { rootNavigator.push(ShareSettingDestination(rootNavigator)) },
            onLogoutClick = {
                rootNavigator.replaceAll(LoginScreen(navigateToScreen = MainAppDestination()))
            }
        )
    }
}

class EditProfileDestination(private val rootNavigator: Navigator) : Screen {
    @Composable
    override fun Content() {
        EditProfileScreen(
            onBackClick = { rootNavigator.pop() },
            onSaveClick = {
                rootNavigator.pop()
                rootNavigator.pop()
            }
        ).Content() // 🌟 อันนี้ถูกต้องแล้ว
    }
}

class ShareSettingDestination(private val rootNavigator: Navigator) : Screen {
    @Composable
    override fun Content() {
        ShareSettingScreen(
            onBackClick = { rootNavigator.pop() },
//            onSaveClick = { rootNavigator.pop() }
        ).Content() // 🌟 เติม .Content() ตรงนี้! ไม่งั้นมันจะไม่ยอมวาด UI ครับ
    }
}
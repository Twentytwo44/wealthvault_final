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
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.profile.ui.EditProfileScreenModel

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

        // 🌟 1. ลบ ProfileScreenDestination ออก แล้วใช้ ProfileScreen ตรงๆ เลยครับ
        // พร้อมโยนคำสั่งให้มันรู้ว่า "ถ้ามีคนกดปุ่มฟันเฟือง ให้ push หน้าเมนูขึ้นมาบังเลยนะ"
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

// 🌟 2. หน้า ProfileScreenDestination ลบทิ้งได้เลยครับ ไม่ต้องใช้แล้ว!

// 🌟 3. หน้ารวมเมนู (อันนี้ใช้ครอบเหมือนเดิม ถูกต้องแล้วครับ)
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

// 🌟 4. หน้าแก้โปรไฟล์
class EditProfileDestination(private val rootNavigator: Navigator) : Screen {
    @Composable
    override fun Content() {
        EditProfileScreen(
            onBackClick = { rootNavigator.pop() },
            onSaveClick = {
                rootNavigator.pop()
                rootNavigator.pop()
            }
        ).Content()
    }
}

// 🌟 5. หน้าตั้งค่าแชร์ทรัพย์สิน
class ShareSettingDestination(private val rootNavigator: Navigator) : Screen {
    @Composable
    override fun Content() {
        ShareSettingScreen(
            onBackClick = { rootNavigator.pop() },
            onSaveClick = { rootNavigator.pop() }
        )
    }
}
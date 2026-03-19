package com.wealthvault.navigation.tabs

// Import หน้า UI ทั้ง 4 หน้าของเรา
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
        // 🌟 ดึง Root Navigator ออกมาตรงๆ แบบชัวร์ 100% (ไม่ต้อง while loop แล้ว)
        val rootNavigator = LocalNavigator.currentOrThrow.parent ?: LocalNavigator.currentOrThrow

        // 🌟 เรียกหน้า ProfileScreenDestination แล้วโยนทางด่วนให้มันเอาไปใช้!
        Navigator(ProfileScreenDestination(rootNavigator))
    }
}

// ==========================================
// 🚀 โซนสร้าง Screen (เส้นทาง) สำหรับให้ Voyager รู้จัก
// ==========================================

// 🌟 1. หน้าเริ่มต้น
class ProfileScreenDestination(private val rootNavigator: Navigator) : Screen {
    @Composable
    override fun Content() {
        ProfileScreen(
//            onSettingsClick = {
//                // กดแล้วผลักหน้ารวมเมนูขึ้นมาผ่าน Root Navigator (ทับ Navbar มิดแน่นอน)
//                rootNavigator.push(MenuProfileSettingDestination(rootNavigator))
//            }
        )
    }
}

// 🌟 2. หน้ารวมเมนู
class MenuProfileSettingDestination(private val rootNavigator: Navigator) : Screen {
    @Composable
    override fun Content() {
        MenuProfileSettingScreen(
            onBackClick = { rootNavigator.pop() }, // กดย้อนกลับ
            onEditProfileClick = { rootNavigator.push(EditProfileDestination(rootNavigator)) },
            onShareSettingClick = { rootNavigator.push(ShareSettingDestination(rootNavigator)) },
            onLogoutClick = {
                // 🌟 สั่งให้มันรู้ว่า ถ้าล็อคอินรอบหน้าเสร็จ ให้กลับมาที่ MainAppDestination นะ!
                rootNavigator.replaceAll(LoginScreen(navigateToScreen = MainAppDestination()))
            }
        )
    }
}

// 🌟 3. หน้าแก้โปรไฟล์
class EditProfileDestination(private val rootNavigator: Navigator) : Screen {
    @Composable
    override fun Content() {
        EditProfileScreen(
            onBackClick = { rootNavigator.pop() },
            onSaveClick = { rootNavigator.pop() }
        )
    }
}

// 🌟 4. หน้าตั้งค่าแชร์ทรัพย์สิน
class ShareSettingDestination(private val rootNavigator: Navigator) : Screen {
    @Composable
    override fun Content() {
        ShareSettingScreen(
            onBackClick = { rootNavigator.pop() },
            onSaveClick = { rootNavigator.pop() }
        )
    }
}

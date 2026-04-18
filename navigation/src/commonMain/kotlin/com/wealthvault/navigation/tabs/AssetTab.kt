package com.wealthvault.navigation.tabs

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_nav_asset
import com.wealthvault.financiallist.ui.asset.AssetScreen
import com.wealthvault.financiallist.ui.shareasset.ShareAssetContent
import com.wealthvault.financiallist.ui.shareasset.ShareAssetScreen
import com.wealthvault.login.ui.LoginScreen
import com.wealthvault.navigation.MainAppDestination
import com.wealthvault.profile.ui.MenuProfileSettingScreen
import org.jetbrains.compose.resources.painterResource

object AssetTab : Tab {

    override val options: TabOptions
        @Composable
        get() = TabOptions(
            index = 3u,
            title = "ทรัพย์สิน",
            icon = painterResource(Res.drawable.ic_nav_asset)
        )

    @Composable
    override fun Content() {
        // 🌟 2. ครอบด้วย Navigator
        val rootNavigator = LocalNavigator.currentOrThrow.parent ?: LocalNavigator.currentOrThrow

        Navigator(AssetScreen(

        ))
    }
}

//
//class MenuAssetDestination(private val rootNavigator: Navigator) : Screen {
//    @Composable
//    override fun Content() {
//        MenuProfileSettingScreen(
//            onBackClick = { rootNavigator.pop() },
////            onEditProfileClick = { rootNavigator.push(EditProfileDestination(rootNavigator)) },
//            onShareAssetClick = { rootNavigator.push(ShareSettingDestination(rootNavigator)) },
//            onLogoutClick = {
//                rootNavigator.replaceAll(LoginScreen(navigateToScreen = MainAppDestination()))
//            }
//        )
//    }
//}
//
//class ShareAssetDestination(private val rootNavigator: Navigator) : Screen {
//    @Composable
//    override fun Content() {
//        ShareAssetScreen(
//            onBackClick = { rootNavigator.pop() },
//            onSaveClick = { rootNavigator.pop() },
//
//        ).Content() // 🌟 เติม .Content() ตรงนี้! ไม่งั้นมันจะไม่ยอมวาด UI ครับ
//    }
//}

package com.wealthvault.social.ui.main_social.friend

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator // 🌟 Import Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.social.ui.components.FriendListItem
import com.wealthvault.social.ui.components.SocialSearchBar
import com.wealthvault.`user-api`.model.FriendData
import com.wealthvault.social.ui.space.FriendSpaceScreen // 🌟 Import หน้า Space

class FriendScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<FriendScreenModel>()

        // 🌟 1. ดึง Root Navigator เพื่อให้หน้า Space เปิดทับ Bottom Bar ขึ้นมาได้
        val navigator = LocalNavigator.currentOrThrow
        var rootNavigator = navigator
        while (rootNavigator.parent != null) {
            rootNavigator = rootNavigator.parent!!
        }

        LaunchedEffect(Unit) {
            screenModel.fetchFriends()
        }

        val friends by screenModel.friends.collectAsState()

        FriendContent(
            friends = friends,
            // 🌟 2. ส่งคำสั่ง onClick เข้าไป
            onFriendClick = { friendId, friendName ->
                rootNavigator.push(FriendSpaceScreen(friendId = friendId, friendName = friendName))
            }
        )
    }
}

@Composable
fun FriendContent(
    friends: List<FriendData>,
    modifier: Modifier = Modifier,
    onFriendClick: (String, String) -> Unit // 🌟 3. รับ Event การคลิก
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredFriends = friends.filter {
        it.username?.contains(searchQuery, ignoreCase = true) == true ||
                it.firstName?.contains(searchQuery, ignoreCase = true) == true
    }

    Column(modifier = modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(16.dp))

        SocialSearchBar(
            searchQuery = searchQuery,
            onSearchChange = { searchQuery = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(filteredFriends) { friend ->
                // 🌟 4. ดัก Event ตอนกดการ์ดแต่ละใบ
                FriendListItem(
                    friend = friend,
                    onClick = {
                        val id = friend.id ?: ""
                        val name = friend.username ?: friend.firstName ?: "Unknown"
                        onFriendClick(id, name) // โยน ID กับ Name ออกไป
                    }
                )
            }
        }
    }
}
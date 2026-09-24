package com.wealthvault.social.ui.main_social.friend

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault.social.ui.components.FriendListItem
import com.wealthvault.social.ui.components.SocialEmptyState
import com.wealthvault.social.ui.components.SocialNoResultsState
import com.wealthvault.social.ui.components.SocialSearchBar
import com.wealthvault.social.ui.space.FriendSpaceScreen
import com.wealthvault.domain.profile.FriendData

class FriendScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<FriendScreenModel>()

        // 🌟 1. ดึง Root Navigator เพื่อให้หน้า Space เปิดทับ Bottom Bar ขึ้นมาได้
        val navigator = LocalNavigator.currentOrThrow
        var rootNavigator = navigator
        while (true) {
            val parentNavigator = rootNavigator.parent ?: break
            rootNavigator = parentNavigator
        }

        LaunchedEffect(Unit) {
            screenModel.fetchFriends()
        }

        val friends by screenModel.friends.collectAsStateWithLifecycle()
        val uiState by screenModel.uiState.collectAsStateWithLifecycle()

        FriendContent(
            friends = friends,
            isLoading = uiState.isLoading,
            errorMessage = uiState.error?.let(::friendErrorMessage),
            onRetry = { screenModel.onAction(FriendUiAction.Refresh) },
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
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onRetry: () -> Unit = {},
    onFriendClick: (String, String) -> Unit // 🌟 3. รับ Event การคลิก
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredFriends = remember(friends, searchQuery) {
        friends.filter {
            it.username?.contains(searchQuery, ignoreCase = true) == true ||
                    it.firstName?.contains(searchQuery, ignoreCase = true) == true
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(10.dp))

        SocialSearchBar(
            searchQuery = searchQuery,
            onSearchChange = { searchQuery = it }
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (isLoading && friends.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = LightPrimary)
            }
        } else if (friends.isEmpty() && errorMessage != null) {
            Box(modifier = Modifier.weight(1f).fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(errorMessage)
                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
                    ) {
                        Text("ลองใหม่", color = LightSoftWhite)
                    }
                }
            }
        } else if (friends.isEmpty()) {
            SocialEmptyState(
                title = "ยังไม่มีเพื่อน",
                description = "ใช้ปุ่มเพิ่มเพื่อนด้านบนเพื่อส่งคำขอและเริ่มเชื่อมต่อกัน",
                modifier = Modifier.weight(1f),
            )
        } else if (filteredFriends.isEmpty()) {
            SocialNoResultsState(modifier = Modifier.weight(1f))
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 24.dp)) {
                items(
                    items = filteredFriends,
                    key = { friend ->
                        friend.id ?: friend.email ?: friend.username ?: friend.hashCode()
                    },
                ) { friend ->
                    FriendListItem(
                        friend = friend,
                        onClick = {
                            val id = friend.id ?: ""
                            val name = friend.username?.takeIf { it.isNotBlank() }
                                ?: friend.firstName?.takeIf { it.isNotBlank() }
                                ?: "ไม่ระบุชื่อ"
                            onFriendClick(id, name)
                        }
                    )
                }
            }
        }

    }
}

private fun friendErrorMessage(error: AppError): String = when (error) {
    AppError.Unauthorized -> "เซสชันหมดอายุ กรุณาเข้าสู่ระบบใหม่"
    AppError.NotFound -> "ไม่พบรายชื่อเพื่อน"
    is AppError.Network -> "เชื่อมต่อเซิร์ฟเวอร์ไม่ได้ กรุณาลองใหม่"
    is AppError.Unknown -> "โหลดรายชื่อเพื่อนไม่สำเร็จ กรุณาลองใหม่"
}

package com.wealthvault.social.ui.main_social.add_friend

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_back
import com.wealthvault.core.generated.resources.ic_nav_profile
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.social.ui.components.SocialSearchBar
import com.wealthvault.domain.profile.FriendData
import com.wealthvault.domain.social.PendingFriend
import org.jetbrains.compose.resources.painterResource

class AddFriendScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<AddFriendScreenModel>()

        var searchQuery by remember { mutableStateOf("") }

        val isSearching by screenModel.isSearching.collectAsStateWithLifecycle()
        val hasSearched by screenModel.hasSearched.collectAsStateWithLifecycle()
        val searchResult by screenModel.searchResult.collectAsStateWithLifecycle()
        val addFriendSuccess by screenModel.addFriendSuccess.collectAsStateWithLifecycle()
        val pendingFriends by screenModel.pendingFriends.collectAsStateWithLifecycle()
        val uiState by screenModel.uiState.collectAsStateWithLifecycle()

        val popupMessage by screenModel.popupMessage.collectAsStateWithLifecycle()

        LaunchedEffect(screenModel) {
            screenModel.fetchPendingFriends()
        }

        // 🌟 3. จัดการตอนเพิ่มเพื่อนสำเร็จ (เพิ่มการเคลียร์ State ก่อน Pop ป้องกันบัค)
        LaunchedEffect(addFriendSuccess) {
            if (addFriendSuccess) {
                screenModel.resetAddFriendSuccess() // 👈 แนะนำให้มีฟังก์ชันนี้ใน ScreenModel ครับ
                navigator.pop()
            }
        }

        // 🌟 ครอบ Box ไว้ เพื่อให้ Popup ลอยอยู่ตรงกลางจอได้
        Box(modifier = Modifier.fillMaxSize()) {
            AddFriendContent(
                searchQuery = searchQuery,
                onSearchChange = { searchQuery = it },
                onSearchClick = { screenModel.searchUser(searchQuery.trim()) },
                searchResult = searchResult,
                hasSearched = hasSearched,
                isSearching = isSearching,
                pendingFriends = pendingFriends,
                isPendingLoading = uiState.isLoading && pendingFriends.isEmpty() && !hasSearched,
                pendingError = if (!hasSearched) uiState.error?.let(::addFriendErrorMessage) else null,
                onRetryPending = { screenModel.onAction(AddFriendUiAction.FetchPending) },
                onBackClick = { navigator.pop() },
                onAddFriendClick = { friend ->
                    friend.id?.let { targetId -> screenModel.addFriend(targetId) }
                },
                onRespondToRequest = { targetId, isAccept ->
                    screenModel.respondToFriendRequest(targetId, isAccept)
                }
            )

            // 🌟 Popup ทำมาได้ดีมากๆ ครับ มีการสั่ง clearPopupMessage ด้วย ถูกต้องเลย!
            if (popupMessage != null) {
                AlertDialog(
                    onDismissRequest = { screenModel.clearPopupMessage() },
                    title = {
                        Text(
                            text = "แจ้งเตือน",
                            color = LightPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    text = {
                        Text(
                            text = popupMessage ?: "",
                            color = Color(0xFF3A2F2A),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = { screenModel.clearPopupMessage() },
                            colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("ตกลง", color = Color.White)
                        }
                    },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }
    }
}

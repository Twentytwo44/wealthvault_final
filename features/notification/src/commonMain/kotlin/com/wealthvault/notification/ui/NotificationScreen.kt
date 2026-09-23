package com.wealthvault.notification.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.architecture.CacheFreshness
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_back
import com.wealthvault.core.generated.resources.ic_form_email_outline
import com.wealthvault.core.model.NotificationItem
import com.wealthvault.core.navigation.SharedScreen
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightMuted
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.notification.viewmodel.NotificationScreenModel
import com.wealthvault.notification.viewmodel.NotificationUiAction
import org.jetbrains.compose.resources.painterResource

class NotificationScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<NotificationScreenModel>()
        val navigator = LocalNavigator.currentOrThrow
        val addFriendScreen = rememberScreen(SharedScreen.AddFriend)
        val uiState by screenModel.uiState.collectAsStateWithLifecycle()

        NotificationContent(
            onBackClick = {
                screenModel.markAllAsReadBackground()
                navigator.pop()
            },
            onReadClick = screenModel::readNotification,
            onNavigateToAddFriend = { navigator.push(addFriendScreen) },
            notificationData = uiState.items,
            isLoading = uiState.isLoading,
            freshness = uiState.freshness,
            errorMessage = uiState.error?.let(::notificationErrorMessage),
            onRetry = { screenModel.onAction(NotificationUiAction.Refresh) },
        )
    }
}

@Composable
fun NotificationContent(
    onBackClick: () -> Unit,
    onReadClick: (String) -> Unit,
    onNavigateToAddFriend: () -> Unit,
    notificationData: List<NotificationItem>,
    isLoading: Boolean,
    freshness: CacheFreshness = CacheFreshness.Fresh,
    errorMessage: String? = null,
    onRetry: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBg)
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 24.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_common_back),
                contentDescription = "Back",
                tint = LightPrimary,
                modifier = Modifier.size(24.dp).clickable { onBackClick() },
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "การแจ้งเตือน", style = MaterialTheme.typography.titleLarge, color = LightPrimary)
        }

        if (freshness != CacheFreshness.Fresh) {
            NotificationCacheStatusBanner(freshness = freshness, isRefreshing = isLoading)
            Spacer(modifier = Modifier.height(10.dp))
        }

        when {
            isLoading && notificationData.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = LightPrimary)
                }
            }
            notificationData.isEmpty() && errorMessage != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.bodyLarge,
                            color = LightMuted,
                        )
                        Button(
                            onClick = onRetry,
                            colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
                        ) {
                            Text("ลองใหม่", color = Color.White)
                        }
                    }
                }
            }
            notificationData.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_form_email_outline),
                            contentDescription = "Empty",
                            tint = LightMuted.copy(alpha = 0.5f),
                            modifier = Modifier.size(60.dp),
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "ยังไม่มีการแจ้งเตือน",
                            style = MaterialTheme.typography.bodyLarge,
                            color = LightMuted,
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    itemsIndexed(
                        items = notificationData,
                        key = { index, item -> item.id ?: "${item.entityType}:${item.createdAt}:$index" },
                    ) { _, notification ->
                        val isRead = notification.isRead == true
                        if (notification.entityType == "FRIEND_REQUEST") {
                            InviteNotificationCard(
                                title = notification.message ?: "",
                                time = notification.createdAt ?: "",
                                isCompleted = notification.isCompleted == true,
                                isRead = isRead,
                                onNavigateToAddFriend = {
                                    onReadClick(notification.id ?: "")
                                    onNavigateToAddFriend()
                                },
                            )
                        } else {
                            StandardNotificationCard(
                                title = notification.message ?: "",
                                time = notification.createdAt ?: "",
                                isRead = isRead,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationCacheStatusBanner(
    freshness: CacheFreshness,
    isRefreshing: Boolean,
) {
    val message = when {
        isRefreshing -> "กำลังอัปเดตการแจ้งเตือน…"
        freshness == CacheFreshness.Offline -> "กำลังแสดงการแจ้งเตือนที่บันทึกไว้ ออฟไลน์อยู่"
        else -> "การแจ้งเตือนอาจเก่า แตะลองใหม่เพื่ออัปเดต"
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(LightSoftWhite, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Text(text = message, color = LightMuted, style = MaterialTheme.typography.bodySmall)
    }
}

private fun notificationErrorMessage(error: AppError): String = when (error) {
    AppError.Unauthorized -> "เซสชันหมดอายุ กรุณาเข้าสู่ระบบใหม่"
    AppError.NotFound -> "ไม่พบการแจ้งเตือน"
    is AppError.Network -> "เชื่อมต่อเซิร์ฟเวอร์ไม่ได้ กรุณาลองใหม่"
    is AppError.Unknown -> "โหลดการแจ้งเตือนไม่สำเร็จ กรุณาลองใหม่"
}

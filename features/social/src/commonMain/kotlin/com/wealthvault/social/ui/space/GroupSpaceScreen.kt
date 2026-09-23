package com.wealthvault.social.ui.space

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_down_line
import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.WealthVaultTheme
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.domain.social.GroupMessage
import com.wealthvault.social.ui.components.profile.SmartAssetDetailDialog
import com.wealthvault.social.ui.components.space.ActivityBubbleCard
import com.wealthvault.social.ui.components.space.InlineGrantAccessCard
import com.wealthvault.social.ui.components.space.SpaceFloatingMenu
import com.wealthvault.social.ui.components.space.SpaceTopBar
import com.wealthvault.social.ui.components.space.SystemAlertBubble
import com.wealthvault.social.ui.manage_shared.SharedAssetManageScreen
import com.wealthvault.social.ui.manage_shared.SharedAssetScreen
import com.wealthvault.social.ui.profile.GroupProfileScreen
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

class GroupSpaceScreen(
    private val groupId: String,
    private val groupName: String
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var rootNavigator = navigator
        while (rootNavigator.parent != null) {
            rootNavigator = rootNavigator.parent!!
        }

        val screenModel = getScreenModel<GroupSpaceScreenModel>()

        val messages by screenModel.messages.collectAsStateWithLifecycle()
        val isLoading by screenModel.isLoading.collectAsStateWithLifecycle()
        val uiState by screenModel.uiState.collectAsStateWithLifecycle()

        val reversedMessages = remember(messages) { messages.reversed() }
        val token by screenModel.accessToken.collectAsStateWithLifecycle()

        LaunchedEffect(groupId, token) {
            token?.let { currentToken ->
                screenModel.fetchMessages(groupId)
                screenModel.connectToChat(groupId, currentToken)
            }
        }

        WealthVaultTheme {
            GroupSpaceContent(
                groupId = groupId,
                groupName = groupName,
                messages = messages, // หรือ reversedMessages ตามที่ UI ต้องการ
                isLoading = isLoading,
                errorMessage = uiState.error?.let(::groupSpaceErrorMessage),
                onRetry = { screenModel.onAction(GroupSpaceUiAction.Refresh(groupId)) },
                onBackClick = { navigator.pop() },
                onShareClick = {
                    navigator.push(
                        SharedAssetScreen(
                            targetId = groupId,
                            targetName = groupName,
                            isGroup = true
                        )
                    )
                },
                onManageClick = {
                    navigator.push(
                        SharedAssetManageScreen(
                            targetId = groupId,
                            targetName = groupName,
                            isGroup = true
                        )
                    )
                },
                onMoreClick = {
                    rootNavigator.push(
                        GroupProfileScreen(
                            groupId = groupId
                        )
                    )
                },
                onGrantAccess = { targetId, itemIds ->
                    screenModel.grantAccess(groupId, targetId, itemIds)
                }
            )
        }
    }
}

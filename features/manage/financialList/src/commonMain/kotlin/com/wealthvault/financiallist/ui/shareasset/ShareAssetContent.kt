package com.wealthvault.financiallist.ui.shareasset

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_back
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.financiallist.ui.shareasset.component.AddEmailContent
import com.wealthvault.financiallist.ui.shareasset.component.FriendSelectionList
import com.wealthvault.financiallist.ui.shareasset.model.FriendTargetModel
import com.wealthvault.financiallist.ui.shareasset.model.GroupTargetModel
import com.wealthvault.financiallist.ui.shareasset.model.ShareInfo
import com.wealthvault.financiallist.ui.shareasset.model.ShareTo
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareAssetContent(
    onBackClick: () -> Unit = {},
    onPrepareUnshare: (ShareInfo, (ShareInfo) -> Unit) -> Unit = { _, _ -> },
    onNextClick: (ShareTo, List<ShareInfo>) -> Unit = { _, _ -> },
    friendData: List<FriendTargetModel> = emptyList(),
    groupData: List<GroupTargetModel> = emptyList(),
    emailData: List<ShareInfo> = emptyList(),
    isLoading: Boolean = false,
    error: AppError? = null,
    onRetryClick: () -> Unit = {},
) {
    val selectedFriends = remember { mutableStateListOf<ShareInfo>() }
    val selectedEmails = remember { mutableStateListOf<ShareInfo>() }
    val itemsToUnshare = remember { mutableStateListOf<ShareInfo>() }

    LaunchedEffect(friendData, groupData, emailData) {
        val preSelected = buildList {
            friendData.filter { it.isShared }.forEach { friend ->
                add(
                    ShareInfo(
                        name = friend.friendName,
                        userId = friend.friendId,
                        typeData = "F",
                        subText = friend.email,
                        profileUrl = friend.profile,
                        isShared = true,
                        date = friend.sharedAt,
                    ),
                )
            }
            groupData.filter { it.isShared }.forEach { group ->
                add(
                    ShareInfo(
                        name = group.groupName,
                        userId = group.groupId,
                        typeData = "G",
                        subText = "${group.memberCount}",
                        profileUrl = group.groupProfile,
                        isShared = true,
                        date = group.sharedAt,
                    ),
                )
            }
        }

        preSelected.forEach { item ->
            if (selectedFriends.none { it.userId == item.userId }) selectedFriends.add(item)
        }
        emailData.filter { it.isShared == true }.forEach { email ->
            if (selectedEmails.none { it.userId == email.userId }) selectedEmails.add(email)
        }
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showEmailSheet by remember { mutableStateOf(false) }
    var showEmailInfoTooltip by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = LightBg,
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                androidx.compose.foundation.layout.Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 12.dp, top = 20.dp),
                ) {
                    Icon(
                        painterResource(Res.drawable.ic_common_back),
                        null,
                        tint = LightPrimary,
                        modifier = Modifier.size(24.dp).clickable(onClick = onBackClick),
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("แชร์ทรัพย์สิน", style = MaterialTheme.typography.titleLarge, color = LightPrimary)
                        Text(
                            "คุณต้องการให้ใครเห็นทรัพย์สินนี้บ้าง?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = LightPrimary.copy(alpha = 0.7f),
                        )
                    }
                }
            }
        },
        bottomBar = {
            Box(modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(24.dp)) {
                Button(
                    onClick = {
                        onNextClick(
                            ShareTo(
                                friend = selectedFriends.filter { it.typeData == "F" },
                                email = selectedEmails.toList(),
                                group = selectedFriends.filter { it.typeData == "G" },
                            ),
                            itemsToUnshare.toList(),
                        )
                    },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
                ) {
                    if (isLoading) {
                        androidx.compose.material3.CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp),
                        )
                    } else {
                        Text("ยืนยัน", color = Color.White, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        },
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 24.dp)) {
            when {
                isLoading && friendData.isEmpty() && groupData.isEmpty() && emailData.isEmpty() -> {
                    ShareLoadingState()
                }

                error != null && friendData.isEmpty() && groupData.isEmpty() && emailData.isEmpty() -> {
                    ShareLoadError(error = error, onRetry = onRetryClick)
                }

                else -> {
                    if (error != null) {
                        ShareInlineError(error = error, onRetry = onRetryClick)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    ShareRecipientsContent(
                        selectedFriends = selectedFriends.toList(),
                        selectedEmails = selectedEmails.toList(),
                        showEmailInfoTooltip = showEmailInfoTooltip,
                        onDeleteFriend = { friend ->
                            if (friend.isShared == true) {
                                onPrepareUnshare(friend) { preparedItem ->
                                    if (itemsToUnshare.none { it.userId == preparedItem.userId && it.typeData == preparedItem.typeData }) {
                                        itemsToUnshare.add(preparedItem)
                                    }
                                    selectedFriends.remove(friend)
                                }
                            } else {
                                selectedFriends.remove(friend)
                            }
                        },
                        onDeleteEmail = { email ->
                            if (email.isShared == true) itemsToUnshare.add(email)
                            selectedEmails.remove(email)
                        },
                        onToggleEmailInfo = { showEmailInfoTooltip = !showEmailInfoTooltip },
                        onAddFriend = { showBottomSheet = true },
                        onAddEmail = { showEmailSheet = true },
                    )
                }
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = LightBg,
            ) {
                FriendSelectionList(
                    alreadySelected = selectedFriends.toList(),
                    friendData = friendData,
                    groupData = groupData,
                    onConfirm = { items ->
                        selectedFriends.addAll(items.filter { item -> selectedFriends.none { it.userId == item.userId } })
                        scope.launch { sheetState.hide() }
                            .invokeOnCompletion { if (!sheetState.isVisible) showBottomSheet = false }
                    },
                )
            }
        }

        if (showEmailSheet) {
            ModalBottomSheet(
                onDismissRequest = { showEmailSheet = false },
                containerColor = LightBg,
            ) {
                AddEmailContent(
                    onConfirm = { email, date, apiDate ->
                        if (email.isNotBlank() && !selectedEmails.any { it.userId == email }) {
                            selectedEmails.add(
                                ShareInfo(
                                    name = email,
                                    userId = email,
                                    date = date,
                                    apiDate = apiDate,
                                    typeData = "E",
                                    subText = email,
                                ),
                            )
                        }
                        showEmailSheet = false
                    },
                )
            }
        }
    }
}

package com.wealthvault.financiallist.ui.shareasset

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_back
import com.wealthvault.core.generated.resources.ic_common_plus
import com.wealthvault.core.generated.resources.ic_form_check
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightText
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.financiallist.ui.shareasset.component.AddEmailContent
import com.wealthvault.financiallist.ui.shareasset.component.FriendSelectionList
import com.wealthvault.financiallist.ui.shareasset.component.ShareItemWithDelete
import com.wealthvault.financiallist.ui.shareasset.model.FriendTargetModel
import com.wealthvault.financiallist.ui.shareasset.model.GroupTargetModel
import com.wealthvault.financiallist.ui.shareasset.model.ShareInfo
import com.wealthvault.financiallist.ui.shareasset.model.ShareTo
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource


@Composable
fun CustomCheckbox(isSelected: Boolean, onSelectedChange: (Boolean) -> Unit) {
    Box(
        modifier = Modifier
            .size(22.dp) // 🌟 ปรับขนาดให้มาตรฐาน
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) LightPrimary else Color.Transparent)
            .border(
                width = 2.dp,
                color = if (isSelected) LightPrimary else Color.LightGray.copy(0.5f),
                shape = RoundedCornerShape(6.dp)
            )
            .clickable { onSelectedChange(!isSelected) },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                painter = painterResource(Res.drawable.ic_form_check),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

data class ShareAssetScreen(val type: String, val id: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<ShareScreenModel>()
        val friendState by screenModel.friendState.collectAsStateWithLifecycle()
        val groupState by screenModel.groupState.collectAsStateWithLifecycle()
        val emailState by screenModel.emailState.collectAsStateWithLifecycle()
        val uiState by screenModel.uiState.collectAsStateWithLifecycle()

        LaunchedEffect(id, type) {
            screenModel.initData(id, type)
        }

        ShareAssetContent(
            onBackClick = { navigator.pop() },
            onPrepareUnshare = { itemToUnshare, onReadyCallback ->
                screenModel.prepareUnshareItem(itemToUnshare, id) { preparedItem ->
                    onReadyCallback(preparedItem)
                }
            },
            onNextClick = { shareTo, unshareList ->
                screenModel.initShareData(shareTo)
                // 💡 ตรงนี้ถ้าเป็นไปได้ แนะนำให้ใช้ท่าสังเกต isSuccess แทน
                // แต่ถ้า ScreenModel จัดการเรื่อง Loading/Debounce ไว้แล้ว ท่านี้ก็ใช้งานได้ครับ
                screenModel.submitShare(
                    id = id,
                    type = type,
                    unshareList = unshareList,
                    onSuccess = {
                        navigator.pop()
                    }
                )
            },
            friendData = friendState,
            groupData = groupState,
            emailData = emailState,
            isLoading = uiState.isLoading,
            error = uiState.error,
            onRetryClick = { screenModel.initData(id, type) },
        )
    }
}

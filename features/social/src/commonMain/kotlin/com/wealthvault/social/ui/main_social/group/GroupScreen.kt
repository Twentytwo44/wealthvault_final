package com.wealthvault.social.ui.main_social.group

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
import com.wealthvault.domain.social.GroupSummary
import com.wealthvault.social.ui.components.GroupListItem
import com.wealthvault.social.ui.components.SocialEmptyState
import com.wealthvault.social.ui.components.SocialNoResultsState
import com.wealthvault.social.ui.components.SocialSearchBar
import com.wealthvault.social.ui.space.GroupSpaceScreen

// 🌟 สร้างคลาส Screen
class GroupScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<GroupScreenModel>()

        // 🌟 1. ดึง Root Navigator เพื่อซ่อน Bottom Bar
        val navigator = LocalNavigator.currentOrThrow
        var rootNavigator = navigator
        while (true) {
            val parentNavigator = rootNavigator.parent ?: break
            rootNavigator = parentNavigator
        }

        LaunchedEffect(Unit) {
            screenModel.fetchGroups()
        }

        val groups by screenModel.groups.collectAsStateWithLifecycle()
        val uiState by screenModel.uiState.collectAsStateWithLifecycle()

        GroupContent(
            groups = groups,
            isLoading = uiState.isLoading,
            errorMessage = uiState.error?.let(::groupErrorMessage),
            onRetry = { screenModel.onAction(GroupUiAction.Refresh) },
            // 🌟 2. ส่งคำสั่งคลิกไป
            onGroupClick = { id, name ->
                rootNavigator.push(GroupSpaceScreen(groupId = id, groupName = name))
            }
        )
    }
}

@Composable
fun GroupContent(
    groups: List<GroupSummary>,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onRetry: () -> Unit = {},
    onGroupClick: (String, String) -> Unit // 🌟 เพิ่ม Parameter
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredGroups = remember(groups, searchQuery) {
        groups.filter {
            it.groupName?.contains(searchQuery, ignoreCase = true) == true
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(10.dp))
        SocialSearchBar(searchQuery = searchQuery, onSearchChange = { searchQuery = it })
        Spacer(modifier = Modifier.height(10.dp))

        if (isLoading && groups.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = LightPrimary)
            }
        } else if (groups.isEmpty() && errorMessage != null) {
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
        } else if (groups.isEmpty()) {
            SocialEmptyState(
                title = "ยังไม่มีกลุ่ม",
                description = "ใช้ปุ่มเพิ่มกลุ่มด้านบนเพื่อสร้างกลุ่มสำหรับแชร์ข้อมูลร่วมกัน",
                modifier = Modifier.weight(1f),
            )
        } else if (filteredGroups.isEmpty()) {
            SocialNoResultsState(modifier = Modifier.weight(1f))
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(
                    items = filteredGroups,
                    key = { group -> group.id ?: group.groupName ?: group.hashCode() },
                ) { group ->
                    GroupListItem(
                        group = group,
                        onClick = {
                            onGroupClick(group.id ?: "", group.groupName ?: "ไม่ระบุชื่อ")
                        }
                    )
                }
            }
        }
    }
}

private fun groupErrorMessage(error: AppError): String = when (error) {
    AppError.Unauthorized -> "เซสชันหมดอายุ กรุณาเข้าสู่ระบบใหม่"
    AppError.NotFound -> "ไม่พบกลุ่ม"
    is AppError.Network -> "เชื่อมต่อเซิร์ฟเวอร์ไม่ได้ กรุณาลองใหม่"
    is AppError.Unknown -> "โหลดกลุ่มไม่สำเร็จ กรุณาลองใหม่"
}

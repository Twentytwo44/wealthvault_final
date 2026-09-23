package com.wealthvault.social.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.WealthVaultTheme
import com.wealthvault.core.model.Money
import com.wealthvault.core.utils.formatAmount
import com.wealthvault.core.utils.formatThaiDate
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.social.ui.components.profile.ExpandableCategoryCard
import com.wealthvault.social.ui.components.profile.ProfileHeader
import com.wealthvault.social.ui.components.profile.RealItemCard
import com.wealthvault.social.ui.components.profile.SmartAssetDetailDialog
import com.wealthvault.social.ui.components.space.SpaceTopBar
import com.wealthvault.domain.social.FriendProfile
import com.wealthvault.domain.social.ItemPreview

class FriendProfileScreen(
    private val friendId: String,
    private val friendName: String
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<FriendProfileScreenModel>()

        val profileData by screenModel.profileData.collectAsStateWithLifecycle()
        val isLoading by screenModel.isLoading.collectAsStateWithLifecycle()
        val isRemoveSuccess by screenModel.isRemoveSuccess.collectAsStateWithLifecycle()
        val isSuccess by screenModel.isSuccess.collectAsStateWithLifecycle()
        val isAlreadySent by screenModel.isAlreadySent.collectAsStateWithLifecycle()
        val uiState by screenModel.uiState.collectAsStateWithLifecycle()

        var showRemoveConfirm by remember { mutableStateOf(false) }

        LaunchedEffect(friendId) {
            screenModel.fetchProfile(friendId)
        }

        // 🌟 3. อันนี้เก็บไว้เหมือนเดิม! เอาไว้รีเฟรชหน้าจอทันทีตอนที่กดยืนยันการลบแล้ว Backend ตอบกลับมาว่าสำเร็จ
        LaunchedEffect(isRemoveSuccess) {
            if (isRemoveSuccess) {
                screenModel.fetchProfile(friendId) // 🔄 รีโหลดเพื่อให้ปุ่มกลับเป็น "เพิ่มเพื่อน"

                // 💡 Pro Tip: หากหน้าจอนี้มีการ recompose บ่อยๆ แนะนำให้เพิ่มฟังก์ชันเคลียร์สถานะด้วยครับ
                // เพื่อป้องกันไม่ให้มันวิ่งเข้ามาโหลด fetchProfile() ซ้ำรัวๆ
                // screenModel.resetRemoveState()
            }
        }

        WealthVaultTheme {
            Box(modifier = Modifier.fillMaxSize()) {
                FriendProfileContent(
                    friendName = friendName,
                    profileData = profileData,
                    isLoading = isLoading,
                    errorMessage = uiState.error?.let(::friendProfileErrorMessage),
                    onRetry = { screenModel.onAction(FriendProfileUiAction.Refresh(friendId)) },
                    isSuccess = isSuccess,
                    isAlreadySent = isAlreadySent,
                    onBackClick = { navigator.pop() },
                    onRemoveFriendClick = { showRemoveConfirm = true },
                    onAddFriendClick = { screenModel.addFriend(friendId) }
                )

                // ส่วนของ AlertDialog ทำมาได้สวยงามและครอบคลุมดีแล้วครับ!
                if (showRemoveConfirm) {
                    AlertDialog(
                        onDismissRequest = { showRemoveConfirm = false },
                        containerColor = Color.White,
                        shape = RoundedCornerShape(20.dp),
                        title = { Text("ลบเพื่อน?", fontWeight = FontWeight.Bold, color = Color(0xFF3A2F2A)) },
                        text = { Text("คุณต้องการลบ $friendName ออกจากรายชื่อเพื่อนใช่หรือไม่?", color = Color.Gray) },
                        confirmButton = {
                            TextButton(onClick = {
                                showRemoveConfirm = false
                                screenModel.removeFriend(friendId) // สั่งลบ
                            }) {
                                Text("ลบออก", color = Color(0xFFE55A5A), fontWeight = FontWeight.Bold)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showRemoveConfirm = false }) {
                                Text("ยกเลิก", color = Color.Gray)
                            }
                        }
                    )
                }
            }
        }
    }
}

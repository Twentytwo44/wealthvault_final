package com.wealthvault.social.ui.main_social.form_group

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.utils.getScreenModel


class CreateGroupScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<FormGroupScreenModel>()

        val isLoading by screenModel.isLoading.collectAsStateWithLifecycle()
        val isSuccess by screenModel.isSuccess.collectAsStateWithLifecycle()

        // รับค่า errorMessage จาก ScreenModel
        val errorMessage by screenModel.errorMessage.collectAsStateWithLifecycle()

        // 🌟 1. สร้าง State สำหรับคุมการโชว์ Snackbar (แจ้งเตือนข้ามแพลตฟอร์ม)
        val snackbarHostState = remember { SnackbarHostState() }

        val allFriends by screenModel.friends.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            screenModel.fetchFriends()
        }

        LaunchedEffect(isSuccess) {
            if (isSuccess) {
                navigator.pop()
            }
        }

        // 🌟 2. ถ้ามี Error ให้โชว์ Snackbar
        LaunchedEffect(errorMessage) {
            errorMessage?.let { msg ->
                snackbarHostState.showSnackbar(message = msg)
            }
        }

        // 🌟 3. ใช้ Box ครอบทั้งหมด
        Box(modifier = Modifier.fillMaxSize()) {
            GroupFormContent(
                title = "สร้างกลุ่ม",
                buttonText = "สร้างกลุ่ม",
                availableFriends = allFriends,
                isLoading = isLoading,
                onBackClick = { navigator.pop() },
                onSaveClick = { groupName, selectedMemberIds, imageBytes ->
                    screenModel.createGroup(groupName, selectedMemberIds, imageBytes) // ยิง API สร้าง
                }

            )

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.2f))
                        .clickable(enabled = false) {},
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFC27A5A))
                }
            }

            // 🌟 4. วางตัวแสดง Snackbar ไว้ล่างสุดของจอ
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

    }
}

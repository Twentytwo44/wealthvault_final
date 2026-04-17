package com.wealthvault.social.ui.main_social.create_group

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.social.ui.main_social.form_group.CreateGroupScreenModel
import com.wealthvault.social.ui.main_social.form_group.GroupFormContent
import androidx.compose.runtime.getValue  // 🌟 สำหรับอ่านค่า (getter)
import androidx.compose.runtime.setValue  // 🌟 สำหรับเขียนค่า (setter) - ตัวนี้แหละที่หายไป

class EditGroupScreen(
    private val groupId: String,
    private val initialGroupName: String,
    private val initialImageUrl: String?,
    private val initialMemberIds: List<String>
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<CreateGroupScreenModel>()

        val isLoading by screenModel.isLoading.collectAsState()
        val isSuccess by screenModel.isSuccess.collectAsState()
        val errorMessage by screenModel.errorMessage.collectAsState()
        val allFriends by screenModel.friends.collectAsState()

        val snackbarHostState = remember { SnackbarHostState() }

        // ดึงรายชื่อเพื่อนสำหรับเลือกใน BottomSheet
        LaunchedEffect(Unit) {
            screenModel.fetchFriends()
        }

        // ถ้าอัปเดตทุกอย่างสำเร็จ ให้เด้งกลับหน้าโปรไฟล์กลุ่ม
        LaunchedEffect(isSuccess) {
            if (isSuccess) {
                navigator.pop()
            }
        }

        LaunchedEffect(errorMessage) {
            errorMessage?.let { msg ->
                snackbarHostState.showSnackbar(message = msg)
            }
        }
        var showExitDialog by remember { mutableStateOf(false) }


        Box(modifier = Modifier.fillMaxSize()) {
            GroupFormContent(
                title = "แก้ไขกลุ่ม",
                buttonText = "บันทึก",
                initialGroupName = initialGroupName,
                initialImageUrl = initialImageUrl,
                initialMemberIds = initialMemberIds,
                availableFriends = allFriends,
                isLoading = isLoading,
                onBackClick = { changed ->
                    if (changed) {
                        showExitDialog = true // ถ้าเปลี่ยน ให้โชว์ Popup
                    } else {
                        navigator.pop() // ถ้าไม่เปลี่ยน ให้ถอยเลยไม่ต้องถาม
                    }
                },
                onSaveClick = { newGroupName, currentMemberIds, imageBytes ->
                    // 🌟 1. เรียกใช้ Logic อัปเดตกลุ่มที่เราเตรียมไว้ใน Repository/DataSource
                    // โดยส่งค่าตั้งต้นไปเทียบกับค่าปัจจุบันเพื่อหาคนที่จะ เพิ่ม หรือ ลบ
                    screenModel.updateExistingGroup(
                        groupId = groupId,
                        newName = newGroupName,
                        imageBytes = imageBytes,
                        initialMemberIds = initialMemberIds,
                        currentMemberIds = currentMemberIds
                    )
                }
            )
            if (showExitDialog) {
                AlertDialog(
                    onDismissRequest = { showExitDialog = false }, // 🌟 เพิ่มบรรทัดนี้เพื่อแก้ Error
                    containerColor = Color.White,
                    shape = RoundedCornerShape(20.dp),
                    title = {
                        Text(
                            text = "ละทิ้งการเปลี่ยนแปลง?",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3A2F2A)
                        )
                    },
                    text = {
                        Text(
                            text = "ข้อมูลที่คุณกรอกไว้จะสูญหายหากคุณย้อนกลับในตอนนี้",
                            color = Color.Gray
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            showExitDialog = false
                            navigator.pop() // ยอมทิ้งข้อมูลแล้วถอยกลับ
                        }) {
                            Text("ละทิ้ง", color = Color(0xFFE55A5A), fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showExitDialog = false }) {
                            Text("ยกเลิก", color = Color.Gray)
                        }
                    }
                )
            }

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

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
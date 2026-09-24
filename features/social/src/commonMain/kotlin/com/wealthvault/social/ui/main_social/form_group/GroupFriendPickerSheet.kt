package com.wealthvault.social.ui.main_social.form_group

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.wealthvault.domain.profile.FriendData
import com.wealthvault.social.ui.main_social.components.SelectPersonItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun GroupFriendPickerSheet(
    availableFriends: List<FriendData>,
    memberIds: Set<String>,
    selectedFriendIds: Set<String>,
    themeColor: Color,
    onSelectedChange: (String, Boolean) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(selectedFriendIds.size) {
        if (selectedFriendIds.isNotEmpty()) sheetState.expand()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFFFDF7F2),
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.LightGray) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f)
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "เลือกสมาชิกเข้ากลุ่ม",
                style = androidx.compose.material3.MaterialTheme.typography.titleSmall,
                color = Color(0xFF3A2F2A),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            LazyColumn(modifier = Modifier.weight(1f)) {
                val availableToAdd = availableFriends.filter { !memberIds.contains(it.id) }
                if (availableToAdd.isEmpty()) {
                    item {
                        Text(
                            "ไม่มีรายชื่อเพื่อนที่สามารถเลือกได้",
                            color = Color.Gray,
                            modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                            textAlign = TextAlign.Center,
                            style = androidx.compose.material3.MaterialTheme.typography.bodySmall
                        )
                    }
                } else {
                    items(
                        items = availableToAdd,
                        key = { friend -> friend.id ?: friend.email ?: friend.username ?: friend.hashCode() }
                    ) { friend ->
                        SelectPersonItem(
                            friend = friend,
                            isSelected = selectedFriendIds.contains(friend.id),
                            onSelectedChange = { isSelected ->
                                friend.id?.let { onSelectedChange(it, isSelected) }
                            }
                        )
                    }
                }
            }

            Button(
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                enabled = selectedFriendIds.isNotEmpty()
            ) {
                Text(
                    "เพิ่มเข้ากลุ่ม",
                    color = Color.White,
                    style = androidx.compose.material3.MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

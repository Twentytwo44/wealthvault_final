package com.wealthvault.social.ui.main_social.form_group

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wealthvault.core.theme.LightMuted
import com.wealthvault.core.theme.LightText
import com.wealthvault.core.theme.RedErr
import com.wealthvault.domain.profile.FriendData

@Composable
internal fun GroupDeleteMemberDialog(
    friend: FriendData?,
    onDismiss: () -> Unit,
    onConfirm: (FriendData) -> Unit
) {
    if (friend == null) return
    val displayName = friend.username?.takeIf { it.isNotBlank() }
        ?: friend.firstName?.takeIf { it.isNotBlank() }
        ?: "ไม่ระบุชื่อ"

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                "ลบสมาชิก",
                style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = LightText
            )
        },
        text = {
            Text(
                text = "คุณต้องการลบ '$displayName' ออกจากกลุ่มใช่หรือไม่?",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                color = LightMuted,
                lineHeight = 22.sp
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(friend) }) {
                Text("ลบออก", color = RedErr, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("ยกเลิก", color = LightMuted, fontWeight = FontWeight.Medium)
            }
        }
    )
}

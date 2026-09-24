package com.wealthvault.financiallist.ui.shareasset

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.compose.runtime.Composable
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightText
import com.wealthvault.financiallist.ui.shareasset.component.ShareItemWithDelete
import com.wealthvault.financiallist.ui.shareasset.model.ShareInfo

@Composable
internal fun ShareRecipientsContent(
    selectedFriends: List<ShareInfo>,
    selectedEmails: List<ShareInfo>,
    showEmailInfoTooltip: Boolean,
    onDeleteFriend: (ShareInfo) -> Unit,
    onDeleteEmail: (ShareInfo) -> Unit,
    onToggleEmailInfo: () -> Unit,
    onAddFriend: () -> Unit,
    onAddEmail: () -> Unit,
) {
    SectionHeader(
        title = "เลือกเพื่อนหรือกลุ่มที่ต้องการแชร์",
        onAddClick = onAddFriend,
    )

    Card(
        modifier = Modifier.fillMaxWidth().height(260.dp),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, LightBorder.copy(0.5f)),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        if (selectedFriends.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("ยังไม่ได้เลือกเพื่อนหรือกลุ่ม", color = Color.Gray.copy(0.6f), style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(
                    items = selectedFriends,
                    key = { friend -> friend.userId.ifBlank { "${friend.typeData}:${friend.name}" } },
                ) { friend ->
                    ShareItemWithDelete(data = friend, onDelete = { onDeleteFriend(friend) })
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Box(modifier = Modifier.fillMaxWidth()) {
        Column {
            SectionHeader(
                title = "แชร์ให้คนที่ไม่มีบัญชี",
                showInfo = true,
                onInfoClick = onToggleEmailInfo,
                onAddClick = onAddEmail,
            )

            Card(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, LightBorder.copy(0.5f)),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            ) {
                if (selectedEmails.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("ยังไม่มีการเพิ่มอีเมล", color = Color.Gray.copy(0.6f), style = MaterialTheme.typography.bodyMedium)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(
                            items = selectedEmails,
                            key = { email -> email.userId.ifBlank { "${email.typeData}:${email.name}" } },
                        ) { email ->
                            ShareItemWithDelete(data = email, onDelete = { onDeleteEmail(email) })
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = showEmailInfoTooltip,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier.align(Alignment.TopCenter).zIndex(5f),
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .offset(y = 42.dp)
                    .shadow(12.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, LightPrimary.copy(alpha = 0.1f)),
            ) {
                Row(
                    modifier = Modifier.clickable(onClick = onToggleEmailInfo).padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Default.Info, null, tint = LightPrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "หากผู้รับยังไม่มีบัญชี Wealth & Vault ระบบจะส่งคำเชิญให้ผ่านอีเมลเพื่อให้เข้าถึงข้อมูลนี้ได้",
                        color = LightText.copy(0.8f),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}

package com.wealthvault.social.ui.main_social.form_group

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_plus
import com.wealthvault.core.generated.resources.ic_nav_profile
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.RedErr
import com.wealthvault.domain.profile.FriendData
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun GroupMembersContent(
    memberIds: List<String>,
    availableFriends: List<FriendData>,
    friendsLoading: Boolean,
    friendsErrorMessage: String?,
    onRetryFriends: () -> Unit,
    onAddClick: () -> Unit,
    onDeleteClick: (FriendData) -> Unit,
    themeColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "สมาชิกในกลุ่ม (${memberIds.size})",
            style = MaterialTheme.typography.titleSmall,
            color = Color(0xFF3A2F2A)
        )
        Icon(
            painter = painterResource(Res.drawable.ic_common_plus),
            contentDescription = "Add",
            tint = themeColor,
            modifier = Modifier.size(24.dp).clickable { onAddClick() }
        )
    }

    LazyColumn(modifier = modifier) {
        val currentMembers = availableFriends.filter { memberIds.contains(it.id) }
        if (friendsLoading && availableFriends.isEmpty()) {
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
                ) {
                    CircularProgressIndicator(color = themeColor)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "กำลังโหลดรายชื่อเพื่อน...",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        } else if (friendsErrorMessage != null && availableFriends.isEmpty()) {
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) {
                    Text(
                        text = friendsErrorMessage,
                        color = RedErr,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                    TextButton(onClick = onRetryFriends) {
                        Text("ลองโหลดอีกครั้ง", color = themeColor)
                    }
                }
            }
        } else if (currentMembers.isEmpty()) {
            item {
                Text(
                    "เลือกสมาชิก",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                )
            }
        } else {
            items(
                items = currentMembers,
                key = { friend -> friend.id ?: friend.email ?: friend.username ?: friend.hashCode() }
            ) { friend ->
                GroupMemberItem(friend = friend, onDeleteClick = { onDeleteClick(friend) })
            }
        }
    }
}

@Composable
fun GroupMemberItem(friend: FriendData, onDeleteClick: () -> Unit) {
    val displayName = friend.username?.takeIf { it.isNotBlank() }
        ?: friend.firstName?.takeIf { it.isNotBlank() }
        ?: "ไม่ระบุชื่อ"

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier.size(44.dp).clip(CircleShape).background(LightBg),
            contentAlignment = Alignment.Center
        ) {
            if (!friend.profile.isNullOrEmpty()) {
                AsyncImage(
                    model = friend.profile,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    painter = painterResource(Res.drawable.ic_nav_profile),
                    contentDescription = null,
                    tint = LightPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = displayName,
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF3A2F2A),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "ลบ",
            style = MaterialTheme.typography.bodyMedium,
            color = RedErr,
            modifier = Modifier.clickable { onDeleteClick() }.padding(8.dp)
        )
    }
}

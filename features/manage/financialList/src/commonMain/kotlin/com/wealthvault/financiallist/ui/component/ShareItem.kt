package com.wealthvault.financiallist.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wealthvault.core.theme.LightMuted
import com.wealthvault.share_api.model.EmailDataList
import com.wealthvault.share_api.model.FriendDataList
import com.wealthvault.share_api.model.GroupDataList
import com.wealthvault.share_api.model.ItemShareTargetsResponse


@Composable
fun ShareItem1(option: GroupDataList,) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = option.groupName ?: "",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
    }
}
@Composable
fun ShareItem2(option: FriendDataList,) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = option.userName ?: "",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
    }
}
@Composable
fun ShareItem3(option: EmailDataList,) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = option.email ?: "",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
    }
}

@Composable
fun ShareTargetList(
    label: String,
    shareTargets: ItemShareTargetsResponse
) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = LightMuted.copy(0.8f),
        letterSpacing = 0.4.sp
    )
    ExpandableCategoryCard(title = "เพื่อน", itemCount = shareTargets.friends?.size ?: 0, themeColor = "asset") {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            // 🌟 ส่ง list เข้าไปตรงๆ (ใช้ ?: emptyList() เผื่อเป็น null)
            items(shareTargets.friends ?: emptyList()) { friend ->
                ShareItem2(friend) // friend จะมี type เป็น FriendDataList
            }
        }
    }
    ExpandableCategoryCard(title = "กลุ่ม", itemCount = shareTargets.groups?.size ?: 0, themeColor = "asset") {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            // 🌟 ส่ง list เข้าไปตรงๆ (ใช้ ?: emptyList() เผื่อเป็น null)
            items(shareTargets.groups ?: emptyList()) { group ->
                ShareItem1(group) // friend จะมี type เป็น FriendDataList
            }
        }
    }
    ExpandableCategoryCard(title = "อีเมล", itemCount = shareTargets.emails?.size ?: 0, themeColor = "asset") {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            // 🌟 ส่ง list เข้าไปตรงๆ (ใช้ ?: emptyList() เผื่อเป็น null)
            items(shareTargets.emails ?: emptyList()) { email ->
                ShareItem3(email) // friend จะมี type เป็น FriendDataList
            }
        }
    }


}

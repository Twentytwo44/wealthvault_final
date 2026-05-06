package com.wealthvault.financiallist.ui.shareasset.component

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_calendar
import com.wealthvault.core.generated.resources.ic_nav_profile
import com.wealthvault.core.generated.resources.ic_nav_social
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault.core.theme.LightText
import com.wealthvault.financiallist.ui.shareasset.CustomCheckbox
import com.wealthvault.financiallist.ui.shareasset.model.FriendTargetModel
import com.wealthvault.financiallist.ui.shareasset.model.GroupTargetModel
import com.wealthvault.financiallist.ui.shareasset.model.ShareInfo
import org.jetbrains.compose.resources.painterResource

@Composable
fun FriendSelectionList(
    alreadySelected: List<ShareInfo>,
    friendData: List<FriendTargetModel>,
    groupData: List<GroupTargetModel>,
    onConfirm: (List<ShareInfo>) -> Unit
) {
    val allSelectableData = remember(friendData, groupData) {
        val friends = friendData.map {
            ShareInfo(
                name = it.friendName,
                userId = it.friendId,
                typeData = "F",
                subText = it.email,
                profileUrl = it.profile,
                isShared = it.isShared
            )
        }
        val groups = groupData.map {
            ShareInfo(
                name = it.groupName,
                userId = it.groupId,
                typeData = "G",
                subText = "${it.memberCount}",
                profileUrl = it.groupProfile,
                isShared = it.isShared
            )
        }
        friends + groups
    }

    // 🌟 ระบบจะกรองคนที่ "เคยแชร์" และ "ถูกเลือกอยู่" ออกจากลิสต์นี้ไปเลย
    val availableData = remember(allSelectableData, alreadySelected) {
        allSelectableData.filter { item ->
            alreadySelected.none { it.userId == item.userId }
        }
    }

    // 🌟 2. ลบการแอดข้อมูลลงกล่องเลือก เพราะหน้าแผ่นล่างควรเริ่มด้วยค่าว่าง
    val tempSelected = remember { mutableStateListOf<ShareInfo>() }

    var isDateChecked by remember { mutableStateOf(false) }
    var globalDate by remember { mutableStateOf<String?>(null) }
    var globalApiDate by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }


    Column(modifier = Modifier.fillMaxWidth().padding(16.dp).navigationBarsPadding()) {

        Text("เลือกเพื่อนหรือกลุ่ม", style = MaterialTheme.typography.titleMedium, color = LightPrimary)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f, fill = false),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(availableData) { item ->
                val isChecked = tempSelected.any { it.userId == item.userId }

                Surface(
                    onClick = {
                        if (isChecked) tempSelected.removeAll { it.userId == item.userId }
                        else tempSelected.add(item)
                    },
                    shape = RoundedCornerShape(16.dp),
                    color = LightSoftWhite,
                    border = BorderStroke(1.dp, LightBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(LightBg), contentAlignment = Alignment.Center) {
                            if (!item.profileUrl.isNullOrBlank()) {
                                AsyncImage(model = item.profileUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                            } else {
                                val icon = if(item.typeData == "G") painterResource(Res.drawable.ic_nav_social) else painterResource(Res.drawable.ic_nav_profile)
                                Icon(painter = icon, contentDescription = null, tint = LightPrimary, modifier = Modifier.size(24.dp))
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.name ?: "", style = MaterialTheme.typography.bodyLarge, color = LightText)

                            if (item.subText.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.background(LightBg, RoundedCornerShape(200.dp)).padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val icon = if(item.typeData == "G") painterResource(Res.drawable.ic_nav_social) else painterResource(Res.drawable.ic_nav_profile)
                                    Icon(painter = icon, contentDescription = null, tint = LightPrimary, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(item.subText, color = LightPrimary, style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }

                        CustomCheckbox(
                            isSelected = isChecked,
                            onSelectedChange = {
                                if (isChecked) tempSelected.removeAll { it.userId == item.userId }
                                else tempSelected.add(item)
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = LightBorder)
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { isDateChecked = !isDateChecked }.padding(vertical = 4.dp)
        ) {
            CustomCheckbox(
                isSelected = isDateChecked,
                onSelectedChange = { isDateChecked = it }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("ตั้งวันการแชร์ล่วงหน้า", color = LightPrimary, style = MaterialTheme.typography.bodyMedium)
        }

        AnimatedVisibility(visible = isDateChecked) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, LightBorder, RoundedCornerShape(12.dp))
                    .background(LightSoftWhite)
                    .clickable { showDatePicker = true }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = globalDate ?: "เลือกวันที่",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (globalDate != null) LightText else Color.Gray.copy(0.7f)
                )
                Icon(painterResource(Res.drawable.ic_common_calendar), contentDescription = "Select Date", tint = LightPrimary, modifier = Modifier.size(24.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val finalDate = if (isDateChecked) globalDate else null
                val finalApiDate = if (isDateChecked) globalApiDate else null
                val finalizedList = tempSelected.map { it.copy(date = finalDate, apiDate = finalApiDate) }
                onConfirm(finalizedList)
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = tempSelected.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(
                containerColor = LightPrimary,
                disabledContainerColor = Color.LightGray.copy(alpha = 0.5f),
                disabledContentColor = Color.White
            )
        ) {
            Text("เพิ่ม", color = Color.White, style = MaterialTheme.typography.titleMedium)
        }
    }

    if (showDatePicker) {
        CustomDatePickerDialog(
            onDismiss = { showDatePicker = false },
            onDateConfirm = { apiStr, thaiStr ->
                globalApiDate = apiStr
                globalDate = thaiStr
                showDatePicker = false
            }
        )
    }
}
package com.wealthvault.financiallist.ui.shareasset.component

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.wealthvault.core.theme.*
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
    // 🌟 1. ป้องกัน Error ด้วยการใช้ Named Arguments ในการ Map ข้อมูล
    val allSelectableData = remember(friendData, groupData) {
        val friends = friendData.map {
            ShareInfo(
                name = it.friendName,
                userId = it.friendId,
                typeData = "F",
                subText = it.email ?: "",
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

    val availableData = remember(allSelectableData, alreadySelected) {
        allSelectableData.filter { item ->
            alreadySelected.none { it.userId == item.userId }
        }
    }

    val tempSelected = remember { mutableStateListOf<ShareInfo>() }
    var isDateChecked by remember { mutableStateOf(false) }
    var globalDate by remember { mutableStateOf<String?>(null) }
    var globalApiDate by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 16.dp).navigationBarsPadding()) {

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
                    shape = RoundedCornerShape(12.dp),
                    color = LightSoftWhite,
                    border = BorderStroke(1.dp, LightBorder.copy(0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Profile Box ขนาด 36.dp (Master UI Style)
                        Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(LightBg), contentAlignment = Alignment.Center) {
                            if (!item.profileUrl.isNullOrBlank()) {
                                AsyncImage(model = item.profileUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                            } else {
                                val icon = if(item.typeData == "G") Res.drawable.ic_nav_social else Res.drawable.ic_nav_profile
                                Icon(painter = painterResource(icon), contentDescription = null, tint = LightPrimary, modifier = Modifier.size(18.dp))
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.name ?: "", style = MaterialTheme.typography.bodyMedium, color = LightText)

                            if (item.subText.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(
                                    modifier = Modifier
                                        .background(LightBg, RoundedCornerShape(200.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val icon = if(item.typeData == "G") Res.drawable.ic_nav_social else Res.drawable.ic_nav_profile
                                    Icon(painter = painterResource(icon), contentDescription = null, tint = LightPrimary, modifier = Modifier.size(10.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(item.subText, color = LightPrimary, style = MaterialTheme.typography.labelSmall)
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
        HorizontalDivider(color = LightBorder.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(12.dp))

        // --- ตั้งวันแชร์ล่วงหน้า ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { isDateChecked = !isDateChecked }.padding(vertical = 4.dp)
        ) {
            CustomCheckbox(
                isSelected = isDateChecked,
                onSelectedChange = { isDateChecked = it }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text("ตั้งวันการแชร์ล่วงหน้า", color = LightPrimary, style = MaterialTheme.typography.bodyMedium)
        }

        AnimatedVisibility(
            visible = isDateChecked,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp) // 🌟 บังคับสูง 44.dp
                        .background(LightSoftWhite, RoundedCornerShape(12.dp))
                        .border(1.dp, LightBorder.copy(0.5f), RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showDatePicker = true }
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = globalDate ?: "เลือกวันที่",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (globalDate != null) Color(0xFF3A2F2A) else Color.LightGray
                    )
                    Icon(
                        painter = painterResource(Res.drawable.ic_common_calendar),
                        contentDescription = null,
                        tint = LightPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- ปุ่มเพิ่ม (สูง 46.dp) ---
        Button(
            onClick = {
                val finalDate = if (isDateChecked) globalDate else null
                val finalApiDate = if (isDateChecked) globalApiDate else null
                val finalizedList = tempSelected.map { it.copy(date = finalDate, apiDate = finalApiDate) }
                onConfirm(finalizedList)
            },
            modifier = Modifier.fillMaxWidth().height(46.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = tempSelected.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(
                containerColor = LightPrimary,
                disabledContainerColor = Color.LightGray.copy(alpha = 0.5f)
            )
        ) {
            Text("เพิ่ม", color = Color.White, style = MaterialTheme.typography.bodyLarge)
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
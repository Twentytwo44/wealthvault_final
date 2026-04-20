package com.wealthvault.financiallist.ui.shareasset.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wealthvault.financiallist.ui.shareasset.model.FriendTargetModel
import com.wealthvault.financiallist.ui.shareasset.model.GroupTargetModel
import com.wealthvault.financiallist.ui.shareasset.model.ShareInfo



@Composable
fun FriendSelectionList(
    alreadySelected: List<ShareInfo>,
    friendData: List<FriendTargetModel>,
    groupData: List<GroupTargetModel>,
    onConfirm: (List<ShareInfo>) -> Unit
) {
    // 1. เตรียมข้อมูล
    val allSelectableData = remember(friendData, groupData) {
        val friends = friendData.map { ShareInfo(name = it.friendName, userId = it.friendId, typeData = "F", isShared = it.isShared) }
        val groups = groupData.map { ShareInfo(name = it.groupName, userId = it.groupId, typeData = "G", isShared = it.isShared) }
        friends + groups
    }


    val tempSelected = remember(allSelectableData) {
        mutableStateListOf<ShareInfo>().apply {
            addAll(allSelectableData.filter { it.isShared ?: false }) // == true ไม่ต้องใส่ก็ได้ครับ ละไว้ในฐานที่เข้าใจได้
        }
    }

    // 🕒 2. State สำหรับระบุว่า "กำลังแก้ปฏิทินของใครอยู่" (เก็บเป็น Object ShareInfo เลย จะได้ไม่งง)
    var editingUserDate by remember { mutableStateOf<ShareInfo?>(null) }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(
            text = "เลือกเพื่อนหรือกลุ่ม",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xFFC08064)
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f, fill = false),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(allSelectableData) { item ->
                val selectedItem = tempSelected.find { it.userId == item.userId }
                val isChecked = selectedItem != null

                Surface(
                    onClick = {
                        if (isChecked) {
                            tempSelected.removeAll { it.userId == item.userId }
                        } else {
                            tempSelected.add(item)
                            editingUserDate = item // เปิดปฏิทินทันทีที่ติ๊กเลือก
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isChecked) Color(0xFFF2E8E1) else Color(0xFFFFF8F3),
                    border = if (isChecked) BorderStroke(1.dp, Color(0xFFC08064)) else null,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(item.name ?: "", modifier = Modifier.weight(1f), fontSize = 16.sp)
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = null,
                                colors = CheckboxDefaults.colors(checkedColor = Color(0xFFC08064))
                            )
                        }

                        // แสดงกล่องปฏิทินจำลอง
                        AnimatedVisibility(visible = isChecked) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                                    .background(Color.White)
                                    .clickable { editingUserDate = selectedItem } // กดแก้ปฏิทินทีหลังได้
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val hasDate = selectedItem?.date != null
                                Text(
                                    text = if (hasDate) selectedItem.date else "ว/ด/ป",
                                    fontSize = 14.sp,
                                    color = if (hasDate) Color.Black else Color.Gray
                                )
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Select Date",
                                    tint = Color(0xFFC08064),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onConfirm(tempSelected.toList()) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC08064))
        ) {
            Text("เลือก (${tempSelected.size}) รายการ", color = Color.White)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }

    // 🕒 3. เรียกใช้งาน Custom Composable ปฏิทินแบบคลีนๆ
    if (editingUserDate != null) {
        CustomDatePickerDialog(
            onDismiss = { editingUserDate = null }, // ปิดป๊อปอัพ
            onDateConfirm = { dateStr ->
                // นำวันที่มาอัปเดตใส่ List
                val index = tempSelected.indexOfFirst { it.userId == editingUserDate?.userId }
                if (index != -1) {
                    tempSelected[index] = tempSelected[index].copy(date = dateStr)
                }
                editingUserDate = null // เซฟเสร็จแล้วปิดป๊อปอัพ
            }
        )
    }
}

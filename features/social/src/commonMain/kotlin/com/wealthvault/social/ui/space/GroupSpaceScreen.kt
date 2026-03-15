package com.wealthvault.social.ui.space

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wealthvault.social.ui.components.space.ActivityBubbleCard
import com.wealthvault.social.ui.components.space.SpaceFloatingMenu
import com.wealthvault.social.ui.components.space.SpaceTopBar

@Composable
fun GroupSpaceScreen(
    groupName: String = "Family",
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    onManageClick: () -> Unit
) {
    val themeColor = Color(0xFFC27A5A)


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 20.dp)
    ) {
        SpaceTopBar(title = groupName, onBackClick = onBackClick, showMoreOption = true)

        HorizontalDivider(color = themeColor.copy(alpha = 0.3f), thickness = 1.dp)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {

            // 1. กิจกรรมคนอื่นๆ ในกลุ่ม
            item {
                ActivityBubbleCard(
                    title = "ธนพล เหรียญดี ได้แชร์ทรัพย์สินนี้กับคุณ",
                    assetName = "บัญชีเงินเก็บเพื่อเกษียณ...",
                    assetType = "บัญชีเงินฝาก",
                    showAvatar = true,
                    themeColor = themeColor
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 2. มีคนใหม่เข้ากลุ่ม (พร้อมกล่องถามแชร์ Asset)
            item {
                Text(
                    text = "Twentytwo ได้เข้าร่วมกลุ่ม",
                    fontSize = 12.sp,
                    color = themeColor,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))

                // กล่องพิเศษสำหรับกลุ่ม (ถามแชร์ Asset ย้อนหลัง)
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, themeColor.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "ต้องการแชร์ ic_nav_asset ของคุณให้ Twentytwo ในช่วงก่อนที่ Twentytwo จะเข้ากลุ่มหรือไม่",
                            fontSize = 14.sp,
                            color = Color(0xFF3A2F2A),
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // รายการ Asset ให้เลือกแชร์
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = false, onCheckedChange = {})
                            Text("Asset 0", fontSize = 14.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = true,
                                onCheckedChange = {},
                                colors = CheckboxDefaults.colors(checkedColor = themeColor)
                            )
                            Text("Asset 1", fontSize = 14.sp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "เลือกทั้งหมด", fontSize = 12.sp, color = themeColor)
                            Button(
                                onClick = { /* TODO */ },
                                colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text("บันทึก", fontSize = 12.sp)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 3. กิจกรรมของเราที่แชร์เข้ากลุ่ม
            item {
                ActivityBubbleCard(
                    title = "คุณได้แชร์ทรัพย์สินนี้กับ $groupName...",
                    assetName = "เก็บเงินซื้อเกม",
                    assetType = "บัญชีเงินฝาก",
                    showAvatar = false,
                    themeColor = themeColor
                )
                Spacer(modifier = Modifier.height(100.dp)) // ดันหนีปุ่ม FAB
            }
        }
    }

}
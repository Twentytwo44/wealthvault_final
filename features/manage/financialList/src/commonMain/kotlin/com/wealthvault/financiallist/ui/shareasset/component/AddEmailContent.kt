package com.wealthvault.financiallist.ui.shareasset.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_calendar
import com.wealthvault.core.theme.*
import com.wealthvault.financiallist.ui.shareasset.CustomCheckbox
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEmailContent(
    onConfirm: (email: String, date: String?, apiDate: String?) -> Unit
) {
    var emailText by remember { mutableStateOf("") }
    var isDateChecked by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<String?>(null) }
    var selectedApiDate by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp).navigationBarsPadding()) {

        Text("เพิ่มอีเมลผู้รับ", style = MaterialTheme.typography.titleMedium, color = LightPrimary)
        Text("ระบบจะส่งคำเชิญไปยังอีเมลนี้เพื่อให้เข้าถึงข้อมูล", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Spacer(modifier = Modifier.height(20.dp))

        // 🌟 Email Input แบบ BasicTextField (สูง 44.dp) ตาม Master UI
        Column {
            Text(text = "อีเมล", style = MaterialTheme.typography.bodyMedium, color = LightPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            BasicTextField(
                value = emailText,
                onValueChange = { emailText = it },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF3A2F2A)),
                cursorBrush = SolidColor(LightPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(LightSoftWhite, RoundedCornerShape(12.dp))
                            .border(
                                width = 1.dp,
                                color = LightBorder.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            if (emailText.isEmpty()) {
                                Text(
                                    text = "example@gmail.com",
                                    color = Color.LightGray,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                            innerTextField()
                        }
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🌟 ส่วนตั้งวันการแชร์ล่วงหน้า
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
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp) // 🌟 สูง 44.dp มาตรฐาน
                        .background(LightSoftWhite, RoundedCornerShape(12.dp))
                        .border(
                            width = 1.dp,
                            color = LightBorder.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showDatePicker = true }
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedDate ?: "เลือกวันที่",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (selectedDate != null) Color(0xFF3A2F2A) else Color.LightGray
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

        Spacer(modifier = Modifier.height(32.dp))

        // 🌟 ปุ่มเพิ่ม (สูง 46.dp)
        Button(
            onClick = {
                val finalDate = if (isDateChecked) selectedDate else null
                val finalApiDate = if (isDateChecked) selectedApiDate else null
                onConfirm(emailText, finalDate, finalApiDate)
            },
            enabled = emailText.contains("@") && emailText.contains("."),
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
            shape = RoundedCornerShape(12.dp),
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
                selectedApiDate = apiStr
                selectedDate = thaiStr
                showDatePicker = false
            }
        )
    }
}
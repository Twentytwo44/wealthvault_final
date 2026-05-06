package com.wealthvault.financiallist.ui.shareasset.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_calendar
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault.core.theme.LightText
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
        Text("ระบบจะส่งคำเชิญไปยังอีเมลนี้เพื่อให้เข้าถึงข้อมูล", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Spacer(modifier = Modifier.height(20.dp))

        Column {
            Text(text = "อีเมล", style = MaterialTheme.typography.bodyMedium, color = LightPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = emailText,
                onValueChange = { emailText = it },
                modifier = Modifier.fillMaxWidth().border(1.dp, LightBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                placeholder = { Text("example@gmail.com", color = Color.LightGray) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = LightSoftWhite,
                    unfocusedContainerColor = LightSoftWhite,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = LightText
                )
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

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
                    text = selectedDate ?: "เลือกวันที่",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (selectedDate != null) LightText else Color.Gray.copy(0.7f)
                )
                Icon(painterResource(Res.drawable.ic_common_calendar), contentDescription = null, tint = LightPrimary, modifier = Modifier.size(24.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val finalDate = if (isDateChecked) selectedDate else null
                val finalApiDate = if (isDateChecked) selectedApiDate else null
                onConfirm(emailText, finalDate, finalApiDate)
            },
            enabled = emailText.contains("@") && emailText.contains("."),
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = LightPrimary)
        ) {
            Text("เพิ่ม", color = Color.White, style = MaterialTheme.typography.titleMedium)
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
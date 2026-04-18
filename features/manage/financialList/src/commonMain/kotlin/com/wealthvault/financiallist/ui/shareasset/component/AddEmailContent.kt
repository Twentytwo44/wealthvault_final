package com.wealthvault.financiallist.ui.shareasset.component

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEmailContent(
    onConfirm: (email: String, date: String?) -> Unit
) {
    var emailText by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<String?>(null) }

    // 🕒 State ควบคุมการเปิด/ปิด ปฏิทิน
    var showDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .navigationBarsPadding() // เว้นพื้นที่คีย์บอร์ด
    ) {
        Text(
            "เพิ่มอีเมลผู้รับ",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFC08064) // WealthVaultBrown
        )
        Text(
            "ระบบจะส่งคำเชิญไปยังอีเมลนี้เพื่อให้เข้าถึงข้อมูล",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 1. ช่องกรอกอีเมล
        OutlinedTextField(
            value = emailText,
            onValueChange = { emailText = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("อีเมล (example@gmail.com)") },
            placeholder = { Text("ระบุอีเมลผู้รับ") },
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFC08064),
                focusedLabelColor = Color(0xFFC08064),
                cursorColor = Color(0xFFC08064)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. กล่องจำลองสำหรับกดเลือกวันที่
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(
                    1.dp,
                    if (selectedDate != null) Color(0xFFC08064) else Color.Gray,
                    RoundedCornerShape(16.dp)
                )
                .clickable { showDatePicker = true } // เปิดปฏิทิน
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedDate ?: "เลือกวันที่ (ว/ด/ป)",
                fontSize = 16.sp,
                color = if (selectedDate != null) Color.Black else Color.Gray
            )
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Select Date",
                tint = Color(0xFFC08064)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 3. ปุ่มเพิ่มรายชื่อ
        Button(
            onClick = { onConfirm(emailText, selectedDate) },
            enabled = emailText.contains("@") && emailText.contains(".") && selectedDate != null,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFC08064),
                disabledContainerColor = Color(0xFFD7CCC8)
            )
        ) {
            Text("เพิ่มรายชื่อ", color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    // 🕒 4. เรียกใช้ CustomDatePickerDialog แยกออกมาให้โค้ดคลีน
    if (showDatePicker) {
        CustomDatePickerDialog(
            onDismiss = { showDatePicker = false },
            onDateConfirm = { dateStr ->
                selectedDate = dateStr // รับวันที่มาใส่ State
                showDatePicker = false // ปิดป๊อปอัพ
            }
        )
    }
}

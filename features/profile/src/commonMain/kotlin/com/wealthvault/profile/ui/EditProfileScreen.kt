package com.wealthvault.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_back
import com.wealthvault.core.generated.resources.ic_common_calendar
import com.wealthvault.core.generated.resources.ic_common_pen // 🌟 ดึงไอคอนดินสอมาใช้
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    val themeColor = Color(0xFFC27A5A)

    // State สำหรับเก็บข้อมูลที่พิมพ์
    var firstName by remember { mutableStateOf("Twentytwo01") }
    var lastName by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("13/08/2549") }
    var phone by remember { mutableStateOf("") }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        // --- Header ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_common_back),
                contentDescription = "Back",
                tint = themeColor,
                modifier = Modifier.size(24.dp).clickable { onBackClick() }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "แก้ไขโปรไฟล์", style = MaterialTheme.typography.titleLarge, color = themeColor)
        }

        // --- Profile Picture Edit ---
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                // รูปโปรไฟล์
                Box(modifier = Modifier.size(100.dp).clip(CircleShape).background(Color(0xFFE0DCDA)))
                // ไอคอนดินสอ
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .offset(x = (-4).dp, y = (-4).dp)
                        .clip(CircleShape)
                        .background(themeColor)
                        .clickable { /* TODO: เลือกรูปใหม่ */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_common_pen),
                        contentDescription = "Edit",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "nptwosudinw@gmail.com", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- Forms ---
        ProfileTextField(label = "ชื่อจริง", value = firstName, onValueChange = { firstName = it })
        Spacer(modifier = Modifier.height(16.dp))
        ProfileTextField(label = "นามสกุล", value = lastName, onValueChange = { lastName = it })
        Spacer(modifier = Modifier.height(16.dp))

        // 🌟 ช่องวันเกิด สมมติว่ามีไอคอนปฏิทินต่อท้าย
        ProfileTextField(
            label = "วันเกิด",
            value = birthDate,
            onValueChange = { birthDate = it },
            trailingIcon = {
                Icon(
                    painter = painterResource(Res.drawable.ic_common_calendar), // 🌟 ดึงไอคอนปฏิทิน
                    contentDescription = "Calendar",
                    tint = themeColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        ProfileTextField(label = "เบอร์โทร", value = phone, onValueChange = { phone = it })

        Spacer(modifier = Modifier.weight(1f)) // ดันปุ่มเซฟลงไปล่างสุด

        // --- Save Button ---
        Button(
            onClick = onSaveClick,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = themeColor)
        ) {
            Text("บันทึก", color = Color.White, style = MaterialTheme.typography.titleMedium)
        }
    }
}

// 🌟 Component ย่อยสำหรับช่องกรอกข้อมูล
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Column {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = Color(0xFFC27A5A))
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            // 🌟 แก้ชื่อเป็น TextFieldDefaults.colors() และใช้ focused/unfocused ContainerColor ครับ
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            trailingIcon = trailingIcon
        )
    }
}
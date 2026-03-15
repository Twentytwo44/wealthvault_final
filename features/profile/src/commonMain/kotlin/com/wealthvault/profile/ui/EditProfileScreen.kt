package com.wealthvault.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.CalendarMonth
//import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wealthvault.profile.ui.components.ClosePersonItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    val themeColor = Color(0xFFC27A5A)
    val bgColor = Color(0xFFFFF8F3)

    // State สำหรับเก็บข้อมูลที่กรอก
    var username by remember { mutableStateOf("Twentytwo01") }
    var email by remember { mutableStateOf("nptwosudinw@gmail.com") }
    var birthDate by remember { mutableStateOf("13/08/2549") }
    var isShareEnabled by remember { mutableStateOf(true) }


        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp)
        ) {
            item {
                // --- Top Bar ---
                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Icon(
//                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                        contentDescription = "Back",
//                        tint = themeColor,
//                        modifier = Modifier.size(24.dp).clickable { onBackClick() }
//                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = "ตั้งค่าโปรไฟล์",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium, color = themeColor)
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                // --- Edit Profile Info ---
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // รูปโปรไฟล์ + ไอคอน Edit
                    Box(modifier = Modifier.size(100.dp)) {
                        Box(modifier = Modifier.fillMaxSize().clip(CircleShape).background(Color(0xFFE0DCDA)))
                        // ปุ่มแก้ไขรูป มุมขวาล่าง
                        Surface(
                            shape = CircleShape,
                            color = themeColor,
                            modifier = Modifier.align(Alignment.BottomEnd).size(28.dp).offset(x = (-4).dp, y = (-4).dp)
                        ) {
//                            Icon(Icons.Default.Edit, contentDescription = "Edit Image", tint = Color.White, modifier = Modifier.padding(6.dp))
                        }
                    }

                    Spacer(modifier = Modifier.width(24.dp))

                    // TextField สำหรับกรอกข้อมูล (ทำแบบมีแค่เส้นใต้)
                    Column(modifier = Modifier.weight(1f)) {
                        UnderlinedTextField(value = username, onValueChange = { username = it })
                        Spacer(modifier = Modifier.height(12.dp))
                        UnderlinedTextField(value = email, onValueChange = { email = it })
                        Spacer(modifier = Modifier.height(12.dp))
                        UnderlinedTextField(
                            value = birthDate,
                            onValueChange = { birthDate = it },
//                            trailingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Calendar", tint = themeColor, modifier = Modifier.size(16.dp)) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
                HorizontalDivider(color = themeColor.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                // --- Toggle Switch ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "แชร์สินทรัพย์ทั้งหมดให้คนใกล้ชิดตามกำหนด", fontSize = 12.sp, color = Color(0xFF3A2F2A), modifier = Modifier.weight(1f))
                    Switch(
                        checked = isShareEnabled,
                        onCheckedChange = { isShareEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = themeColor,
                            checkedTrackColor = themeColor.copy(alpha = 0.3f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- Age Limit ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "เปิดให้เห็นสินทรัพย์เมื่อถึงอายุ", fontSize = 12.sp, color = Color(0xFF3A2F2A))
                    Text(text = "80 ปี", fontSize = 14.sp, color = themeColor) // ในอนาคตสามารถกดแก้ได้
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                // --- Close People List (Editable) ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "คนใกล้ชิด", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF3A2F2A))
//                    Icon(Icons.Default.Add, contentDescription = "Add Person", tint = themeColor, modifier = Modifier.clickable { /* TODO */ })
                }
                Spacer(modifier = Modifier.height(16.dp))

                ClosePersonItem(name = "Nai", showDelete = true, onDeleteClick = { /* TODO */ })

                Spacer(modifier = Modifier.height(80.dp)) // ดันหนีปุ่ม Save
            }
        }

}

// ----------------------------------------------------
// Helper Component สำหรับช่องกรอกข้อมูลแบบมีแค่เส้นใต้
// ----------------------------------------------------
@Composable
fun UnderlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    BasicTextFieldWithUnderline(value, onValueChange, trailingIcon)
}

@Composable
private fun BasicTextFieldWithUnderline(
    value: String,
    onValueChange: (String) -> Unit,
    trailingIcon: @Composable (() -> Unit)?
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = value, fontSize = 14.sp, color = Color(0xFF3A2F2A))
            if (trailingIcon != null) trailingIcon()
        }
        HorizontalDivider(color = Color(0xFFC27A5A).copy(alpha = 0.5f))
    }
}
package com.wealthvault.financiallist.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// TODO: เปลี่ยนสีพื้นหลังให้ตรงกับ Theme ของคุณ (เช่น LightBg)
val LightBg = Color(0xFFFFF8F3)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialListTemplate(
    headerTitle: String,
    themeColor: Color, // สีหลักของหน้า (ส้ม สำหรับทรัพย์สิน, แดง สำหรับหนี้สิน)
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onAddClick: () -> Unit,
    headerIcon: @Composable () -> Unit, // ช่องสำหรับใส่ Icon หัวข้อ
    content: @Composable () -> Unit // ช่องว่างตรงกลางสำหรับใส่รายการข้อมูล (LazyColumn)
) {
    Scaffold(
        containerColor = LightBg, // สีพื้นหลังของหน้าจอ
        floatingActionButton = {
            // ปุ่มบวก (+) มุมขวาล่าง
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = themeColor, // ใช้สีตามโหมดที่ส่งเข้ามา
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 80.dp) // ดันขึ้นหนี Bottom Navigation ของแอปหลัก
            ) {
//                Icon(
//                    imageVector = Icons.Default.Add,
//                    contentDescription = "Add",
//                    modifier = Modifier.size(32.dp)
//                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // --- 1. ส่วนหัวข้อ (Header) ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // วาด Icon ที่ส่งเข้ามา
                headerIcon()

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = headerTitle,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = themeColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- 2. ช่องค้นหา (Search Bar) ---
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                placeholder = {
                    Text(text = "ค้นหาด้วยชื่อ", color = Color.Gray, fontSize = 14.sp)
                },
                leadingIcon = {
//                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = Color.Gray)
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = themeColor, // ขอบตอนกดเป็นสีตามโหมด
                    unfocusedBorderColor = Color(0xFFE5E5E5), // ขอบตอนปกติสีเทาอ่อน
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- 3. พื้นที่สำหรับใส่ข้อมูลลิสต์ ---
            // โค้ดส่วนนี้จะดึง UI จากหน้า Asset หรือ Debt มาแสดง
            Box(modifier = Modifier.weight(1f)) {
                content()
            }
        }
    }
}
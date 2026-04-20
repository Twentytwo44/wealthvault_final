package com.wealthvault_final.`financial-asset`.ui.realestate

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault_final.`financial-asset`.ui.realestate.building.BuildingFormScreen


class RealEstateScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        RealEstateContent(
            onBackClick = { navigator.pop() },
            onClickToLand = { navigator.push(BuildingFormScreen()) },
            onClickToBuilding = { navigator.push(BuildingFormScreen()) }
        )

    }
}
@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun RealEstateContent(
    onBackClick: () -> Unit = {},
    onClickToLand: () -> Unit = {
    },
    onClickToBuilding :() -> Unit = {
    },
) {
    // 🎨 กำหนดสีตาม Theme ในรูปภาพ
    val primaryColor = Color(0xFFC08064) // สีน้ำตาลส้ม (ปุ่ม, ตัวหนังสือหลัก)
    val backgroundColor = Color(0xFFFCF6F2) // สีพื้นหลังครีมอ่อน
    val borderColor = Color(0xFFF0DFD3) // สีขอบกล่อง Dropdown

    Scaffold(
        containerColor = Color(0xFFFFF8F3),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                ),
                title = {
                    Text(
                        "ข้อมูลอสังหาริมทรัพย์",
                        color = Color(0xFF8D6E63),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color(0xFF8D6E63))
                    }
                }
            )
        }
    ) {
        paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(horizontal = 24.dp, vertical = 32.dp) // Padding รอบหน้าจอ
        ) {
           
            Text(
                text = "ประเภทอสังหาริมทรัพย์",
                fontSize = 14.sp,
                color = primaryColor,
                modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
            )

            // 🔹 3. Custom Dropdown (กดแล้วขยายตัวในกล่องเดิม)
            var expanded by remember { mutableStateOf(true) } // ในรูปคือเปิดอยู่ เลยตั้ง Default = true
            val options = listOf("บ้าน ตึก อาคาร", "ที่ดิน")
            var selectedOption by remember { mutableStateOf(options[0]) }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .animateContentSize() // เพิ่ม Animation ให้ตอนกาง/หุบ ดูนุ่มนวล
            ) {
                // ส่วนหัวของ Dropdown (โชว์ตลอดเวลา)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedOption,
                        color = Color(0xFF333333),
                        fontSize = 16.sp
                    )
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Dropdown Arrow",
                        tint = primaryColor
                    )
                }

                // ส่วนรายการที่ดรอปลงมา (โชว์เมื่อ expanded = true)
                if (expanded) {
                    options.forEach { option ->
                        Text(
                            text = option,
                            color = Color(0xFF333333),
                            fontSize = 16.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedOption = option // อัปเดตค่าที่เลือก
                                    expanded = false // เลือกเสร็จให้หุบเก็บ
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp)) // เผื่อที่ด้านล่างนิดนึงให้สวยงาม
                }
            }

            // 🔹 4. ดันเนื้อหาที่เหลือลงไปด้านล่างสุด
            Spacer(modifier = Modifier.weight(1f))

            // 🔹 5. ปุ่ม "ต่อไป"
            Button(
                onClick = {
                    if (selectedOption == "บ้าน ตึก อาคาร") {
                        onClickToBuilding()
                    } else if (selectedOption == "ที่ดิน") {
                        onClickToLand()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "ต่อไป",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    }

}

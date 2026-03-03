package com.wealthvault_final.`financial-asset`.ui.stock

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetInputForm(
    onBackClick: () -> Unit = {},
    onNextClick: () -> Unit = {}
) {
    Scaffold(
        containerColor = Color(0xFFFFF8F3), //  ใช้ containerColor แทน backgroundColor
        topBar = {
            CenterAlignedTopAppBar( //  TopAppBar แบบจัดกลาง
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                ),
                title = {
                    Text(
                        "ข้อมูลหุ้น กองทุน",
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 1. ส่วนกรอกข้อมูลหลัก
            TextField(label = "ชื่อหุ้น กองทุน*", placeholder = "กรอกชื่อ")
            TextField(label = "จำนวนหุ้น กองทุน*", placeholder = "0.00")
            TextField(label = "ราคาที่ซื้อ*", placeholder = "0.00")
            TextField(label = "คำอธิบาย", placeholder = "รายละเอียดเพิ่มเติม", isMultiLine = true)

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Reference Section
            ReferenceSection()

            Spacer(modifier = Modifier.height(40.dp))

            // 3. ปุ่ม "ต่อไป" แบบ
            Button(
                onClick = onNextClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFB08968) //  ใช้ containerColor
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("ต่อไป", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun TextField(label: String, placeholder: String, isMultiLine: Boolean = false) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = label, color = Color(0xFFBCAAA4), style = MaterialTheme.typography.bodySmall)
        OutlinedTextField(
            value = "",
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .then(if (isMultiLine) Modifier.height(120.dp) else Modifier),
            placeholder = { Text(placeholder, color = Color.LightGray) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color(0xFFB08968),
                unfocusedBorderColor = Color(0xFFE0E0E0)
            )
        )
    }
}

@Composable
fun ReferenceSection() {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("เพิ่มข้อมูลอ้างอิง", color = Color(0xFF8D6E63), fontWeight = FontWeight.Medium)
            Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF8D6E63), modifier = Modifier.size(28.dp))
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            repeat(3) {
                Box(modifier = Modifier.padding(end = 12.dp).size(80.dp)) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                    ) {
                        Icon(Icons.Default.Place, contentDescription = null, tint = Color(0xFFD7CCC8), modifier = Modifier.padding(24.dp))
                    }
                    //ปุ่มลบ
                    Surface(
                        modifier = Modifier.align(Alignment.TopEnd).offset(x = 4.dp, y = (-4).dp).size(20.dp),
                        shape = CircleShape,
                        color = Color(0xFFE57373)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.padding(4.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // PDF Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE0E0E0))
        ) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Place, contentDescription = null, tint = Color(0xFF8D6E63))
                Spacer(modifier = Modifier.width(8.dp))
                Text("record.pdf", modifier = Modifier.weight(1f), fontSize = 14.sp)
                Icon(Icons.Default.Close, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(18.dp))
            }
        }
    }
}

package com.wealthvault_final.`financial-asset`.ui.insurance

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wealthvault_final.`financial-asset`.Imagepicker.Attachment
import com.wealthvault_final.`financial-asset`.Imagepicker.rememberFilePicker
import com.wealthvault_final.`financial-asset`.ui.components.AssetTextField
import com.wealthvault_final.`financial-asset`.ui.components.ReferenceImagepicker


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsuranceInputForm(
    onBackClick: () -> Unit = {},
    onNextClick: () -> Unit = {}
) {
    // 1. สร้าง State เก็บรายการไฟล์ที่เลือกมา
    val attachments = remember { mutableStateListOf<Attachment>() }

    // 2. เรียกใช้ File Picker แบบ Native
    val filePicker = rememberFilePicker { newFiles ->
        attachments.addAll(newFiles)
    }

    Scaffold(
        containerColor = Color(0xFFFFF8F3),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                ),
                title = {
                    Text(
                        "ข้อมูลประกัน",
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

            // ส่วนกรอกข้อมูลหลัก
            AssetTextField(label = "ประเภทประกัน*", placeholder = "")
            AssetTextField(label = "เลขกรมธรรม์*", placeholder = "")
            AssetTextField(label = "บริษัทประกัน*", placeholder = "")
            AssetTextField(label = "ระยะเวลาคุ้มครอง", placeholder = "")
            AssetTextField(label = "วงเงินคุ้มครอง", placeholder = "")
            AssetTextField(label = "วันหมดอายุความคุ้มครอง", placeholder = "")
            AssetTextField(label = "วันที่เริ่มสัญญา", placeholder = "")
            AssetTextField(label = "คำอธิบาย", placeholder = "", isMultiLine = true)

            Spacer(modifier = Modifier.height(24.dp))

            // 3. ส่ง State และคำสั่ง Launch เข้าไปใน ReferenceSection
            ReferenceImagepicker(
                attachments = attachments,
                onAddImage = { filePicker.launchImage() },
                onAddPdf = { filePicker.launchPdf() },
                onRemove = { item -> attachments.remove(item) }
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = onNextClick, // อนาคตสามารถโยนค่า attachments ไปให้ ViewModel ตรงนี้ได้เลย
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFB08968)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("ต่อไป", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}



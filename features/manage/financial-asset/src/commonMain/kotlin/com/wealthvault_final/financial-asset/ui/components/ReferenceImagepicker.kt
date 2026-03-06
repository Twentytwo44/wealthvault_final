package com.wealthvault_final.`financial-asset`.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.wealthvault_final.`financial-asset`.Imagepicker.Attachment
import com.wealthvault_final.`financial-asset`.Imagepicker.AttachmentType


@Composable
fun ReferenceImagepicker(
    attachments: List<Attachment>,
    onAddImage: () -> Unit,
    onAddPdf: () -> Unit,
    onRemove: (Attachment) -> Unit
) {
    val images = attachments.filter { it.type == AttachmentType.IMAGE }
    val pdfs = attachments.filter { it.type == AttachmentType.PDF }

    // สร้าง State สำหรับควบคุมการเปิด/ปิดเมนู Dropdown
    var expanded by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("เพิ่มข้อมูลอ้างอิง", color = Color(0xFF8D6E63), fontWeight = FontWeight.Medium)

            // ใช้ Box เพื่อให้ DropdownMenu ลอยอยู่ตำแหน่งเดียวกับปุ่ม +
            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "เพิ่ม",
                        tint = Color(0xFF8D6E63),
                        modifier = Modifier.size(28.dp)
                    )
                }

                // เมนูที่จะกางออกมาเมื่อ expanded = true
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    // ตัวเลือกที่ 1: รูปภาพ
                    DropdownMenuItem(
                        text = { Text("รูปภาพ", color = Color(0xFF8D6E63)) },
                        leadingIcon = {
                            Icon(Icons.Default.Image, contentDescription = null, tint = Color(0xFF8D6E63))
                        },
                        onClick = {
                            expanded = false
                            onAddImage() // เรียกฟังก์ชันเปิดรูป
                        }
                    )

                    // ตัวเลือกที่ 2: ไฟล์ PDF
                    DropdownMenuItem(
                        text = { Text("ไฟล์ PDF", color = Color(0xFF8D6E63)) },
                        leadingIcon = {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color(0xFF8D6E63))
                        },
                        onClick = {
                            expanded = false
                            onAddPdf() // เรียกฟังก์ชันเปิด PDF
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // แถวรูปภาพ (แสดงเมื่อมีรูปภาพเท่านั้น)
        if (images.isNotEmpty()) {
            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                images.forEach { img ->
                    Box(modifier = Modifier.padding(end = 12.dp).size(80.dp)) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                        ) {
                            Icon(Icons.Default.Image, contentDescription = null, tint = Color(0xFFD7CCC8), modifier = Modifier.padding(24.dp))
                        }
                        // ปุ่มกากบาทสำหรับลบ
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 4.dp, y = (-4).dp)
                                .size(20.dp)
                                .clickable { onRemove(img) },
                            shape = CircleShape,
                            color = Color(0xFFE57373)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "ลบ", tint = Color.White, modifier = Modifier.padding(4.dp))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // แถว PDF (แสดงเรียงกันลงมา)
        pdfs.forEach { pdf ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0))
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF8D6E63))
                    Spacer(modifier = Modifier.width(8.dp))

                    Text(pdf.name, modifier = Modifier.weight(1f), fontSize = 14.sp)

                    Icon(
                        Icons.Default.Close,
                        contentDescription = "ลบ",
                        tint = Color.LightGray,
                        modifier = Modifier
                            .size(18.dp)
                            .clickable { onRemove(pdf) }
                    )
                }
            }
        }
    }
}

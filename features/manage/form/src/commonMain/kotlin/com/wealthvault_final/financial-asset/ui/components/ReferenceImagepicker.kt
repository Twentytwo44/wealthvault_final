package com.wealthvault_final.`financial-asset`.ui.components

// 🌟 Import Theme ของแอป

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
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault.core.theme.LightText
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
            // 🌟 ปรับสีและฟอนต์ของหัวข้อให้ตรงกับ Theme
            Text(
                text = "เพิ่มข้อมูลอ้างอิง",
                color = LightPrimary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "เพิ่ม",
                        tint = LightPrimary, // 🌟 ใช้ LightPrimary
                        modifier = Modifier.size(28.dp)
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(LightSoftWhite) // 🌟 ปรับพื้นหลังเมนู
                ) {
                    DropdownMenuItem(
                        text = {
                            Text("รูปภาพ", color = LightText, style = MaterialTheme.typography.bodyMedium)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                tint = LightPrimary, // 🌟 ปรับสีไอคอนให้ละมุน
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        onClick = {
                            expanded = false
                            onAddImage()
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Text("ไฟล์ PDF", color = LightText, style = MaterialTheme.typography.bodyMedium)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = null,
                                tint = Color(0xFFE57373), // 🌟 สีแดง PDF (คงไว้)
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        onClick = {
                            expanded = false
                            onAddPdf()
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- แถวรูปภาพ ---
        if (images.isNotEmpty()) {
            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                images.forEach { img ->
                    Box(modifier = Modifier.padding(end = 12.dp, top = 4.dp).size(80.dp)) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            shape = RoundedCornerShape(12.dp), // 🌟 ปรับความโค้งเป็น 12.dp ให้เข้ากับ Input
                            color = LightSoftWhite, // 🌟 ใช้สีพื้นหลังของแอป
                            border = BorderStroke(1.dp, LightBorder.copy(alpha = 0.5f)) // 🌟 กรอบจางๆ
                        ) {
                            val imageBytes = img.platformData as? ByteArray

                            if (imageBytes != null) {
                                AsyncImage(
                                    model = imageBytes,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    Icons.Default.Image,
                                    contentDescription = null,
                                    tint = Color.LightGray,
                                    modifier = Modifier.padding(24.dp)
                                )
                            }
                        }

                        // ปุ่มกากบาท
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 6.dp, y = (-6).dp)
                                .size(22.dp)
                                .clickable { onRemove(img) },
                            shape = CircleShape,
                            color = Color(0xFFE57373) // สีแดงเตือนสำหรับการลบ
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "ลบ",
                                tint = Color.White,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // --- แถว PDF ---
        pdfs.forEach { pdf ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(12.dp), // 🌟 โค้ง 12.dp
                colors = CardDefaults.cardColors(containerColor = LightSoftWhite), // 🌟 สีพื้นหลัง
                border = BorderStroke(1.dp, LightBorder.copy(alpha = 0.5f)) // 🌟 กรอบบางๆ
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = null,
                        tint = Color(0xFFE57373) // คงสีแดงไว้ให้รู้ว่าเป็น PDF
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = pdf.name,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium,
                        color = LightText, // 🌟 ใช้สีตัวหนังสือ Theme
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Icon(
                        Icons.Default.Close,
                        contentDescription = "ลบ",
                        tint = Color.Gray,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { onRemove(pdf) }
                    )
                }
            }
        }
    }
}
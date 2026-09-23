package com.wealthvault.financiallist.ui.form

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_pdf
import com.wealthvault.core.generated.resources.ic_common_plus
import com.wealthvault.core.generated.resources.ic_form_cross
import com.wealthvault.core.generated.resources.ic_form_photo
import com.wealthvault.core.model.Attachment
import com.wealthvault.core.model.AttachmentType
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault.core.theme.LightText
import org.jetbrains.compose.resources.painterResource

@Composable
fun ReferenceImagePicker(
    attachments: List<Attachment>,
    onAddImage: () -> Unit,
    onAddPdf: () -> Unit,
    onRemove: (Attachment) -> Unit,
) {
    val images = attachments.filter { it.type == AttachmentType.IMAGE }
    val pdfs = attachments.filter { it.type == AttachmentType.PDF }
    var expanded by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "เพิ่มข้อมูลอ้างอิง",
                color = LightPrimary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )
            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_common_plus),
                        contentDescription = "เพิ่ม",
                        tint = LightPrimary,
                        modifier = Modifier.size(28.dp),
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(LightSoftWhite),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    DropdownMenuItem(
                        text = { Text("รูปภาพ", color = LightText, style = MaterialTheme.typography.bodyMedium) },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(Res.drawable.ic_form_photo),
                                contentDescription = null,
                                tint = LightPrimary,
                                modifier = Modifier.size(24.dp),
                            )
                        },
                        onClick = { expanded = false; onAddImage() },
                    )
                    DropdownMenuItem(
                        text = { Text("ไฟล์ PDF", color = LightText, style = MaterialTheme.typography.bodyMedium) },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(Res.drawable.ic_common_pdf),
                                contentDescription = null,
                                tint = Color(0xFFE57373),
                                modifier = Modifier.size(24.dp),
                            )
                        },
                        onClick = { expanded = false; onAddPdf() },
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        if (images.isNotEmpty()) {
            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                images.forEach { image ->
                    Box(modifier = Modifier.padding(end = 12.dp, top = 4.dp).size(80.dp)) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            shape = RoundedCornerShape(12.dp),
                            color = LightSoftWhite,
                            border = BorderStroke(1.dp, LightBorder.copy(alpha = 0.5f)),
                        ) {
                            val imageBytes = image.platformData as? ByteArray
                            if (imageBytes != null) {
                                AsyncImage(
                                    model = imageBytes,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop,
                                )
                            } else {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_form_photo),
                                    contentDescription = null,
                                    tint = Color.LightGray,
                                    modifier = Modifier.padding(24.dp),
                                )
                            }
                        }
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 6.dp, y = (-6).dp)
                                .size(22.dp)
                                .clickable { onRemove(image) },
                            shape = CircleShape,
                            color = Color(0xFFE57373),
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_form_cross),
                                contentDescription = "ลบ",
                                tint = Color.White,
                                modifier = Modifier.padding(4.dp),
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        pdfs.forEach { pdf ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = LightSoftWhite),
                border = BorderStroke(1.dp, LightBorder.copy(alpha = 0.5f)),
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_common_pdf),
                        contentDescription = null,
                        tint = Color(0xFFE57373),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = pdf.name,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium,
                        color = LightText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Icon(
                        painter = painterResource(Res.drawable.ic_form_cross),
                        contentDescription = "ลบ",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp).clickable { onRemove(pdf) },
                    )
                }
            }
        }
    }
}

/** Keep the old spelling local to the feature while routes migrate. */
@Composable
fun ReferenceImagepicker(
    attachments: List<Attachment>,
    onAddImage: () -> Unit,
    onAddPdf: () -> Unit,
    onRemove: (Attachment) -> Unit,
) = ReferenceImagePicker(attachments, onAddImage, onAddPdf, onRemove)

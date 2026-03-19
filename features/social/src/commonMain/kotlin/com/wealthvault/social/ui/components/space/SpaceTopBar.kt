package com.wealthvault.social.ui.components.space

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SpaceTopBar(
    title: String,
    onBackClick: () -> Unit,
    showMoreOption: Boolean = false, // หน้า Space มี 3 จุด แต่หน้าอื่นไม่มี
    themeColor: Color = Color(0xFFC27A5A)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
//        Icon(
//            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//            contentDescription = "Back",
//            tint = themeColor,
//            modifier = Modifier
//                .size(24.dp)
//                .clickable { onBackClick() }
//        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = themeColor,
            modifier = Modifier.weight(1f)
                .padding(horizontal = 24.dp).padding(bottom = 12.dp)

        )
        if (showMoreOption) {
//            Icon(
//                imageVector = Icons.Default.MoreVert,
//                contentDescription = "More options",
//                tint = themeColor,
//                modifier = Modifier.clickable { /* TODO: เปิดเมนูเพิ่มเติม */ }
//            )
        }
    }
}
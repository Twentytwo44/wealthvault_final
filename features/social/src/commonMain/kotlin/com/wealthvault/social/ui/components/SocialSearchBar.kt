package com.wealthvault.social.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SocialSearchBar(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    themeColor: Color = Color(0xFFC27A5A)
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        placeholder = {
            Text(text = "ค้นหาด้วยชื่อ", color = Color(0xFFD7CCC8), fontSize = 14.sp) // สีเทาอ่อนๆ
        },
        leadingIcon = {
//            Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = Color(0xFFD7CCC8))
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = themeColor,
            unfocusedBorderColor = Color(0xFFF3E5D8),
        ),
        singleLine = true
    )
}
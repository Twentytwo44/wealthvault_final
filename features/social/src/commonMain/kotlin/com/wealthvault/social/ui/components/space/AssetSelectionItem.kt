package com.wealthvault.social.ui.components.space

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AssetSelectionItem(
    name: String,
    amount: String,
    tag: String,
    tagColor: Color,
    initiallyChecked: Boolean,
    themeColor: Color = Color(0xFFC27A5A)
) {
    var isChecked by remember { mutableStateOf(initiallyChecked) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(40.dp).background(Color(0xFFF5F5F5), CircleShape))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = Color(0xFF3A2F2A))
                Spacer(modifier = Modifier.width(8.dp))
                Surface(color = tagColor, shape = RoundedCornerShape(4.dp), modifier = Modifier.height(16.dp)) {
                    Text(text = tag, color = Color.White, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 6.dp))
                }
            }
            Text(text = amount, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
        Checkbox(
            checked = isChecked,
            onCheckedChange = { isChecked = it },
            colors = CheckboxDefaults.colors(checkedColor = themeColor)
        )
    }
}
package com.wealthvault.financiallist.ui.shareasset.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wealthvault.financiallist.ui.shareasset.WealthVaultBrown
import com.wealthvault.financiallist.ui.shareasset.model.ShareInfo

@Composable
fun ShareItemWithDelete(
    data: ShareInfo,
    onDelete: () -> Unit // Callback เมื่อกดลบ
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Box
            Box(
                modifier = Modifier.size(48.dp).background(Color(0xFFD9D9D9), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (data.userId == "") Icon(Icons.Default.Email, null, tint = WealthVaultBrown)
                else Icon(Icons.Default.Groups, null, tint = Color.Gray)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(data.name ?: "", fontSize = 16.sp, color = Color.Black)
                Text(data.date ?: "", fontSize = 16.sp, color = Color.Black)

            }

            // ปุ่มลบ
            IconButton(onClick = onDelete) { // ✅ เรียกใช้ onDelete
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null,
                    tint = Color(0xFFD32F2F).copy(alpha = 0.7f)
                )
            }
        }
    }
}

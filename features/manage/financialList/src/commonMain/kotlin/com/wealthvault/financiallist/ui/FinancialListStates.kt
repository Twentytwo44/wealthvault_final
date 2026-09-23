package com.wealthvault.financiallist.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.wealthvault.core.theme.LightSoftWhite

@Composable
fun FinancialListEmptyState(
    themeColor: Color,
    title: String,
    description: String,
    actionLabel: String,
    onAction: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = themeColor,
                textAlign = TextAlign.Center,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center,
            )
            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(containerColor = themeColor),
            ) {
                Text(actionLabel, color = LightSoftWhite)
            }
        }
    }
}

@Composable
fun FinancialListNoResultsState(
    themeColor: Color,
    query: String,
    onClear: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "ไม่พบรายการที่ค้นหา",
                style = MaterialTheme.typography.titleMedium,
                color = themeColor,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "ลองค้นหาด้วยคำอื่น หรือเพิ่มรายการใหม่",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center,
            )
            Button(
                onClick = onClear,
                colors = ButtonDefaults.buttonColors(containerColor = themeColor),
            ) {
                Text("ล้างการค้นหา${if (query.isNotBlank()) " ($query)" else ""}", color = LightSoftWhite)
            }
        }
    }
}

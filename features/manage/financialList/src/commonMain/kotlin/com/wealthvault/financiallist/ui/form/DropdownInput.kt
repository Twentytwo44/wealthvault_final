package com.wealthvault.financiallist.ui.form

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_solid_down
import com.wealthvault.core.generated.resources.ic_common_solid_up
import com.wealthvault.core.theme.*
import org.jetbrains.compose.resources.painterResource

@Composable
fun DropdownInput(
    label: String,
    options: List<Pair<String, String>>,
    selectedValue: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "กรุณาเลือก",
) {
    var expanded by remember { mutableStateOf(false) }
    val displayLabel = options.firstOrNull { it.first == selectedValue }?.second
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = LightPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .background(LightSoftWhite, RoundedCornerShape(12.dp))
                    .border(1.dp, LightBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .clickable { expanded = !expanded }
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = displayLabel ?: placeholder,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (displayLabel != null) Color(0xFF3A2F2A) else Color.LightGray,
                )
                Icon(
                    painter = painterResource(if (expanded) Res.drawable.ic_common_solid_up else Res.drawable.ic_common_solid_down),
                    contentDescription = null,
                    tint = LightPrimary,
                    modifier = Modifier.size(18.dp),
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(0.88f).background(Color.White),
            ) {
                options.forEachIndexed { index, (value, text) ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (value == selectedValue) LightPrimary else Color(0xFF3A2F2A),
                            )
                        },
                        onClick = { onValueChange(value); expanded = false },
                        modifier = Modifier.height(44.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                    )
                    if (index < options.lastIndex) {
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp), color = LightBg.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}

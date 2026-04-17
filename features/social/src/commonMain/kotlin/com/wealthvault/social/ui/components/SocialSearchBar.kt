package com.wealthvault.social.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_search
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightSoftWhite
import org.jetbrains.compose.resources.painterResource

@Composable
fun SocialSearchBar(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    placeholderText: String = "ค้นหาด้วยชื่อ",
    themeColor: Color = Color(0xFFC27A5A)
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchChange,
        modifier = modifier
            .height(52.dp),
        placeholder = {
            Text(text = placeholderText, color = Color.Gray, fontSize = 14.sp)
        },
        leadingIcon = {
            Icon(
                painter = painterResource(Res.drawable.ic_common_search),
                contentDescription = null,
                tint = LightBorder,
                modifier = Modifier.padding(horizontal = 4.dp).size(28.dp)
            )
        },
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = LightSoftWhite,
            unfocusedContainerColor = LightSoftWhite,
            focusedIndicatorColor = LightBorder,
            unfocusedIndicatorColor = LightBorder
        ),
        singleLine = true
    )
}
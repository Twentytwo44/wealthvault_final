package com.wealthvault.financiallist.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_search
import com.wealthvault.core.generated.resources.ic_nav_asset
import com.wealthvault.core.theme.LightAsset
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightSoftWhite
import org.jetbrains.compose.resources.painterResource



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialListTemplate(
    headerTitle: String,
    themeColor: Color, // สีหลักของหน้า (ส้ม สำหรับทรัพย์สิน, แดง สำหรับหนี้สิน)
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onAddClick: () -> Unit,
    headerIcon: @Composable () -> Unit, // ช่องสำหรับใส่ Icon หัวข้อ
    content: @Composable () -> Unit // ช่องว่างตรงกลางสำหรับใส่รายการข้อมูล (LazyColumn)
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(top = 20.dp)
    ) {
        // --- 1. ส่วนหัวข้อ (Header) ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // วาด Icon ที่ส่งเข้ามา
            headerIcon()

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = headerTitle,
                style = MaterialTheme.typography.titleLarge,
                color = themeColor
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- 2. ช่องค้นหา (Search Bar) ---
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            placeholder = {
                Text(text = "ค้นหาด้วยชื่อ", color = Color.Gray, fontSize = 14.sp)
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

        Spacer(modifier = Modifier.height(24.dp))

        // --- 3. พื้นที่สำหรับใส่ข้อมูลลิสต์ ---
        // โค้ดส่วนนี้จะดึง UI จากหน้า Asset หรือ Debt มาแสดง
        Box(modifier = Modifier.weight(1f)) {
            content()
        }
    }

}
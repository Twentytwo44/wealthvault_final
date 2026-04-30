package com.wealthvault_final.`financial-asset`.ui.realestate

// 🌟 Import Theme และหน้าฟอร์มที่เกี่ยวข้อง
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault.core.theme.LightText
import com.wealthvault_final.`financial-asset`.ui.realestate.building.BuildingFormScreen
import com.wealthvault_final.`financial-asset`.ui.realestate.land.LandFormScreen

class RealEstateScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        RealEstateContent(
            onBackClick = { navigator.pop() },
            // 🌟 แก้ไข: แยกการนำทางไปที่ดินและอาคารให้ถูกต้อง
            onClickToLand = { navigator.push(LandFormScreen()) },
            onClickToBuilding = { navigator.push(BuildingFormScreen()) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RealEstateContent(
    onBackClick: () -> Unit = {},
    onClickToLand: () -> Unit = {},
    onClickToBuilding: () -> Unit = {},
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = LightBg, // 🌟 ใช้สีพื้นหลังหลัก
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    title = {
                        Text(
                            "ข้อมูลอสังหาริมทรัพย์",
                            color = LightPrimary,
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null, tint = LightPrimary)
                        }
                    }
                )
            }
        },
        bottomBar = {
            // 🌟 ปรับปรุงปุ่มให้สูง 50.dp และโค้ง 12.dp ตามมาตรฐาน
            Box(modifier = Modifier.navigationBarsPadding().padding(24.dp)) {
                // ย้ายปุ่มลงมาข้างล่างเพื่อให้กดง่าย
            }
        }
    ) { paddingValues ->
        // เก็บตัวเลือกไว้
        val options = listOf("บ้าน ตึก อาคาร", "ที่ดิน")
        var expanded by remember { mutableStateOf(false) }
        var selectedOption by remember { mutableStateOf(options[0]) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "เลือกประเภทอสังหาริมทรัพย์",
                style = MaterialTheme.typography.bodyMedium,
                color = LightPrimary,
                modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
            )

            // 🔹 Custom Dropdown (โฉมใหม่)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, LightBorder, RoundedCornerShape(16.dp))
                    .background(LightSoftWhite, RoundedCornerShape(16.dp))
                    .animateContentSize()
            ) {
                // ส่วนที่แสดงผล
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedOption,
                        style = MaterialTheme.typography.bodyLarge,
                        color = LightText
                    )
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = LightPrimary
                    )
                }

                // รายการให้เลือก
                if (expanded) {
                    options.forEach { option ->
                        if (option != selectedOption) {
                            Divider(modifier = Modifier.padding(horizontal = 16.dp), color = LightBg, thickness = 1.dp)
                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyLarge,
                                color = LightText,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedOption = option
                                        expanded = false
                                    }
                                    .padding(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 🌟 ปุ่มยืนยัน (ปรับดีไซน์ใหม่)
            Button(
                onClick = {
                    if (selectedOption == "บ้าน ตึก อาคาร") {
                        onClickToBuilding()
                    } else {
                        onClickToLand()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp), // สูง 50.dp
                colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
                shape = RoundedCornerShape(12.dp) // โค้ง 12.dp
            ) {
                Text(
                    text = "ต่อไป",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp).navigationBarsPadding())
        }
    }
}
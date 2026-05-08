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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_back
import com.wealthvault.core.generated.resources.ic_common_solid_down
import com.wealthvault.core.generated.resources.ic_common_solid_up
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault.core.theme.LightText
import com.wealthvault_final.`financial-asset`.ui.realestate.building.BuildingFormScreen
import com.wealthvault_final.`financial-asset`.ui.realestate.land.LandFormScreen
import org.jetbrains.compose.resources.painterResource

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
    // เก็บตัวเลือกไว้
    val options = listOf("บ้าน ตึก อาคาร", "ที่ดิน")
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(options[0]) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = LightBg, // 🌟 ใช้สีพื้นหลังหลัก
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    // 🌟 1. ปรับ Padding ของ TopBar ขอบซ้าย-ขวา เป็น 24.dp ถอด IconButton ออก
                    modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 16.dp, top = 24.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_common_back),
                        contentDescription = "Back",
                        tint = LightPrimary,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onBackClick() }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "ข้อมูลอสังหาริมทรัพย์",
                        style = MaterialTheme.typography.titleLarge,
                        color = LightPrimary
                    )
                }
            }
        },
        bottomBar = {
            // 🌟 2. ย้ายปุ่ม ต่อไป มาไว้ใน bottomBar เพื่อความเป๊ะของ Layout ขอบซ้ายขวาล่าง 24.dp
            Box(modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 24.dp).padding(bottom = 24.dp)) {
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
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp) // 🌟 ขอบเนื้อหา 24.dp
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
                        painter  = if (expanded) painterResource(Res.drawable.ic_common_solid_up) else painterResource(Res.drawable.ic_common_solid_down),
                        contentDescription = null,
                        tint = LightPrimary,
                        modifier = Modifier
                            .size(18.dp)
                    )
                }

                // รายการให้เลือก
                if (expanded) {
                    options.forEach { option ->
                        if (option != selectedOption) {
                            // 🌟 3. ใช้ HorizontalDivider แทน Divider ตัวเก่า
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = LightBg, thickness = 1.dp)
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
        }
    }
}
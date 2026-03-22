package com.wealthvault_final.financial_obligations.menu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.debtpic
import com.wealthvault.core.generated.resources.expensepic
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.painterResource


val WealthVaultBrown = Color(0xFFB37E61)
val WealthVaultBackground = Color(0xFFFFF8F3)
val WealthVaultCardHeader = Color(0xFF6D4C41)

data class AssetCategory(
    val id: Int,
    val title: String,
    val iconRes: DrawableResource, // ✅ กลับมาใช้ Resource Object
    val backgroundColor: Color,
    val borderColor: Color
)

val assetCategories = listOf(
    AssetCategory(1, "หนี้สิน", Res.drawable.debtpic, Color(0xFFFFC2C2), Color(0xFFD81B60)),
    AssetCategory(2, "ค่าใช้จ่ายระยะยาว",Res.drawable.expensepic,Color(0xFFFFF9BA), Color(0xFFFBC02D)),

)

@OptIn(org.jetbrains.compose.resources.ExperimentalResourceApi::class)
@Composable
fun MenuObligationScreen(
    onBackClick: () -> Unit = {},
    onNextClick: (AssetCategory?) -> Unit = {}
) {
    var selectedId by remember { mutableStateOf<Int?>(1) } // Default เลือกอันแรกตามรูป
    Scaffold(
        modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding(),
        containerColor = WealthVaultBackground,
        topBar = {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = WealthVaultBrown)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("ประเภททรัพย์สิน", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = WealthVaultBrown)
            }
        },
        bottomBar = {
            Box(modifier = Modifier.padding(16.dp)) {
                Button(
                    onClick = { onNextClick(assetCategories.find { it.id == selectedId }) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = WealthVaultBrown)
                ) {
                    Text("ต่อไป", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        // ✅ ใช้ LazyVerticalGrid จัดเรียง 2 คอลัมน์
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(assetCategories) { category ->
                AssetCard(
                    category = category,
                    isSelected = selectedId == category.id,
                    onClick = { selectedId = category.id }
                )
            }
        }
    }

}
@OptIn(ExperimentalResourceApi::class, InternalResourceApi::class)
@Composable
fun AssetCard(
    category: AssetCategory,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderWidth = if (isSelected) 2.dp else 0.dp
    val borderColor = if (isSelected) category.borderColor else Color.Transparent

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(borderWidth, borderColor),
        colors = CardDefaults.cardColors(containerColor = category.backgroundColor)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = category.borderColor,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(20.dp)
                )
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // ✅ 2. ส่ง imagePath ให้ AsyncImage
                // Coil3 จะเข้าใจ Path นี้และไปดึงไฟล์จาก Resources มาวาดให้เอง
                Image(
                    painter = painterResource(category.iconRes),
                    contentDescription = category.title,
                    modifier = Modifier.size(116.dp),
                )



                Text(
                    text = category.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

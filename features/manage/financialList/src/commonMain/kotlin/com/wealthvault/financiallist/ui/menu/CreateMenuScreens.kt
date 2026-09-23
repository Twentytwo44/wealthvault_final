package com.wealthvault.financiallist.ui.menu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.bookbank
import com.wealthvault.core.generated.resources.cashgold
import com.wealthvault.core.generated.resources.debtpic
import com.wealthvault.core.generated.resources.expensepic
import com.wealthvault.core.generated.resources.ic_common_back
import com.wealthvault.core.generated.resources.insurance
import com.wealthvault.core.generated.resources.land
import com.wealthvault.core.generated.resources.stock
import com.wealthvault.core.navigation.SharedScreen
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightText
import com.wealthvault.financiallist.ui.form.DropdownInput
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

private data class CreateMenuItem(
    val title: String,
    val icon: DrawableResource,
    val destination: SharedScreen,
    val background: Color,
    val border: Color,
)

private val assetItems = listOf(
    CreateMenuItem("บัญชีเงินฝาก", Res.drawable.bookbank, SharedScreen.CreateBankAccount, Color(0xFFE3F2FD), Color(0xFF2196F3)),
    CreateMenuItem("เงินสด ทองคำ", Res.drawable.cashgold, SharedScreen.CreateCash, Color(0xFFF1F8E9), Color(0xFF8BC34A)),
    CreateMenuItem("ลงทุน หุ้น กองทุน", Res.drawable.stock, SharedScreen.CreateInvestment, Color(0xFFE8F5E9), Color(0xFF4CAF50)),
    CreateMenuItem("ประกัน", Res.drawable.insurance, SharedScreen.CreateInsurance, Color(0xFFF3E5F5), Color(0xFF9C27B0)),
    CreateMenuItem("อสังหาริมทรัพย์", Res.drawable.land, SharedScreen.CreateRealEstate, Color(0xFFFFF3E0), Color(0xFFFF9800)),
)

private val debtItems = listOf(
    CreateMenuItem("หนี้สิน", Res.drawable.debtpic, SharedScreen.CreateLiability, Color(0xFFE3F2FD), Color(0xFF2196F3)),
    CreateMenuItem("ค่าใช้จ่ายต่อเนื่อง", Res.drawable.expensepic, SharedScreen.CreateExpense, Color(0xFFF1F8E9), Color(0xFF8BC34A)),
)

class FinancialCreateMenuScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        CreateMenuContent(
            items = assetItems,
            onBackClick = { navigator.pop() },
            onNext = { screen -> navigator.push(screen) },
        )
    }
}

class DebtCreateMenuScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        CreateMenuContent(
            items = debtItems,
            onBackClick = { navigator.pop() },
            onNext = { screen -> navigator.push(screen) },
        )
    }
}

class RealEstateCreateMenuScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var selectedType by remember { mutableStateOf("building") }
        val destination = if (selectedType == "building") {
            rememberScreen(SharedScreen.CreateBuilding)
        } else {
            rememberScreen(SharedScreen.CreateLand)
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = LightBg,
            topBar = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.statusBarsPadding().padding(horizontal = 24.dp).padding(top = 24.dp, bottom = 16.dp),
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_common_back),
                        contentDescription = "Back",
                        tint = LightPrimary,
                        modifier = Modifier.size(24.dp).clickable { navigator.pop() },
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("ข้อมูลอสังหาริมทรัพย์", style = MaterialTheme.typography.titleLarge, color = LightPrimary)
                }
            },
            bottomBar = {
                Box(modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(24.dp)) {
                    Button(
                        onClick = { navigator.push(destination) },
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
                    ) { Text("ต่อไป", color = Color.White, style = MaterialTheme.typography.bodyLarge) }
                }
            },
        ) { paddingValues ->
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 24.dp)) {
                DropdownInput(
                    label = "เลือกประเภทอสังหาริมทรัพย์",
                    options = listOf("building" to "บ้าน ตึก อาคาร", "land" to "ที่ดิน"),
                    selectedValue = selectedType,
                    onValueChange = { selectedType = it },
                    placeholder = "กรุณาเลือกประเภท",
                )
            }
        }
    }
}

@Composable
private fun CreateMenuContent(
    items: List<CreateMenuItem>,
    onBackClick: () -> Unit,
    onNext: (Screen) -> Unit,
) {
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    val destinationScreens = items.map { rememberScreen(it.destination) }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = LightBg,
        topBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.statusBarsPadding().padding(horizontal = 24.dp).padding(top = 24.dp, bottom = 16.dp),
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_common_back),
                    contentDescription = "Back",
                    tint = LightPrimary,
                    modifier = Modifier.size(24.dp).clickable(onClick = onBackClick),
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text("ประเภท", style = MaterialTheme.typography.titleLarge, color = LightPrimary)
            }
        },
        bottomBar = {
            Box(modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(24.dp)) {
                Button(
                    onClick = { selectedIndex?.let { onNext(destinationScreens[it]) } },
                    enabled = selectedIndex != null,
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
                ) {
                    Text("ต่อไป", color = Color.White, style = MaterialTheme.typography.bodyLarge)
                }
            }
        },
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
        ) {
            itemsIndexed(items, key = { _, item -> item.title }) { index, item ->
                val isSelected = selectedIndex == index
                Card(
                    onClick = { selectedIndex = index },
                    modifier = Modifier.fillMaxWidth().height(130.dp),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) item.border else LightBorder.copy(alpha = 0.3f)),
                    colors = CardDefaults.cardColors(containerColor = if (isSelected) item.background else Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = item.border,
                                modifier = Modifier.align(Alignment.TopEnd).padding(12.dp).size(20.dp),
                            )
                        }
                        Column(
                            modifier = Modifier.fillMaxSize().padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Image(painterResource(item.icon), contentDescription = item.title, modifier = Modifier.size(70.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) item.border else LightText,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }
    }
}

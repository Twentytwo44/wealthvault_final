package com.wealthvault.financiallist.ui.asset

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

// Import Models
import com.wealthvault.account_api.model.AccountData
import com.wealthvault.building_api.model.GetBuildingData
import com.wealthvault.cash_api.model.GetCashData
import com.wealthvault.core.components.ConfirmDeleteDialog
import com.wealthvault.insurance_api.model.GetInsuranceData
import com.wealthvault.investment_api.model.GetInvestmentData
import com.wealthvault.land_api.model.GetLandData

// Import Utils & Resources
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_plus
import com.wealthvault.core.generated.resources.ic_nav_asset
import com.wealthvault.core.theme.LightAsset
import com.wealthvault.core.theme.LightDebt
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault.core.utils.formatAmount
import com.wealthvault.core.utils.getScreenModel
import org.jetbrains.compose.resources.painterResource

// Import Components ของฝั่ง Financial
import com.wealthvault.financiallist.ui.FinancialListTemplate
import com.wealthvault.financiallist.ui.component.ExpandableCategoryCard
import com.wealthvault.financiallist.ui.component.RealItemCard
import com.wealthvault.financiallist.ui.component.SmartAssetDetailDialog

import com.wealthvault_final.`financial-asset`.ui.menu.MenuScreen

class AssetScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        var rootNavigator = navigator
        while (rootNavigator.parent != null) {
            rootNavigator = rootNavigator.parent!!
        }

        val screenModel = getScreenModel<AssetScreenModel>()

        LaunchedEffect(Unit) {
            screenModel.fetchAllAssets()
        }

        val accounts by screenModel.accounts.collectAsState()
        val cashes by screenModel.cashes.collectAsState()
        val investments by screenModel.investments.collectAsState()
        val insurances by screenModel.insurances.collectAsState()
        val buildings by screenModel.buildings.collectAsState()
        val lands by screenModel.lands.collectAsState()

        AssetContent(
            screenModel = screenModel,
            onAddClick = {
                rootNavigator.push(MenuScreen())
            },
            accounts = accounts,
            cashes = cashes,
            investments = investments,
            insurances = insurances,
            buildings = buildings,
            lands = lands
        )
    }
}

@Composable
fun AssetContent(
    screenModel: AssetScreenModel,
    onAddClick: () -> Unit,
    accounts: List<AccountData>,
    cashes: List<GetCashData>,
    investments: List<GetInvestmentData>,
    insurances: List<GetInsuranceData>,
    buildings: List<GetBuildingData>,
    lands: List<GetLandData>
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedAssetId by remember { mutableStateOf<String?>(null) }
    var selectedAssetType by remember { mutableStateOf<String?>(null) }

    // 🌟 State สำหรับ Alert ลบข้อมูล
    var showConfirmDelete by remember { mutableStateOf(false) }
    var itemNameToDelete by remember { mutableStateOf("") }

    val filteredAccounts = accounts.filter { it.name.contains(searchQuery, ignoreCase = true) }
    val filteredCashes = cashes.filter { it.name.toString().contains(searchQuery, ignoreCase = true) }
    val filteredInvestments = investments.filter { it.name.toString().contains(searchQuery, ignoreCase = true) }
    val filteredInsurances = insurances.filter { it.name.toString().contains(searchQuery, ignoreCase = true) }
    val filteredBuildings = buildings.filter { it.name.contains(searchQuery, ignoreCase = true) }
    val filteredLands = lands.filter { it.name.toString().contains(searchQuery, ignoreCase = true) }

    Box(modifier = Modifier.fillMaxSize()) {
        FinancialListTemplate(
            headerTitle = "ทรัพย์สิน",
            themeColor = LightAsset,
            searchQuery = searchQuery,
            onSearchChange = { searchQuery = it },
            onAddClick = onAddClick,
            headerIcon = {
                Icon(
                    painter = painterResource(Res.drawable.ic_nav_asset),
                    contentDescription = null,
                    tint = LightAsset,
                    modifier = Modifier.padding(horizontal = 4.dp).size(28.dp)
                )
            }
        ) {
            LazyColumn {
                if (filteredAccounts.isNotEmpty()) {
                    item {
                        ExpandableCategoryCard(title = "บัญชีเงินฝาก", itemCount = filteredAccounts.size, themeColor = "asset", initiallyExpanded = true) {
                            filteredAccounts.forEach { account ->
                                RealItemCard(
                                    title = account.name,
                                    subtitleLabel = "ธนาคาร", subtitleValue = account.bankName,
                                    amountLabel = "ยอดเงิน", amountValue = "${formatAmount(account.amount)} บาท",
                                    onClick = { selectedAssetId = account.id; selectedAssetType = "account" }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                if (filteredCashes.isNotEmpty()) {
                    item {
                        ExpandableCategoryCard(title = "เงินสด ทองคำ", itemCount = filteredCashes.size, themeColor = "asset") {
                            filteredCashes.forEach { cash ->
                                RealItemCard(
                                    title = cash.name ?: "",
                                    subtitleLabel = "รายละเอียด", subtitleValue = cash.description.toString().ifEmpty { "เงินสด" },
                                    amountLabel = "มูลค่า", amountValue = "${formatAmount(cash.ammount ?: 0)} บาท",
                                    onClick = { selectedAssetId = cash.id; selectedAssetType = "cash" }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                if (filteredInvestments.isNotEmpty()) {
                    item {
                        ExpandableCategoryCard(title = "ลงทุน หุ้น กองทุน", itemCount = filteredInvestments.size, themeColor = "asset") {
                            filteredInvestments.forEach { invest ->
                                val rawTotal = (invest.quantity ?: 0.0) * (invest.costPerPrice ?: 0.0)
                                RealItemCard(
                                    title = "${invest.name} (${invest.symbol})",
                                    subtitleLabel = "โบรกเกอร์", subtitleValue = invest.brokerName ?: "",
                                    amountLabel = "มูลค่ารวม", amountValue = "${formatAmount(rawTotal)} บาท",
                                    onClick = { selectedAssetId = invest.id; selectedAssetType = "investment" }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                if (filteredInsurances.isNotEmpty()) {
                    item {
                        ExpandableCategoryCard(title = "ประกัน", itemCount = filteredInsurances.size, themeColor = "asset") {
                            filteredInsurances.forEach { insurance ->
                                RealItemCard(
                                    title = insurance.name ?: "",
                                    subtitleLabel = "บริษัท", subtitleValue = insurance.companyName ?: "",
                                    amountLabel = "วงเงินคุ้มครอง", amountValue = "${formatAmount(insurance.coverageAmount?: 0)} บาท",
                                    onClick = { selectedAssetId = insurance.id; selectedAssetType = "insurance" }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                if (filteredBuildings.isNotEmpty()) {
                    item {
                        ExpandableCategoryCard(title = "บ้าน ตึก อาคาร", itemCount = filteredBuildings.size, themeColor = "asset") {
                            filteredBuildings.forEach { building ->
                                RealItemCard(
                                    title = building.name,
                                    subtitleLabel = "พื้นที่", subtitleValue = "${formatAmount(building.area)} ตร.ม.",
                                    amountLabel = "มูลค่าประเมิน", amountValue = "${formatAmount(building.amount)} บาท",
                                    onClick = { selectedAssetId = building.id; selectedAssetType = "building" }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                if (filteredLands.isNotEmpty()) {
                    item {
                        ExpandableCategoryCard(title = "ที่ดิน", itemCount = filteredLands.size, themeColor = "asset") {
                            filteredLands.forEach { land ->
                                RealItemCard(
                                    title = land.name?: "",
                                    subtitleLabel = "เลขโฉนด", subtitleValue = land.deedNum ?: "",
                                    amountLabel = "มูลค่าประเมิน", amountValue = "${formatAmount(land.amount?: 0)} บาท",
                                    onClick = { selectedAssetId = land.id; selectedAssetType = "land" }
                                )
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(140.dp)) }
            }
        }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 40.dp, end = 25.dp)
                .size(56.dp)
                .clickable { onAddClick() },
            shape = CircleShape,
            color = LightAsset,
            shadowElevation = 3.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(Res.drawable.ic_common_plus),
                    contentDescription = "เพิ่มทรัพย์สิน",
                    tint = LightSoftWhite,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }

    // 🌟 ส่วนของ Alert ลบข้อมูล
    if (showConfirmDelete) {
        val annotatedMessage = buildAnnotatedString {
            append("คุณแน่ใจหรือไม่ว่าต้องการลบ ")
            withStyle(style = SpanStyle(color = LightAsset, fontWeight = FontWeight.Bold)) {
                append("'$itemNameToDelete'")
            }
            append(" ออกจากระบบ?")
        }

        ConfirmDeleteDialog(
            title = "ลบทรัพย์สิน",
            message = annotatedMessage,
            onConfirm = {
                selectedAssetId?.let { id ->
                    selectedAssetType?.let { type ->
                        screenModel.deleteAsset(id, type)
                    }
                }
                showConfirmDelete = false
                selectedAssetId = null
                selectedAssetType = null
            },
            onDismiss = { showConfirmDelete = false }
        )
    }

    // 🌟 เรียกใช้ Smart Dialog สุดฉลาด
    if (selectedAssetId != null && selectedAssetType != null && !showConfirmDelete) {
        SmartAssetDetailDialog(
            assetId = selectedAssetId!!,
            assetType = selectedAssetType!!,
            showBottomMenu = true, // หน้าจัดการทรัพย์สินของตัวเอง ต้องมีปุ่ม
            onDismiss = {
                selectedAssetId = null
                selectedAssetType = null
            },
            onDelete = { itemName ->
                itemNameToDelete = itemName // รับชื่อที่ Smart Dialog ส่งกลับมา
                showConfirmDelete = true
            },
            onEdit = {
                // TODO: นำทางไปยังหน้าแก้ไข
                selectedAssetId = null
                selectedAssetType = null
            }
        )
    }
}
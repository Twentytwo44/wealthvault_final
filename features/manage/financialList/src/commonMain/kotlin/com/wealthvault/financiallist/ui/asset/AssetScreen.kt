package com.wealthvault.financiallist.ui.asset

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.screen.Screen
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_nav_asset
import com.wealthvault.core.theme.LightAsset
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.financiallist.ui.FinancialListTemplate
import com.wealthvault.financiallist.ui.component.ExpandableCategoryCard
import com.wealthvault.financiallist.ui.component.RealItemCard
import com.wealthvault.financiallist.ui.component.DetailDialog
import com.wealthvault.financiallist.ui.component.DetailRow
import org.jetbrains.compose.resources.painterResource
import kotlin.math.roundToInt

// Import Data Class
import com.wealthvault.account_api.model.AccountData
import com.wealthvault.account_api.model.BankAccountData
import com.wealthvault.building_api.model.BuildingIdData
import com.wealthvault.cash_api.model.GetCashData
import com.wealthvault.investment_api.model.GetInvestmentData
import com.wealthvault.insurance_api.model.GetInsuranceData
import com.wealthvault.building_api.model.GetBuildingData
import com.wealthvault.cash_api.model.CashIdData
import com.wealthvault.financiallist.ui.component.DetailImageRow
import com.wealthvault.insurance_api.model.InsuranceIdData
import com.wealthvault.investment_api.model.InvestmentIdData
import com.wealthvault.land_api.model.GetLandData
import com.wealthvault.land_api.model.LandIdData

class AssetScreen(private val onAddClick: () -> Unit) : Screen {
    @Composable
    override fun Content() {
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
            screenModel = screenModel, // 🌟 ส่ง Model ลงไปเพื่อให้ FetcherDialog ใช้ยิง API
            onAddClick = onAddClick,
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

    // 🌟 เก็บแค่ ID และ Type เพื่อเอาไปยิง API ตอนกด
    var selectedAssetId by remember { mutableStateOf<String?>(null) }
    var selectedAssetType by remember { mutableStateOf<String?>(null) }

    val filteredAccounts = accounts.filter { it.name.contains(searchQuery, ignoreCase = true) }
    val filteredCashes = cashes.filter { it.name.contains(searchQuery, ignoreCase = true) }
    val filteredInvestments = investments.filter { it.name.contains(searchQuery, ignoreCase = true) }
    val filteredInsurances = insurances.filter { it.name.contains(searchQuery, ignoreCase = true) }
    val filteredBuildings = buildings.filter { it.name.contains(searchQuery, ignoreCase = true) }
    val filteredLands = lands.filter { it.name.contains(searchQuery, ignoreCase = true) }

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
                                amountLabel = "ยอดเงิน", amountValue = "${account.amount} บาท",
                                onClick = { selectedAssetId = account.id; selectedAssetType = "account" }
                            )
                        }
                    }
                }
            }

            if (filteredCashes.isNotEmpty()) {
                item {
                    ExpandableCategoryCard(title = "เงินสด ทองคำ", itemCount = filteredCashes.size, themeColor = "asset") {
                        filteredCashes.forEach { cash ->
                            RealItemCard(
                                title = cash.name,
                                subtitleLabel = "รายละเอียด", subtitleValue = cash.description.ifEmpty { "เงินสด" },
                                amountLabel = "มูลค่า", amountValue = "${cash.amount} บาท",
                                onClick = { selectedAssetId = cash.id; selectedAssetType = "cash" }
                            )
                        }
                    }
                }
            }

            if (filteredInvestments.isNotEmpty()) {
                item {
                    ExpandableCategoryCard(title = "ลงทุน หุ้น กองทุน", itemCount = filteredInvestments.size, themeColor = "asset") {
                        filteredInvestments.forEach { invest ->
                            val rawTotal = invest.quantity * invest.costPerPrice
                            val totalVal = (rawTotal * 100.0).roundToInt() / 100.0
                            RealItemCard(
                                title = "${invest.name} (${invest.symbol})",
                                subtitleLabel = "โบรกเกอร์", subtitleValue = invest.brokerName,
                                amountLabel = "มูลค่ารวม", amountValue = "${totalVal} บาท",
                                onClick = { selectedAssetId = invest.id; selectedAssetType = "investment" }
                            )
                        }
                    }
                }
            }

            if (filteredInsurances.isNotEmpty()) {
                item {
                    ExpandableCategoryCard(title = "ประกัน", itemCount = filteredInsurances.size, themeColor = "asset") {
                        filteredInsurances.forEach { insurance ->
                            RealItemCard(
                                title = insurance.name,
                                subtitleLabel = "บริษัท", subtitleValue = insurance.companyName,
                                amountLabel = "วงเงินคุ้มครอง", amountValue = "${insurance.coverageAmount} บาท",
                                onClick = { selectedAssetId = insurance.id; selectedAssetType = "insurance" }
                            )
                        }
                    }
                }
            }

            if (filteredBuildings.isNotEmpty()) {
                item {
                    ExpandableCategoryCard(title = "บ้าน ตึก อาคาร", itemCount = filteredBuildings.size, themeColor = "asset") {
                        filteredBuildings.forEach { building ->
                            RealItemCard(
                                title = building.name,
                                subtitleLabel = "พื้นที่", subtitleValue = "${building.area} ตร.ม.",
                                amountLabel = "มูลค่าประเมิน", amountValue = "${building.amount} บาท",
                                onClick = { selectedAssetId = building.id; selectedAssetType = "building" }
                            )
                        }
                    }
                }
            }

            if (filteredLands.isNotEmpty()) {
                item {
                    ExpandableCategoryCard(title = "ที่ดิน", itemCount = filteredLands.size, themeColor = "asset") {
                        filteredLands.forEach { land ->
                            RealItemCard(
                                title = land.name,
                                subtitleLabel = "เลขโฉนด", subtitleValue = land.deedNum,
                                amountLabel = "มูลค่าประเมิน", amountValue = "${land.amount} บาท",
                                onClick = { selectedAssetId = land.id; selectedAssetType = "land" }
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    // 🌟 เรียกใช้ FetcherDialog เมื่อมีการกดเลือก Asset
    if (selectedAssetId != null && selectedAssetType != null) {
        AssetDetailFetcherDialog(
            assetId = selectedAssetId!!,
            assetType = selectedAssetType!!,
            screenModel = screenModel,
            onDismiss = {
                selectedAssetId = null
                selectedAssetType = null
            }
        )
    }
}

// 🌟 ตัวกลางสำหรับโชว์ Loading และยิง API ดึง Detail
// 🌟 ตัวกลางสำหรับโชว์ Loading และยิง API ดึง Detail
@Composable
fun AssetDetailFetcherDialog(
    assetId: String,
    assetType: String,
    screenModel: AssetScreenModel,
    onDismiss: () -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }
    var detailData by remember { mutableStateOf<Any?>(null) }

    LaunchedEffect(assetId, assetType) {
        isLoading = true
        detailData = when (assetType) {
            "account" -> screenModel.getAccountById(assetId)
            "cash" -> screenModel.getCashById(assetId)
            "investment" -> screenModel.getInvestmentById(assetId) // 🌟 รันล่วงหน้า
            "insurance" -> screenModel.getInsuranceById(assetId)   // 🌟 รันประกัน
            "building" -> screenModel.getBuildingById(assetId)
            "land" -> screenModel.getLandById(assetId)


            else -> null
        }
        isLoading = false
    }

    if (isLoading) {
        Dialog(onDismissRequest = onDismiss) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.White, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = LightAsset)
            }
        }
    } else if (detailData != null) {
        when (val item = detailData) {
            // 1. บัญชี
            is BankAccountData -> {
                DetailDialog(subtitle = "ทรัพย์สิน · บัญชีเงินฝาก", title = item.name, themeType = "asset", onDismiss = onDismiss) {
                    DetailRow("ธนาคาร", item.bankName)
                    DetailRow("เลขบัญชี", item.bankAccount)
                    DetailRow("ประเภท", item.type)
                    DetailRow("ยอดเงิน", "${item.amount} บาท")
                    DetailRow("คำอธิบาย", item.description, isLast = item.files.isNullOrEmpty())
                    DetailImageRow(files = item.files)
                }
            }

            // 2. เงินสด
            is CashIdData -> {
                DetailDialog(subtitle = "ทรัพย์สิน · เงินสด ทองคำ", title = item.name, themeType = "asset", onDismiss = onDismiss) {
                    DetailRow("มูลค่า", "${item.amount} บาท")
                    DetailRow("คำอธิบาย", item.description, isLast = item.files.isNullOrEmpty())
                    DetailImageRow(files = item.files)
                }
            }

            // 3. การลงทุน (สมมติ Model ล่วงหน้า)
            is InvestmentIdData -> {
                DetailDialog(subtitle = "ทรัพย์สิน · ลงทุน หุ้น กองทุน", title = "${item.name} (${item.symbol})", themeType = "asset", onDismiss = onDismiss) {
                    DetailRow("โบรกเกอร์", item.brokerName)
                    DetailRow("จำนวน", "${item.quantity}")
                    DetailRow("ราคาทุนต่อหน่วย", "${item.costPerPrice} บาท")
                    DetailRow("ประเภท", item.type)
                    DetailRow("คำอธิบาย", item.description, isLast = item.files.isNullOrEmpty())
                    DetailImageRow(files = item.files)
                }
            }

            // 4. ประกัน (ของจริง มาแล้ว!)
            is InsuranceIdData -> {
                DetailDialog(subtitle = "ทรัพย์สิน · ประกัน", title = item.name, themeType = "asset", onDismiss = onDismiss) {
                    DetailRow("เลขกรมธรรม์", item.policyNumber)
                    DetailRow("บริษัท", item.companyName)
                    DetailRow("วงเงินคุ้มครอง", "${item.coverageAmount} บาท")
                    DetailRow("ระยะเวลาคุ้มครอง", "${item.coveragePeriod} ปี")
                    DetailRow("วันเริ่มสัญญา", item.conDate.take(10))
                    DetailRow("วันสิ้นสุดสัญญา", item.expDate.take(10))
                    DetailRow("คำอธิบาย", item.description, isLast = item.files.isNullOrEmpty())
                    DetailImageRow(files = item.files)
                }
            }

            // 5. อาคาร
            is BuildingIdData -> {
                DetailDialog(subtitle = "ทรัพย์สิน · บ้าน ตึก อาคาร", title = item.name, themeType = "asset", onDismiss = onDismiss) {
                    DetailRow("ประเภท", item.type)
                    DetailRow("พื้นที่", "${item.area} ตร.ม.")
                    DetailRow("มูลค่าประเมิน", "${item.amount} บาท")
                    val addressStr = item.location?.let { "${it.address} ${it.subDistrict} ${it.district} ${it.province} ${it.postalCode}".trim() } ?: "-"
                    DetailRow("ที่อยู่", addressStr)
                    DetailRow("คำอธิบาย", item.description, isLast = item.files.isNullOrEmpty())
                    DetailImageRow(files = item.files)
                }
            }

            // 6. ที่ดิน (สมมติ Model ล่วงหน้า)
            is LandIdData -> {
                DetailDialog(subtitle = "ทรัพย์สิน · ที่ดิน", title = item.name, themeType = "asset", onDismiss = onDismiss) {
                    DetailRow("เลขโฉนด", item.deedNum)
                    DetailRow("ขนาดพื้นที่", "${item.area} ตารางวา")
                    DetailRow("มูลค่าประเมิน", "${item.amount} บาท")
                    val addressStr = item.location?.let { "${it.address} ${it.subDistrict} ${it.district} ${it.province} ${it.postalCode}".trim() } ?: "-"
                    DetailRow("ที่อยู่", addressStr)
                    DetailRow("คำอธิบาย", item.description, isLast = item.files.isNullOrEmpty())
                    DetailImageRow(files = item.files)
                }
            }
        }
    }
}
package com.wealthvault.financiallist.ui.asset

// 🌟 Import เพิ่มเติมสำหรับปุ่มเพิ่มรายการ

// Import Data Class
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.screen.Screen
import com.wealthvault.account_api.model.AccountData
import com.wealthvault.account_api.model.BankAccountData
import com.wealthvault.building_api.model.BuildingIdData
import com.wealthvault.building_api.model.GetBuildingData
import com.wealthvault.cash_api.model.CashIdData
import com.wealthvault.cash_api.model.GetCashData
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_plus
import com.wealthvault.core.generated.resources.ic_nav_asset
import com.wealthvault.core.theme.LightAsset
import com.wealthvault.core.theme.LightDebt
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault.core.utils.formatAmount
import com.wealthvault.core.utils.formatThaiDate
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.financiallist.ui.FinancialListTemplate
import com.wealthvault.financiallist.ui.component.ConfirmDeleteDialog
import com.wealthvault.financiallist.ui.component.DetailDialog
import com.wealthvault.financiallist.ui.component.DetailImageRow
import com.wealthvault.financiallist.ui.component.DetailRow
import com.wealthvault.financiallist.ui.component.ExpandableCategoryCard
import com.wealthvault.financiallist.ui.component.RealItemCard
import com.wealthvault.insurance_api.model.GetInsuranceData
import com.wealthvault.insurance_api.model.InsuranceIdData
import com.wealthvault.investment_api.model.GetInvestmentData
import com.wealthvault.investment_api.model.InvestmentIdData
import com.wealthvault.land_api.model.GetLandData
import com.wealthvault.land_api.model.LandIdData
import org.jetbrains.compose.resources.painterResource

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
            screenModel = screenModel,
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

    var selectedAssetId by remember { mutableStateOf<String?>(null) }
    var selectedAssetType by remember { mutableStateOf<String?>(null) }

    val filteredAccounts = accounts.filter { it.name.contains(searchQuery, ignoreCase = true) }
    val filteredCashes = cashes.filter { it.name.toString().contains(searchQuery, ignoreCase = true) }
    val filteredInvestments = investments.filter { it.name.toString().contains(searchQuery, ignoreCase = true) }
    val filteredInsurances = insurances.filter { it.name.toString().contains(searchQuery, ignoreCase = true) }
    val filteredBuildings = buildings.filter { it.name.contains(searchQuery, ignoreCase = true) }
    val filteredLands = lands.filter { it.name.toString().contains(searchQuery, ignoreCase = true) }

    // 🌟 1. ใช้ Box ครอบทั้งหน้าจอเพื่อที่จะวางปุ่มไว้ด้านบนสุด (Overlay) ได้
    Box(modifier = Modifier.fillMaxSize()) {

        // เนื้อหาหลัก (รายการทรัพย์สิน)
        FinancialListTemplate(
            headerTitle = "ทรัพย์สิน",
            themeColor = LightAsset,
            searchQuery = searchQuery,
            onSearchChange = { searchQuery = it },
            onAddClick = onAddClick, // ถ้าใน FinancialListTemplate มีปุ่มเพิ่มของมันเองด้วย
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

                item { Spacer(modifier = Modifier.height(140.dp)) } // 🌟 เผื่อพื้นที่ด้านล่างไว้ไม่ให้โดนปุ่มบัง
            }
        }

        // 🌟 2. ปุ่มลอยมุมขวาล่าง (Floating Action Button)
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

@Composable
fun AssetDetailFetcherDialog(
    assetId: String,
    assetType: String,
    screenModel: AssetScreenModel,
    onDismiss: () -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }
    var detailData by remember { mutableStateOf<Any?>(null) }

    // 🌟 1. ปรับ State ให้เก็บชื่อของ Item ที่จะลบด้วย
    var showConfirmDelete by remember { mutableStateOf(false) }
    var itemNameToDelete by remember { mutableStateOf("") }

    LaunchedEffect(assetId, assetType) {
        isLoading = true
        detailData = when (assetType) {
            "account" -> screenModel.getAccountById(assetId)
            "cash" -> screenModel.getCashById(assetId)
            "investment" -> screenModel.getInvestmentById(assetId)
            "insurance" -> screenModel.getInsuranceById(assetId)
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
        val item = detailData!!

        // 🌟 2. Popup ยืนยันการลบที่ระบุชื่อชัดเจน
        // 🌟 2. Popup ยืนยันการลบที่มีการเน้นสีชื่อไอเทม
        if (showConfirmDelete) {
            // สร้างข้อความแบบเน้นสีเฉพาะจุด (AnnotatedString)
            val annotatedMessage = buildAnnotatedString {
                append("คุณแน่ใจหรือไม่ว่าต้องการลบ ")
                withStyle(style = SpanStyle(
                    color = if (assetType == "debt") LightDebt else LightAsset, // 🌟 เลือกสีตามประเภท
                    fontWeight = FontWeight.Bold
                )
                ) {
                    append("'$itemNameToDelete'")
                }
                append(" ออกจากระบบ?")
            }

            ConfirmDeleteDialog(
                title = "ลบทรัพย์สิน",
                message = annotatedMessage, // 🌟 ส่งค่าที่เป็น AnnotatedString เข้าไป
                onConfirm = {
                    screenModel.deleteAsset(assetId, assetType)
                    showConfirmDelete = false
                    onDismiss()
                },
                onDismiss = { showConfirmDelete = false }
            )
        }

        when (item) {
            is BankAccountData -> {
                DetailDialog(
                    subtitle = "ทรัพย์สิน · บัญชีเงินฝาก",
                    title = item.name,
                    updatedAt = formatThaiDate(item.updatedAt),
                    themeType = "asset",
                    onDismiss = onDismiss,
                    onDelete = {
                        itemNameToDelete = item.name // 🌟 เก็บชื่อก่อนเปิด Dialog
                        showConfirmDelete = true
                    }
                ) {
                    DetailRow("ธนาคาร", item.bankName)
                    DetailRow("เลขบัญชี", item.bankAccount)
                    DetailRow("ประเภท", item.type)
                    DetailRow("ยอดเงิน", "${formatAmount(item.amount)} บาท")
                    DetailRow("คำอธิบาย", item.description, isLast = item.files.isNullOrEmpty())
                    DetailImageRow(files = item.files)
                }
            }

            is CashIdData -> {
                DetailDialog(
                    subtitle = "ทรัพย์สิน · เงินสด ทองคำ",
                    title = item.name,
                    updatedAt = formatThaiDate(item.updatedAt),
                    themeType = "asset",
                    onDismiss = onDismiss,
                    onDelete = {
                        itemNameToDelete = item.name
                        showConfirmDelete = true
                    }
                ) {
                    DetailRow("มูลค่า", "${formatAmount(item.amount)} บาท")
                    DetailRow("คำอธิบาย", item.description, isLast = item.files.isNullOrEmpty())
                    DetailImageRow(files = item.files)
                }
            }

            is InvestmentIdData -> {
                DetailDialog(
                    subtitle = "ทรัพย์สิน · ลงทุน หุ้น กองทุน",
                    title = "${item.name} (${item.symbol})",
                    updatedAt = formatThaiDate(item.updatedAt),
                    themeType = "asset",
                    onDismiss = onDismiss,
                    onDelete = {
                        itemNameToDelete = "${item.name} (${item.symbol})"
                        showConfirmDelete = true
                    }
                ) {
                    DetailRow("โบรกเกอร์", item.brokerName)
                    DetailRow("จำนวน", "${formatAmount(item.quantity)}")
                    DetailRow("ราคาทุนต่อหน่วย", "${formatAmount(item.costPerPrice)} บาท")
                    DetailRow("ประเภท", item.type)
                    DetailRow("คำอธิบาย", item.description, isLast = item.files.isNullOrEmpty())
                    DetailImageRow(files = item.files)
                }
            }

            is InsuranceIdData -> {
                DetailDialog(
                    subtitle = "ทรัพย์สิน · ประกัน",
                    title = item.name,
                    updatedAt = formatThaiDate(item.updatedAt),
                    themeType = "asset",
                    onDismiss = onDismiss,
                    onDelete = {
                        itemNameToDelete = item.name
                        showConfirmDelete = true
                    }
                ) {
                    DetailRow("เลขกรมธรรม์", item.policyNumber)
                    DetailRow("บริษัท", item.companyName)
                    DetailRow("วงเงินคุ้มครอง", "${formatAmount(item.coverageAmount)} บาท")
                    DetailRow("ระยะเวลาคุ้มครอง", "${item.coveragePeriod} ปี")
                    DetailRow("วันเริ่มสัญญา", formatThaiDate(item.conDate))
                    DetailRow("วันสิ้นสุดสัญญา", formatThaiDate(item.expDate))
                    DetailRow("คำอธิบาย", item.description, isLast = item.files.isNullOrEmpty())
                    DetailImageRow(files = item.files)
                }
            }

            is BuildingIdData -> {
                DetailDialog(
                    subtitle = "ทรัพย์สิน · บ้าน ตึก อาคาร",
                    title = item.name?: "",
                    updatedAt = formatThaiDate(item.updatedAt),
                    themeType = "asset",
                    onDismiss = onDismiss,
                    onDelete = {
                        itemNameToDelete = item.name?: ""
                        showConfirmDelete = true
                    }
                ) {
                    DetailRow("ประเภท", item.type?: "")
                    DetailRow("พื้นที่", "${formatAmount(item.area?: 0)} ตร.ม.")
                    DetailRow("มูลค่าประเมิน", "${formatAmount(item.amount?: 0)} บาท")
                    val addressStr = item.location?.let { "${it.address} ${it.subDistrict} ${it.district} ${it.province} ${it.postalCode}".trim() } ?: "-"
                    DetailRow("ที่อยู่", addressStr)
                    DetailRow("คำอธิบาย", item.description?: "", isLast = item.files.isNullOrEmpty())
                    DetailImageRow(files = item.files)
                }
            }

            is LandIdData -> {
                DetailDialog(
                    subtitle = "ทรัพย์สิน · ที่ดิน",
                    title = item.name,
                    updatedAt = formatThaiDate(item.updatedAt),
                    themeType = "asset",
                    onDismiss = onDismiss,
                    onDelete = {
                        itemNameToDelete = item.name
                        showConfirmDelete = true
                    }
                ) {
                    DetailRow("เลขโฉนด", item.deedNum)
                    DetailRow("ขนาดพื้นที่", "${formatAmount(item.area)} ตารางวา")
                    DetailRow("มูลค่าประเมิน", "${formatAmount(item.amount)} บาท")
                    val addressStr = item.location?.let { "${it.address} ${it.subDistrict} ${it.district} ${it.province} ${it.postalCode}".trim() } ?: "-"
                    DetailRow("ที่อยู่", addressStr)
                    DetailRow("คำอธิบาย", item.description, isLast = item.files.isNullOrEmpty())
                    DetailImageRow(files = item.files)
                }
            }
        }
    }
}

package com.wealthvault.financiallist.ui.asset

// Import Utils & Resources

// Import Components ของฝั่ง Financial
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.navigator.Navigator
import com.wealthvault.domain.portfolio.AccountData
import com.wealthvault.domain.portfolio.BankAccountData
import com.wealthvault.domain.portfolio.BuildingIdData
import com.wealthvault.domain.portfolio.GetBuildingData
import com.wealthvault.domain.portfolio.CashIdData
import com.wealthvault.domain.portfolio.GetCashData
import com.wealthvault.core.components.ConfirmDeleteDialog
import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_plus
import com.wealthvault.core.generated.resources.ic_nav_asset
import com.wealthvault.core.theme.LightAsset
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault.core.utils.LocalRootNavigator
import com.wealthvault.core.utils.formatAmount
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.core.model.FixedDecimal
import com.wealthvault.core.model.Money
import com.wealthvault.core.navigation.SharedScreen
import com.wealthvault.financiallist.ui.FinancialListTemplate
import com.wealthvault.financiallist.ui.FinancialListEmptyState
import com.wealthvault.financiallist.ui.FinancialListNoResultsState
import com.wealthvault.financiallist.ui.asset.form.account.BankAccountFormScreen
import com.wealthvault.financiallist.ui.asset.form.building.BuildingFormScreen
import com.wealthvault.financiallist.ui.asset.form.cash.CashFormScreen
import com.wealthvault.financiallist.ui.asset.form.insurance.InsuranceFormScreen
import com.wealthvault.financiallist.ui.asset.form.investment.StockFormScreen
import com.wealthvault.financiallist.ui.asset.form.land.LandFormScreen
import com.wealthvault.financiallist.ui.component.ExpandableCategoryCard
import com.wealthvault.financiallist.ui.component.RealItemCard
import com.wealthvault.financiallist.ui.component.SmartAssetDetailDialog
import com.wealthvault.financiallist.ui.shareasset.ShareAssetScreen
import com.wealthvault.domain.portfolio.GetInsuranceData
import com.wealthvault.domain.portfolio.InsuranceIdData
import com.wealthvault.domain.portfolio.GetInvestmentData
import com.wealthvault.domain.portfolio.InvestmentIdData
import com.wealthvault.domain.portfolio.GetLandData
import com.wealthvault.domain.portfolio.LandIdData
import com.wealthvault.financiallist.ui.toAttachment
import com.wealthvault.domain.portfolio.BankAccountModel
import com.wealthvault.domain.portfolio.BuildingModel
import com.wealthvault.domain.portfolio.CashModel
import com.wealthvault.domain.portfolio.InsRefModel
import com.wealthvault.domain.portfolio.InsuranceModel
import com.wealthvault.domain.portfolio.LandModel
import com.wealthvault.domain.portfolio.RefModel
import com.wealthvault.domain.portfolio.StockModel
import org.jetbrains.compose.resources.painterResource

@Composable
fun AssetContent(
    screenModel: AssetScreenModel,
    onAddClick: () -> Unit,
    accounts: List<AccountData>,
    cashes: List<GetCashData>,
    investments: List<GetInvestmentData>,
    insurances: List<GetInsuranceData>,
    buildings: List<GetBuildingData>,
    lands: List<GetLandData>,
    navigatorContent: Navigator
) {
    val uiState by screenModel.uiState.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var selectedAssetId by remember { mutableStateOf<String?>(null) }
    var selectedAssetType by remember { mutableStateOf<String?>(null) }

    var showConfirmDelete by remember { mutableStateOf(false) }
    var itemNameToDelete by remember { mutableStateOf("") }

    // Keep search-derived lists stable while unrelated UI state (dialogs,
    // animations, or scroll position) recomposes the screen.
    val filteredAccounts = remember(accounts, searchQuery) {
        accounts.filter { (it.name ?: "").contains(searchQuery, ignoreCase = true) }
    }
    val filteredCashes = remember(cashes, searchQuery) {
        cashes.filter { (it.name ?: "").contains(searchQuery, ignoreCase = true) }
    }
    val filteredInvestments = remember(investments, searchQuery) {
        investments.filter { (it.name ?: "").contains(searchQuery, ignoreCase = true) }
    }
    val filteredInsurances = remember(insurances, searchQuery) {
        insurances.filter { (it.name ?: "").contains(searchQuery, ignoreCase = true) }
    }
    val filteredBuildings = remember(buildings, searchQuery) {
        buildings.filter { (it.name ?: "").contains(searchQuery, ignoreCase = true) }
    }
    val filteredLands = remember(lands, searchQuery) {
        lands.filter { (it.name ?: "").contains(searchQuery, ignoreCase = true) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        FinancialListTemplate(
            headerTitle = "ทรัพย์สิน & ประกัน",
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
            val hasData = accounts.isNotEmpty() || cashes.isNotEmpty() ||
                investments.isNotEmpty() || insurances.isNotEmpty() ||
                buildings.isNotEmpty() || lands.isNotEmpty()

            if (uiState.isLoading && !hasData) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = LightAsset)
                }
            } else if (!hasData && uiState.error != null) {
                val error = requireNotNull(uiState.error)
                AssetLoadError(
                    message = assetErrorMessage(error),
                    onRetry = { screenModel.onAction(AssetUiAction.Refresh) },
                )
            } else if (!hasData) {
                FinancialListEmptyState(
                    themeColor = LightAsset,
                    title = "ยังไม่มีทรัพย์สิน",
                    description = "เพิ่มบัญชี เงินสด การลงทุน ประกัน บ้าน หรือที่ดิน เพื่อเริ่มติดตามภาพรวมการเงิน",
                    actionLabel = "เพิ่มทรัพย์สิน",
                    onAction = onAddClick,
                )
            } else if (
                filteredAccounts.isEmpty() &&
                filteredCashes.isEmpty() &&
                filteredInvestments.isEmpty() &&
                filteredInsurances.isEmpty() &&
                filteredBuildings.isEmpty() &&
                filteredLands.isEmpty()
            ) {
                FinancialListNoResultsState(
                    themeColor = LightAsset,
                    query = searchQuery,
                    onClear = { searchQuery = "" },
                )
            } else {
                LazyColumn {
                if (filteredAccounts.isNotEmpty()) {
                    item {
                        ExpandableCategoryCard(title = "บัญชีเงินฝาก", itemCount = filteredAccounts.size, themeColor = "asset", initiallyExpanded = true) {
                            filteredAccounts.forEach { account ->
                                RealItemCard(
                                    title = account.name ?: "",
                                    subtitleLabel = "ธนาคาร", subtitleValue = account.bankName ?: "",
                                    amountLabel = "ยอดเงิน", amountValue = "${formatAmount(account.amount ?: Money(0))} บาท",
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
                                    subtitleLabel = "รายละเอียด",
                                    subtitleValue = cash.description?.takeIf { it.isNotBlank() } ?: "-",
                                    amountLabel = "มูลค่า",
                                    amountValue = "${formatAmount(cash.ammount ?: Money(0))} บาท",
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
                                 val rawTotal = invest.amount ?: Money(0)

                                RealItemCard(
                                    title = invest.name ?: "ไม่ระบุชื่อ",
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
                                    amountLabel = "วงเงินคุ้มครอง", amountValue = "${formatAmount(insurance.coverageAmount ?: Money(0))} บาท",
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
                                    title = building.name?:"",
                                    subtitleLabel = "พื้นที่", subtitleValue = "${formatAmount(building.area?:0.0)} ตร.ม.",
                                    amountLabel = "มูลค่าประเมิน", amountValue = "${formatAmount(building.amount ?: Money(0))} บาท",
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
                                    amountLabel = "มูลค่าประเมิน", amountValue = "${formatAmount(land.amount ?: Money(0))} บาท",
                                    onClick = { selectedAssetId = land.id; selectedAssetType = "land" }
                                )
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(140.dp)) }
                }
            }
        }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 40.dp, end = 25.dp)
                .size(50.dp)
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

    // 🌟 จัดการ logic ทั้งหมดตรงนี้แทน
    if (selectedAssetId != null && selectedAssetType != null && !showConfirmDelete) {
        SmartAssetDetailDialog(
            assetId = selectedAssetId!!,
            assetType = selectedAssetType!!,
            showBottomMenu = true,
            onDismiss = {
                selectedAssetId = null
                selectedAssetType = null
            },
            onDelete = { itemName ->
                itemNameToDelete = itemName
                showConfirmDelete = true
            },
            onShare = {
                navigatorContent.push(ShareAssetScreen(selectedAssetType!!, selectedAssetId!!))
            },
            onEdit = { rawData ->
                when (rawData) {
                    is BankAccountData -> {
                        val attachments = rawData.files?.map { it.toAttachment() }
                        val dataToSend = BankAccountModel(
                            type = rawData.type ?: "", name = rawData.name ?: "", bankName = rawData.bankName ?: "",
                            bankId = rawData.bankAccount ?: "", amount = rawData.amount ?: Money(0), description = rawData.description ?: "",
                            attachments = attachments ?: emptyList()
                        )
                        navigatorContent.push(BankAccountFormScreen(rawData.id, dataToSend))
                    }
                    is CashIdData -> {
                        val attachments = rawData.files?.map { it.toAttachment() }
                        val dataToSend = CashModel(
                            cashName = rawData.name ?: "", amount = rawData.amount ?: Money(0), description = rawData.description ?: "",
                            attachments = attachments ?: emptyList()
                        )
                        navigatorContent.push(CashFormScreen(rawData.id, dataToSend))
                    }
                    is InvestmentIdData -> {
                        val attachments = rawData.files?.map { it.toAttachment() }
                        val dataToSend = StockModel(
                            stockName = rawData.name ?: "",
                            quantity = FixedDecimal.fromDecimal(rawData.quantity?.toString(), scale = 4) ?: FixedDecimal(0, 4),
                            costPerPrice = rawData.costPerPrice ?: Money(0),
                            description = rawData.description ?: "", attachments = attachments ?: emptyList(),
                            stockSymbol = "", brokerName = rawData.brokerName ?: "",type = rawData.type ?: ""
                        )
                        navigatorContent.push(StockFormScreen(rawData.id, dataToSend))
                    }
                    is InsuranceIdData -> {
                        val attachments = rawData.files?.map { it.toAttachment() }
                        val dataToSend = InsuranceModel(
                            type = rawData.type ?: "", name = rawData.name ?: "", policyNumber = rawData.policyNumber ?: "",
                            companyName = rawData.companyName ?: "", coveragePeriod = rawData.coveragePeriod.toString(),
                            coverageAmount = rawData.coverageAmount ?: Money(0), conDate = rawData.conDate ?: "",
                            expDate = rawData.expDate ?: "", description = rawData.description ?: "",
                            attachments = attachments ?: emptyList()
                        )
                        navigatorContent.push(InsuranceFormScreen(rawData.id, dataToSend))
                    }
                    is BuildingIdData -> {
                        val attachments = rawData.files?.map { it.toAttachment() }
                        val insData = rawData.ins?.map { InsRefModel(insId = it.insId, insName = it.insName) }
                        val refData = rawData.referenceIds?.map { RefModel(areaName = it.refName, areaId = it.refId) }
                        val dataToSend = BuildingModel(
                            buildingName = rawData.name ?: "", area = rawData.area ?: 0.0, amount = rawData.amount ?: Money(0),
                            description = rawData.description ?: "", attachments = attachments ?: emptyList(),
                            locationAddress = rawData.location?.address ?: "", locationSubDistrict = rawData.location?.subDistrict ?: "",
                            locationDistrict = rawData.location?.district ?: "", locationProvince = rawData.location?.province ?: "",
                            locationPostalCode = rawData.location?.postalCode ?: "", insIds = insData ?: emptyList(),
                            referenceIds = refData ?: emptyList(), type = rawData.type ?: ""
                        )
                        navigatorContent.push(BuildingFormScreen(rawData.id ?: "", dataToSend))
                    }
                    is LandIdData -> {
                        val attachments = rawData.files?.map { it.toAttachment() }
                        val refData = rawData.ref?.map { RefModel(areaName = it.refName, areaId = it.refId) }
                        val dataToSend = LandModel(
                            landName = rawData.name ?: "", area = rawData.area ?: 0.0, amount = rawData.amount ?: Money(0),
                            description = rawData.description ?: "", attachments = attachments ?: emptyList(),
                            locationAddress = rawData.location?.address ?: "", locationSubDistrict = rawData.location?.subDistrict ?: "",
                            locationDistrict = rawData.location?.district ?: "", locationProvince = rawData.location?.province ?: "",
                            locationPostalCode = rawData.location?.postalCode ?: "", referenceIds = refData ?: emptyList(),
                            deedNum = rawData.deedNum ?: ""
                        )
                        navigatorContent.push(LandFormScreen(rawData.id, dataToSend))
                    }
                }
            }
        )
    }
}

@Composable
private fun AssetLoadError(message: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(horizontal = 24.dp),
        ) {
            Text(message, textAlign = TextAlign.Center, color = LightAsset)
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = LightAsset),
            ) {
                Text("ลองใหม่", color = LightSoftWhite)
            }
        }
    }
}

private fun assetErrorMessage(error: AppError): String = when (error) {
    AppError.Unauthorized -> "เซสชันหมดอายุ กรุณาเข้าสู่ระบบใหม่"
    AppError.NotFound -> "ไม่พบรายการทรัพย์สิน"
    is AppError.Network -> "เชื่อมต่อเซิร์ฟเวอร์ไม่ได้ กรุณาลองใหม่"
    is AppError.Unknown -> "โหลดทรัพย์สินไม่สำเร็จ กรุณาลองใหม่"
}

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
import cafe.adriel.voyager.navigator.Navigator
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
import com.wealthvault.core.utils.LocalRootNavigator
import com.wealthvault.core.utils.formatAmount
import com.wealthvault.core.utils.getScreenModel
import org.jetbrains.compose.resources.painterResource

// Import Components ของฝั่ง Financial
import com.wealthvault.financiallist.ui.FinancialListTemplate
import com.wealthvault.financiallist.ui.asset.form.account.BankAccountFormScreen
import com.wealthvault.financiallist.ui.asset.form.building.BuildingFormScreen
import com.wealthvault.financiallist.ui.asset.form.cash.CashFormScreen
import com.wealthvault.financiallist.ui.asset.form.insurance.InsuranceFormScreen
import com.wealthvault.financiallist.ui.asset.form.investment.StockFormScreen
import com.wealthvault.financiallist.ui.asset.form.land.LandFormScreen
import com.wealthvault.financiallist.ui.component.ConfirmDeleteDialog
import com.wealthvault.financiallist.ui.component.DetailDialog
import com.wealthvault.financiallist.ui.component.DetailImageRow
import com.wealthvault.financiallist.ui.component.DetailRow
import com.wealthvault.financiallist.ui.component.ExpandableCategoryCard
import com.wealthvault.financiallist.ui.component.RealItemCard
import com.wealthvault.financiallist.ui.component.ShareTargetList
import com.wealthvault.financiallist.ui.shareasset.ShareAssetScreen
import com.wealthvault.insurance_api.model.GetInsuranceData
import com.wealthvault.insurance_api.model.InsuranceIdData
import com.wealthvault.investment_api.model.GetInvestmentData
import com.wealthvault.investment_api.model.InvestmentIdData
import com.wealthvault.land_api.model.GetLandData
import com.wealthvault.land_api.model.LandIdData
import com.wealthvault_final.`financial-asset`.Imagepicker.toAttachment
import com.wealthvault_final.`financial-asset`.model.BankAccountModel
import com.wealthvault_final.`financial-asset`.model.BuildingModel
import com.wealthvault_final.`financial-asset`.model.CashModel
import com.wealthvault_final.`financial-asset`.model.InsRefModel
import com.wealthvault_final.`financial-asset`.model.InsuranceModel
import com.wealthvault_final.`financial-asset`.model.LandModel
import com.wealthvault_final.`financial-asset`.model.RefModel
import com.wealthvault_final.`financial-asset`.model.StockModel
import org.jetbrains.compose.resources.painterResource

class AssetScreen() : Screen {

    @Composable
    override fun Content() {
        // 🌟 1. ดึง Navigator ของ Tab ปัจจุบัน
        val navigator = LocalRootNavigator.current

        // 🌟 2. ดึง Navigator ตัวแม่สุด (Root) ที่อยู่หน้า App() มาใช้

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
            lands = lands,
            navigatorContent = navigator
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
    lands: List<GetLandData>,
    navigatorContent: Navigator
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
            navigatorContent = navigatorContent
        )
    }
}

@Composable
fun AssetDetailFetcherDialog(
    assetId: String,
    assetType: String,
    screenModel: AssetScreenModel,
    onDismiss: () -> Unit,
    navigatorContent: Navigator
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
        screenModel.getShareTarget(assetId,assetType)
        isLoading = false
    }

    val shareTargets by screenModel.shareTargets.collectAsState()

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
                    },
                    onEdit ={
                        val attachments = item.files?.map { it.toAttachment() }
                        println("IMAGE LIST: ${attachments}")
                        val dataToSend = BankAccountModel(
                            type = item.type,
                            name = item.name,
                            bankName = item.bankName,
                            bankId = item.bankAccount,
                            amount = item.amount,
                            description = item.description,
                            attachments = attachments ?: emptyList()

                        )
                        println("data to send cash: ${dataToSend}")
                        navigatorContent.push(BankAccountFormScreen(item.id,dataToSend))
                    },
                    onShare = {
                        navigatorContent.push(ShareAssetScreen("account", item.id))
                    }
                ) {
                    DetailRow("ธนาคาร", item.bankName)
                    DetailRow("เลขบัญชี", item.bankAccount)
                    DetailRow("ประเภท", item.type)
                    DetailRow("ยอดเงิน", "${formatAmount(item.amount)} บาท")
                    DetailRow("คำอธิบาย", item.description, isLast = item.files.isNullOrEmpty())
                    ShareTargetList("แชร์ไปยัง",shareTargets)
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
                    },
                    onEdit ={
                        val attachments = item.files?.map { it.toAttachment() }
                        println("IMAGE LIST: ${attachments}")
                        val dataToSend = CashModel(
                            cashName = item.name,
                            amount = item.amount,
                            description = item.description,
                            attachments = attachments ?: emptyList()
                        )
                        println("data to send cash: ${dataToSend}")
                        navigatorContent.push(CashFormScreen(item.id,dataToSend))
                    },
                    onShare = {
                        navigatorContent.push(ShareAssetScreen("cash", item.id))
                    }
                ) {
                    DetailRow("มูลค่า", "${formatAmount(item.amount)} บาท")
                    DetailRow("คำอธิบาย", item.description, isLast = item.files.isNullOrEmpty())
                    ShareTargetList("แชร์ไปยัง",shareTargets)
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
                    },
                    onEdit ={
                        val attachments = item.files?.map { it.toAttachment() }
                        println("IMAGE LIST: ${attachments}")
                        val dataToSend = StockModel(
                            stockName = item.name,
                            quantity = item.quantity,
                            costPerPrice = item.costPerPrice,
                            description = item.description,
                            attachments = attachments ?: emptyList(),
                            stockSymbol = "",
                            brokerName = item.brokerName,

                        )
                        println("data to send cash: ${dataToSend}")
                        navigatorContent.push(StockFormScreen(item.id,dataToSend))
                    },
                    onShare = {
                        navigatorContent.push(ShareAssetScreen("investment", item.id))
                    }
                ) {
                    DetailRow("โบรกเกอร์", item.brokerName)
                    DetailRow("จำนวน", "${formatAmount(item.quantity)}")
                    DetailRow("ราคาทุนต่อหน่วย", "${formatAmount(item.costPerPrice)} บาท")
                    DetailRow("ประเภท", item.type)
                    DetailRow("คำอธิบาย", item.description, isLast = item.files.isNullOrEmpty())
                    ShareTargetList("แชร์ไปยัง",shareTargets)
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
                    },
                    onEdit ={
                        val attachments = item.files?.map { it.toAttachment() }
                        println("IMAGE LIST: ${attachments}")
                        val dataToSend = InsuranceModel(
                            type = item.type,
                            name = item.name,
                            policyNumber = item.policyNumber,
                            companyName = item.companyName,
                            coveragePeriod = item.coveragePeriod.toString(),
                            coverageAmount = item.coverageAmount,
                            conDate = item.conDate,
                            expDate = item.expDate,
                            description = item.description,
                            attachments = attachments ?: emptyList()

                        )
                        println("data to send cash: ${dataToSend}")
                        navigatorContent.push(InsuranceFormScreen(item.id,dataToSend))
                    },
                    onShare = {
                        navigatorContent.push(ShareAssetScreen("insurance", item.id))
                    }
                ) {
                    DetailRow("เลขกรมธรรม์", item.policyNumber)
                    DetailRow("บริษัท", item.companyName)
                    DetailRow("วงเงินคุ้มครอง", "${formatAmount(item.coverageAmount)} บาท")
                    DetailRow("ระยะเวลาคุ้มครอง", "${item.coveragePeriod} ปี")
                    DetailRow("วันเริ่มสัญญา", formatThaiDate(item.conDate))
                    DetailRow("วันสิ้นสุดสัญญา", formatThaiDate(item.expDate))
                    DetailRow("คำอธิบาย", item.description, isLast = item.files.isNullOrEmpty())
                    ShareTargetList("แชร์ไปยัง",shareTargets)
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
                    },
                    onEdit ={
                        val attachments = item.files?.map { it.toAttachment() }
                        val insData = item.ins?.map {
                            InsRefModel(
                                insId = it.insId,
                                insName = it.insName
                            )
                        }
                        val refData = item.referenceIds?.map {
                            RefModel(
                                areaName = it.refName,
                                areaId = it.refId
                            )
                        }
                        println("IMAGE LIST: ${attachments}")
                        val dataToSend = BuildingModel(

                            buildingName = item.name?: "",
                            area = item.area?: 0.0,
                            amount = item.amount?: 0.0,
                            description = item.description?: "",
                            attachments = attachments ?: emptyList(),
                            locationAddress = item.location?.address ?: "",
                            locationSubDistrict = item.location?.subDistrict ?: "",
                            locationDistrict = item.location?.district ?: "",
                            locationProvince = item.location?.province ?: "",
                            locationPostalCode = item.location?.postalCode ?: "",
                            insIds = insData ?: emptyList(),
                            referenceIds = refData ?: emptyList(),
                            type = item.type ?: ""

                        )
                        println("data to send cash: ${dataToSend}")
                        navigatorContent.push(BuildingFormScreen(item.id ?: "",dataToSend))
                    },
                    onShare = {
                        navigatorContent.push(ShareAssetScreen("building", item.id ?: ""))
                    }
                ) {
                    DetailRow("ประเภท", item.type?: "")
                    DetailRow("พื้นที่", "${formatAmount(item.area?: 0)} ตร.ม.")
                    DetailRow("มูลค่าประเมิน", "${formatAmount(item.amount?: 0)} บาท")
                    val addressStr = item.location?.let { "${it.address} ${it.subDistrict} ${it.district} ${it.province} ${it.postalCode}".trim() } ?: "-"
                    DetailRow("ที่อยู่", addressStr)
                    DetailRow("คำอธิบาย", item.description?: "", isLast = item.files.isNullOrEmpty())
                    ShareTargetList("แชร์ไปยัง",shareTargets)
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
                    },
                    onEdit ={
                        val attachments = item.files?.map { it.toAttachment() }

                        val refData = item.ref?.map {
                            RefModel(
                                areaName = it.refName,
                                areaId = it.refName
                            )
                        }
                        println("IMAGE LIST: ${attachments}")
                        val dataToSend = LandModel(
                            landName = item.name,
                            area = item.area,
                            amount = item.amount,
                            description = item.description,
                            attachments = attachments ?: emptyList(),
                            locationAddress = item.location?.address ?: "",
                            locationSubDistrict = item.location?.subDistrict ?: "",
                            locationDistrict = item.location?.district ?: "",
                            locationProvince = item.location?.province ?: "",
                            locationPostalCode = item.location?.postalCode ?: "",
                            referenceIds = refData ?: emptyList(),
                            deedNum = item.deedNum,

                        )
                        println("data to send cash: ${dataToSend}")
                        navigatorContent.push(LandFormScreen(item.id,dataToSend))
                    },
                    onShare = {
                        navigatorContent.push(ShareAssetScreen("land", item.id))
                    }
                ) {
                    DetailRow("เลขโฉนด", item.deedNum)
                    DetailRow("ขนาดพื้นที่", "${formatAmount(item.area)} ตารางวา")
                    DetailRow("มูลค่าประเมิน", "${formatAmount(item.amount)} บาท")
                    val addressStr = item.location?.let { "${it.address} ${it.subDistrict} ${it.district} ${it.province} ${it.postalCode}".trim() } ?: "-"
                    DetailRow("ที่อยู่", addressStr)
                    DetailRow("คำอธิบาย", item.description, isLast = item.files.isNullOrEmpty())
                    ShareTargetList("แชร์ไปยัง",shareTargets)
                    DetailImageRow(files = item.files)
                }
            }
        }
    }
}

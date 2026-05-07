package com.wealthvault_final.`financial-asset`.ui.share

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_back
import com.wealthvault.core.generated.resources.ic_common_bin
import com.wealthvault.core.generated.resources.ic_common_calendar
import com.wealthvault.core.generated.resources.ic_common_plus
import com.wealthvault.core.generated.resources.ic_form_check
import com.wealthvault.core.generated.resources.ic_form_email_outline
import com.wealthvault.core.generated.resources.ic_nav_profile
import com.wealthvault.core.generated.resources.ic_nav_social
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault.core.theme.LightText
import com.wealthvault.core.theme.RedErr
import com.wealthvault.core.utils.formatThaiDate
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.group_api.model.GetAllGroupData
import com.wealthvault.`user-api`.model.FriendData
import com.wealthvault_final.`financial-asset`.model.BankAccountModel
import com.wealthvault_final.`financial-asset`.model.BuildingModel
import com.wealthvault_final.`financial-asset`.model.CashModel
import com.wealthvault_final.`financial-asset`.model.InsuranceModel
import com.wealthvault_final.`financial-asset`.model.LandModel
import com.wealthvault_final.`financial-asset`.model.ShareInfo
import com.wealthvault_final.`financial-asset`.model.ShareTo
import com.wealthvault_final.`financial-asset`.model.StockModel
import com.wealthvault_final.`financial-asset`.ui.bankaccount.summary.BankAccountSummaryScreen
import com.wealthvault_final.`financial-asset`.ui.cash.summary.CashSummaryScreen
import com.wealthvault_final.`financial-asset`.ui.expense.summary.ExpenseSummaryScreen
import com.wealthvault_final.`financial-asset`.ui.insurance.summary.InsuranceSummaryScreen
import com.wealthvault_final.`financial-asset`.ui.realestate.building.summary.BuildingSummaryScreen
import com.wealthvault_final.`financial-asset`.ui.realestate.land.summary.LandSummaryScreen
import com.wealthvault_final.`financial-asset`.ui.stock.summary.SummaryScreen
import com.wealthvault_final.`financial-obligations`.model.ExpenseModel
import com.wealthvault_final.`financial-obligations`.model.LiabilityModel
import com.wealthvault_final.`financial-obligations`.ui.liability.summary.LiabilitySummaryScreen
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource

class ShareAssetScreen<T>(val request: T? = null) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<ShareAssetScreenModel<T>>()

        val formState by screenModel.formState.collectAsState() // 🌟 ดึง State หลักมา
        val friendState by screenModel.friendState.collectAsState()
        val groupState by screenModel.groupState.collectAsState()

        LaunchedEffect(Unit) {
            screenModel.initData(request)
        }

        ShareAssetContent(
            initialShareTo = formState.shareTo, // 🌟 โยนค่าเดิมเข้าไป
            onBackClick = { navigator.pop() },
            onNextClick = { shareTo ->
                screenModel.saveShareInfo(shareTo) // 🌟 บันทึกคนที่เราเลือกก่อนไปหน้าสรุป
                navigateToSummary(navigator, request, shareTo)
            },
            friendData = friendState,
            groupData = groupState
        )
    }
}

fun navigateToSummary(navigator: Navigator, request: Any?, shareTo: ShareTo) {
    when (request) {
        is StockModel -> navigator.push(SummaryScreen(request, shareTo))
        is BankAccountModel -> navigator.push(BankAccountSummaryScreen(request, shareTo))
        is InsuranceModel -> navigator.push(InsuranceSummaryScreen(request, shareTo))
        is CashModel -> navigator.push(CashSummaryScreen(request, shareTo))
        is LandModel -> navigator.push(LandSummaryScreen(request, shareTo))
        is BuildingModel -> navigator.push(BuildingSummaryScreen(request, shareTo))
        is ExpenseModel -> navigator.push(ExpenseSummaryScreen(request, shareTo))
        is LiabilityModel -> navigator.push(LiabilitySummaryScreen(request, shareTo))
        else -> println("Unknown request type")
    }
}

// 🌟 Component Custom Checkbox 🌟
@Composable
fun CustomCheckbox(isSelected: Boolean, onSelectedChange: (Boolean) -> Unit) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) LightPrimary else Color.Transparent)
            .border(
                width = 2.dp,
                color = if (isSelected) LightPrimary else Color.LightGray,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onSelectedChange(!isSelected) },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                painter = painterResource(Res.drawable.ic_form_check),
                contentDescription = null,
                tint = LightSoftWhite,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareAssetContent(
    initialShareTo: ShareTo? = null,
    onBackClick: () -> Unit = {},
    onNextClick: (ShareTo) -> Unit = {},
    friendData: List<FriendData>,
    groupData: List<GetAllGroupData>
) {
    val selectedFriends = remember {
        mutableStateListOf<ShareInfo>().apply {
            initialShareTo?.let { addAll(it.friend + it.group) }
        }
    }
    val selectedEmails = remember {
        mutableStateListOf<ShareInfo>().apply {
            initialShareTo?.let { addAll(it.email) }
        }
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    val openSheet = { showBottomSheet = true }

    var showEmailSheet by remember { mutableStateOf(false) }
    val openInputEmail = { showEmailSheet = true }

    // 🌟 1. State ควบคุมการแสดงผล Info Tooltip ของ "แชร์ให้คนที่ไม่มีบัญชี"
    var showEmailInfoTooltip by remember { mutableStateOf(false) }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = LightBg,
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 16.dp, top = 20.dp)
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
                    Column {
                        Text("แชร์ทรัพย์สิน", style = MaterialTheme.typography.titleLarge, color = LightPrimary)
                        Text("คุณต้องการให้ใครเห็นทรัพย์สินนี้ของคุณบ้าง?", style = MaterialTheme.typography.bodyMedium, color = LightPrimary.copy(alpha = 0.7f))
                    }
                }
            }
        },
        bottomBar = {
            Box(modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(24.dp)) {
                Button(
                    onClick = {
                        val dataToSend = ShareTo(
                            friend = selectedFriends.filter { it.typeData == "F" },
                            email = selectedEmails.toList(),
                            group = selectedFriends.filter { it.typeData == "G" },
                            shareAt = ""
                        )
                        onNextClick(dataToSend)
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LightPrimary)
                ) {
                    Text("ต่อไป", color = Color.White, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            // --- ส่วนที่ 1: เลือกเพื่อนหรือกลุ่ม ---
            SectionHeader(title = "เลือกเพื่อนหรือกลุ่มที่ต้องการแชร์", onAddClick = openSheet)

            Card(
                modifier = Modifier.fillMaxWidth().height(300.dp),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, LightBorder),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                LazyColumn(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (selectedFriends.isEmpty()) {
                        item {
                            Text(
                                "ยังไม่ได้เลือกเพื่อนหรือกลุ่ม",
                                modifier = Modifier.fillMaxWidth().height(200.dp).padding(16.dp),
                                textAlign = TextAlign.Center,
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        items(selectedFriends) { friend ->
                            ShareItemWithDelete(
                                data = friend,
                                onDelete = { selectedFriends.remove(friend) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- ส่วนที่ 2: แชร์ให้คนไม่มีบัญชี ---
            SectionHeader(
                title = "แชร์ให้คนที่ไม่มีบัญชี",
                showInfo = true,
                onInfoClick = { showEmailInfoTooltip = !showEmailInfoTooltip }, // 🌟 กด Info เพื่อเปิด/ปิด Tooltip
                onAddClick = openInputEmail
            )

            // 🌟 2. กล่องข้อความ Info Popup (แสดงเมื่อกด Icon Info)
            AnimatedVisibility(visible = showEmailInfoTooltip) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .border(1.dp, LightBorder, RoundedCornerShape(20.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "หากคนที่คุณต้องการให้เข้าถึงทรัพย์สินนี้\nยังไม่มีบัญชีของ Wealth & Vault\nหรือยังไม่ได้เพิ่มเพื่อน สามารถแชร์ผ่านอีเมลได้",
                        color = LightPrimary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth().height(240.dp),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, LightBorder),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                LazyColumn(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (selectedEmails.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(16.dp).height(100.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("ยังไม่มีการเพิ่มอีเมล", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    } else {
                        items(selectedEmails) { email ->
                            ShareItemWithDelete(
                                data = email,
                                onDelete = { selectedEmails.remove(email) }
                            )
                        }
                    }
                }
            }
        }

        // --- Bottom Sheets ---
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = LightBg
            ) {
                FriendSelectionList(
                    alreadySelected = selectedFriends.toList(),
                    friendData = friendData,
                    groupData = groupData,
                    onConfirm = { selectedItems ->
                        val newItems = selectedItems.filter { newItem ->
                            selectedFriends.none { it.userId == newItem.userId }
                        }
                        selectedFriends.addAll(newItems)

                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) showBottomSheet = false
                        }
                    }
                )
            }
        }

        if (showEmailSheet) {
            ModalBottomSheet(
                onDismissRequest = { showEmailSheet = false },
                containerColor = LightBg
            ) {
                AddEmailContent(
                    onConfirm = { email, date, apiDate ->
                        if (email.isNotBlank()) {
                            val isDuplicate = selectedEmails.any { it.userId == email }
                            if (!isDuplicate) {
                                selectedEmails.add(
                                    ShareInfo(
                                        name = email,
                                        userId = email,
                                        date = date,
                                        apiDate = apiDate,
                                        typeData = "E",
                                        subText = email
                                    )
                                )
                            }
                        }
                        showEmailSheet = false
                    }
                )
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, showInfo: Boolean = false, onInfoClick: () -> Unit = {}, onAddClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(title, color = LightPrimary, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            if (showInfo) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp).clickable { onInfoClick() }, // 🌟 เพิ่ม Event คลิกที่นี่
                    tint = LightPrimary.copy(alpha = 0.5f)
                )
            }
        }
        IconButton(onClick = onAddClick) {
            Icon(
                painter = painterResource(Res.drawable.ic_common_plus),
                contentDescription = "Add",
                tint = LightPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun ShareItemWithDelete(data: ShareInfo, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().background(LightBg),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LightSoftWhite),
        border = BorderStroke(1.dp, LightBorder),
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Image / Placeholder
            Box(
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(LightBg),
                contentAlignment = Alignment.Center
            ) {
                if (!data.profileUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = data.profileUrl,
                        contentDescription = "Profile",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    val icon = when (data.typeData) {
                        "E" -> painterResource(Res.drawable.ic_form_email_outline)
                        "G" -> painterResource(Res.drawable.ic_nav_social)
                        else -> painterResource(Res.drawable.ic_nav_profile)
                    }
                    if (data.typeData == "E") {
                        Icon(painter = painterResource(Res.drawable.ic_form_email_outline), contentDescription = null, tint = LightPrimary, modifier = Modifier.size(24.dp))
                    } else {
                        Icon(painter = icon, contentDescription = null, tint = LightPrimary, modifier = Modifier.size(24.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = data.name ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    color = LightText,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )

                if (data.typeData == "E") {
                    // 🌟 แบบของอีเมล: วันที่อยู่ใต้ชื่อเลย (ที่คุณแชมป์บอกว่าโอเคแล้ว)
                    if (data.date != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(data.date!!, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                    }
                } else {
                    // 🌟 แบบของเพื่อน/กลุ่ม: จับ Badge และ วันที่ มาเรียงในบรรทัดเดียวกัน
                    if (data.subText.isNotBlank() || data.date != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween // ดันวันที่ไปชิดขวา
                        ) {
                            // Badge (อยู่ซ้าย)
                            if (data.subText.isNotBlank()) {
                                Row(
                                    modifier = Modifier
                                        .weight(1f, fill = false) // กันข้อความยาวเกินแล้วดันวันที่ตกขอบ
                                        .background(LightBg, RoundedCornerShape(200.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val icon = if(data.typeData == "G") painterResource(Res.drawable.ic_nav_social) else painterResource(Res.drawable.ic_nav_profile)
                                    Icon(painter = icon, contentDescription = null, tint = LightPrimary, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = data.subText,
                                        color = LightPrimary,
                                        style = MaterialTheme.typography.labelMedium,
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.width(1.dp))
                            }

                            // วันที่แชร์ล่วงหน้า (อยู่ขวา)
                            if (data.date != null) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = data.date!!,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.Gray,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // ปุ่มลบ (ถังขยะ)
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(
                    painter = painterResource(Res.drawable.ic_common_bin),
                    contentDescription = "Delete",
                    tint = RedErr,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun FriendSelectionList(
    alreadySelected: List<ShareInfo>,
    friendData: List<FriendData>,
    groupData: List<GetAllGroupData>,
    onConfirm: (List<ShareInfo>) -> Unit
) {
    val allSelectableData = remember(friendData, groupData) {
        val friends = friendData.map {
            ShareInfo(
                name = it.username,
                userId = it.id!!,
                typeData = "F",
                subText = it.email ?: "",
                profileUrl = it.profile
            )
        }
        val groups = groupData.map {
            ShareInfo(
                name = it.groupName,
                userId = it.id!!,
                typeData = "G",
                subText = "${it.memberCount ?: 0}",
                profileUrl = it.groupProfile
            )
        }
        friends + groups
    }

    // กรองข้อมูลที่เคยเลือกไปแล้วออก จะได้ไม่แสดงซ้ำ
    val availableData = remember(allSelectableData, alreadySelected) {
        allSelectableData.filter { item ->
            alreadySelected.none { it.userId == item.userId }
        }
    }

    val tempSelected = remember { mutableStateListOf<ShareInfo>() }

    var isDateChecked by remember { mutableStateOf(false) }
    var globalDate by remember { mutableStateOf<String?>(null) }
    var globalApiDate by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp).navigationBarsPadding()) {

        Text("เลือกเพื่อนหรือกลุ่ม", style = MaterialTheme.typography.titleMedium, color = LightPrimary)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f, fill = false),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // วนลูปแสดงเฉพาะคนที่ยังไม่ได้ถูกเลือกในรอบก่อน
            items(availableData) { item ->
                val isChecked = tempSelected.any { it.userId == item.userId }

                Surface(
                    onClick = {
                        if (isChecked) tempSelected.removeAll { it.userId == item.userId }
                        else tempSelected.add(item)
                    },
                    shape = RoundedCornerShape(16.dp),
                    color = LightSoftWhite,
                    border = BorderStroke(1.dp, LightBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(LightBg), contentAlignment = Alignment.Center) {
                            // 🌟 แก้ไข: เช็กให้ชัวร์ว่าไม่เป็น null และไม่เป็น String ว่าง ("")
                            if (!item.profileUrl.isNullOrBlank()) {
                                AsyncImage(model = item.profileUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                            } else {
                                val icon = if(item.typeData == "G") painterResource(Res.drawable.ic_nav_social) else painterResource(Res.drawable.ic_nav_profile)
                                Icon(painter = icon, contentDescription = null, tint = LightPrimary, modifier = Modifier.size(24.dp))
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.name ?: "", style = MaterialTheme.typography.bodyLarge, color = LightText)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.background(LightBg, RoundedCornerShape(200.dp)).padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val icon = if(item.typeData == "G") painterResource(Res.drawable.ic_nav_social) else painterResource(Res.drawable.ic_nav_profile)
                                Icon(painter = icon, contentDescription = null, tint = LightPrimary, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(item.subText, color = LightPrimary, style = MaterialTheme.typography.labelMedium)
                            }
                        }

                        CustomCheckbox(
                            isSelected = isChecked,
                            onSelectedChange = {
                                if (isChecked) tempSelected.removeAll { it.userId == item.userId }
                                else tempSelected.add(item)
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = LightBorder)
        Spacer(modifier = Modifier.height(10.dp))

        // --- ตั้งวันแชร์ล่วงหน้า (Global) ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { isDateChecked = !isDateChecked }.padding(vertical = 4.dp)
        ) {
            CustomCheckbox(
                isSelected = isDateChecked,
                onSelectedChange = { isDateChecked = it }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("ตั้งวันการแชร์ล่วงหน้า", color = LightPrimary, style = MaterialTheme.typography.bodyMedium)
        }

        AnimatedVisibility(visible = isDateChecked) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, LightBorder, RoundedCornerShape(12.dp))
                    .background(LightSoftWhite)
                    .clickable { showDatePicker = true }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = globalDate ?: "เลือกวันที่",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (globalDate != null) LightText else Color.Gray.copy(0.7f)
                )
                Icon(painterResource(Res.drawable.ic_common_calendar), contentDescription = "Select Date", tint = LightPrimary, modifier = Modifier.size(24.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val finalDate = if (isDateChecked) globalDate else null
                val finalApiDate = if (isDateChecked) globalApiDate else null
                val finalizedList = tempSelected.map { it.copy(date = finalDate, apiDate = finalApiDate) }
                onConfirm(finalizedList)
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = tempSelected.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(
                containerColor = LightPrimary,
                disabledContainerColor = Color.LightGray.copy(alpha = 0.5f),
                disabledContentColor = Color.White
            )
        ) {
            Text("เพิ่ม", color = Color.White, style = MaterialTheme.typography.titleMedium)
        }
    }

    if (showDatePicker) {
        CustomDatePickerDialog(
            onDismiss = { showDatePicker = false },
            onDateConfirm = { apiStr, thaiStr ->
                globalApiDate = apiStr
                globalDate = thaiStr
                showDatePicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePickerDialog(onDismiss: () -> Unit, onDateConfirm: (String, String) -> Unit) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        colors = DatePickerDefaults.colors(containerColor = Color.White),
        confirmButton = {
            TextButton(onClick = {
                val millis = datePickerState.selectedDateMillis
                if (millis != null) {
                    val localDate = Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.UTC)
                    val day = localDate.dayOfMonth.toString().padStart(2, '0')
                    val month = localDate.monthNumber.toString().padStart(2, '0')
                    val engYear = localDate.year.toString()

                    val apiDateStr = "$engYear-$month-$day"
                    val thaiDateStr = formatThaiDate(apiDateStr)

                    onDateConfirm(apiDateStr, thaiDateStr)
                } else {
                    onDismiss()
                }
            }) { Text("ตกลง", color = LightPrimary) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("ยกเลิก", color = Color.Gray) }
        }
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                selectedDayContainerColor = LightPrimary,
                todayDateBorderColor = LightPrimary,
                todayContentColor = LightPrimary
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEmailContent(onConfirm: (email: String, date: String?, apiDate: String?) -> Unit) {
    var emailText by remember { mutableStateOf("") }
    var isDateChecked by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<String?>(null) }
    var selectedApiDate by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp).navigationBarsPadding()) {

        Text("เพิ่มอีเมลผู้รับ", style = MaterialTheme.typography.titleMedium, color = LightPrimary)
        Text("ระบบจะส่งคำเชิญไปยังอีเมลนี้เพื่อให้เข้าถึงข้อมูล", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Spacer(modifier = Modifier.height(20.dp))

        Column {
            Text(text = "อีเมล", style = MaterialTheme.typography.bodyMedium, color = LightPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = emailText,
                onValueChange = { emailText = it },
                modifier = Modifier.fillMaxWidth().border(1.dp, LightBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                placeholder = { Text("example@gmail.com", color = Color.LightGray) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = LightSoftWhite,
                    unfocusedContainerColor = LightSoftWhite,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = LightText
                )
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // 🌟 เพิ่ม Checkbox "ตั้งวันการแชร์ล่วงหน้า" สำหรับ Email Flow
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { isDateChecked = !isDateChecked }.padding(vertical = 4.dp)
        ) {
            CustomCheckbox(
                isSelected = isDateChecked,
                onSelectedChange = { isDateChecked = it }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("ตั้งวันการแชร์ล่วงหน้า", color = LightPrimary, style = MaterialTheme.typography.bodyMedium)
        }

        AnimatedVisibility(visible = isDateChecked) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, LightBorder, RoundedCornerShape(12.dp))
                    .background(LightSoftWhite)
                    .clickable { showDatePicker = true }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedDate ?: "เลือกวันที่",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (selectedDate != null) LightText else Color.Gray.copy(0.7f)
                )
                Icon(painterResource(Res.drawable.ic_common_calendar), contentDescription = null, tint = LightPrimary, modifier = Modifier.size(24.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val finalDate = if (isDateChecked) selectedDate else null
                val finalApiDate = if (isDateChecked) selectedApiDate else null
                onConfirm(emailText, finalDate, finalApiDate)
            },
            enabled = emailText.contains("@") && emailText.contains("."),
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = LightPrimary)
        ) {
            Text("เพิ่ม", color = Color.White, style = MaterialTheme.typography.titleMedium)
        }
    }

    if (showDatePicker) {
        CustomDatePickerDialog(
            onDismiss = { showDatePicker = false },
            onDateConfirm = { apiStr, thaiStr ->
                selectedApiDate = apiStr
                selectedDate = thaiStr
                showDatePicker = false
            }
        )
    }
}
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
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
import com.wealthvault_final.`financial-asset`.ui.insurance.summary.InsuranceSummaryScreen
import com.wealthvault_final.`financial-asset`.ui.realestate.building.summary.BuildingSummaryScreen
import com.wealthvault_final.`financial-asset`.ui.realestate.land.summary.LandSummaryScreen
import com.wealthvault_final.`financial-asset`.ui.stock.summary.SummaryScreen
import com.wealthvault_final.`financial-obligations`.model.ExpenseModel
import com.wealthvault_final.`financial-obligations`.model.LiabilityModel
import com.wealthvault_final.`financial-obligations`.ui.expense.summary.ExpenseSummaryScreen
import com.wealthvault_final.`financial-obligations`.ui.liability.summary.LiabilitySummaryScreen
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

val WealthVaultBrown = Color(0xFFB37E61)
val WealthVaultBackground = Color(0xFFFFF8F3)
val WealthVaultCardHeader = Color(0xFF6D4C41)


class ShareAssetScreen<T>(val request: T? = null) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<ShareAssetScreenModel<T>>()

        val friendState by screenModel.friendState.collectAsState()
        val groupState by screenModel.groupState.collectAsState()
        LaunchedEffect(Unit) {
            println("Test123"+ request)
            screenModel.initData(request)
        }

        ShareAssetContent(
            onBackClick = { navigator.pop() },
            onNextClick = { shareTo ->

                navigateToSummary(navigator, request,shareTo)
            },
            // ✅ 2. ส่งค่าที่ถูก Observe แล้วเข้าไปแทน
            friendData = friendState,
            groupData = groupState
        )
    }
}

fun navigateToSummary(navigator: Navigator, request: Any?,shareTo: ShareTo) {
    println("data type ${request}")
    when (request) {
        is StockModel -> navigator.push(SummaryScreen(request,shareTo))
        is BankAccountModel -> navigator.push(BankAccountSummaryScreen(request, shareTo))
        is InsuranceModel -> navigator.push(InsuranceSummaryScreen(request, shareTo))
        is CashModel -> navigator.push(CashSummaryScreen(request,shareTo))
        is LandModel -> navigator.push(LandSummaryScreen(request,shareTo))
        is BuildingModel -> navigator.push(BuildingSummaryScreen(request, shareTo))
        is ExpenseModel -> navigator.push(ExpenseSummaryScreen(request, shareTo))
        is LiabilityModel -> navigator.push(LiabilitySummaryScreen(request, shareTo))

        else -> {
            println("Unknown request type")
        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareAssetContent(
    onBackClick: () -> Unit = {},
    onNextClick: (ShareTo) -> Unit = {},
    onAddFriendClick: () -> Unit = {},
    onAddExternalClick: () -> Unit = {},
    friendData: List<FriendData>,
    groupData: List<GetAllGroupData>
) {

    val selectedFriends = remember { mutableStateListOf<ShareInfo>() }

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    val openSheet = { showBottomSheet = true }

    var showEmailSheet by remember { mutableStateOf(false) }
    val selectedEmails = remember { mutableStateListOf<ShareInfo>() }
    val openInputEmail = { showEmailSheet = true }


    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        containerColor = Color(0xFFFFF8F3),
        topBar = {
            // ส่วน Header: ย้อนกลับ + หัวข้อ
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp), // ✅ ใส่ Gap ตรงนี้
                modifier = Modifier.padding(14.dp)
            ){
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = WealthVaultBrown)
                }
                Column() {

                    Text("แชร์ทรัพย์สิน", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = WealthVaultBrown)
                    Text("คุณต้องการให้ใครเห็นทรัพย์สินนี้ของคุณบ้าง?", fontSize = 14.sp, color = WealthVaultBrown.copy(alpha = 0.7f))
                }
            }
        },
        bottomBar = {
            // ปุ่ม "ต่อไป" แบบ Fixed
            Box(modifier = Modifier.padding(16.dp)) {
                Button(
                    onClick = {

                        val dataToSend = ShareTo(
                            friend = selectedFriends.filter { it.typeData == "F" },
                            email = selectedEmails.toList(),
                            group = selectedFriends.filter { it.typeData == "G" },
                            shareAt = "" // ควรใช้ฟังก์ชันดึงเวลาปัจจุบันใน ScreenModel
                        )
                        onNextClick(dataToSend)
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = WealthVaultBrown)
                ) {
                    Text("ต่อไป", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)

        ) {
            // --- ส่วนที่ 1: เลือกเพื่อนหรือกลุ่ม ---
            SectionHeader(title = "เลือกเพื่อนหรือกลุ่มที่ต้องการแชร์", onAddClick = openSheet)


            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    // ✅ กำหนดเพดานความสูงไว้ที่ 300.dp (ประมาณ 4 คน)
                    // ถ้าข้อมูลยังไม่ถึง 300.dp กล่องจะเตี้ยตามจริง
                    // ถ้าเกิน 300.dp มันจะหยุดขยายและเปิดให้เลื่อน (Scroll)
                    .height(300.dp),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color(0xFFF0E0D6)),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                // ✅ ใช้ LazyColumn แทน Column + forEach
                LazyColumn(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp) // ระยะห่างระหว่างไอเทม
                ) {
                    if (selectedFriends.isEmpty()) {
                        item {
                            Text(
                                "ยังไม่ได้เลือกเพื่อนหรือกลุ่ม",
                                modifier = Modifier
                                    .fillMaxWidth() // เพิ่มตัวนี้
                                    .height(200.dp) // ลดจาก 350 เหลือ 200 จะดูดีกว่า
                                    .padding(16.dp),
                                textAlign = TextAlign.Center,
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        // ✅ ใช้ items() เพื่อวนลูปรายการเพื่อน
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
                onAddClick = openInputEmail
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    // ✅ กำหนดความสูงสูงสุด (ประมาณ 3-4 รายการ) ถ้าเกินนี้จะ Scroll ภายใน Card
                    .height(240.dp),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color(0xFFF0E0D6)),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                // ✅ ใช้ LazyColumn แทน Column เพื่อให้ Scroll ได้ในตัว
                LazyColumn(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp) // ระยะห่างระหว่างไอเทม
                ){
                    if (selectedEmails.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("ยังไม่มีการเพิ่มอีเมล", color = Color.Gray)
                            }
                        }
                    } else {
                        // ✅ ใช้ items เพื่อแสดงรายการอีเมล
                        items(selectedEmails) { email ->
                            ShareItemWithDelete(
                                data = email,
                                onDelete = { selectedEmails.remove(email) }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp)) // เผื่อที่ให้ scroll พ้นปุ่ม


            Spacer(modifier = Modifier.height(100.dp)) // เผื่อที่ให้ scroll พ้นปุ่ม

            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showBottomSheet = false },
                    sheetState = sheetState,
                    containerColor = Color.White
                ) {
                    FriendSelectionList(
                        // 1. ส่ง List ของ ShareInfo ไปตรงๆ (ไม่ต้อง map แค่ name)
                        alreadySelected = selectedFriends.toList(),
                        friendData = friendData,
                        groupData = groupData,
                        onConfirm = { selectedItems -> // selectedItems จะเป็น List<ShareInfo>
                            // 2. ล้างค่าเก่าแล้วเพิ่มค่าใหม่จาก Object ที่ได้มา
                            selectedFriends.clear()
                            selectedFriends.addAll(selectedItems)

                            // 3. ปิด Sheet ตามปกติ
                            scope.launch {
                                sheetState.hide()
                            }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    showBottomSheet = false
                                }
                            }
                        }
                    )

                }
            }
            if (showEmailSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showEmailSheet = false },
                    containerColor = Color.White
                ) {
                    AddEmailContent(
                        onConfirm = { email, date ->
                            if (email.isNotBlank()) {
                                // 1. เช็กว่าอีเมลนี้เคยถูกเพิ่มไปหรือยัง (หาจาก userId ที่เราตั้งเป็นอีเมล)
                                val isDuplicate = selectedEmails.any { it.userId == email }

                                if (!isDuplicate) {
                                    // 2. ถ้ายังไม่ซ้ำ ให้เพิ่มเข้าไปใน List โดยเก็บทั้งอีเมลและวันที่
                                    selectedEmails.add(
                                        ShareInfo(
                                            name = email,    // ให้แสดงชื่อเป็นอีเมล
                                            userId = email,  // ใช้ตัวอีเมลเป็น ID เพื่อไว้เช็กซ้ำ
                                            date = date      // ✅ เซฟวันที่เก็บไว้ด้วย
                                        )
                                    )
                                }
                            }
                            showEmailSheet = false // ปิด BottomSheet
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    showInfo: Boolean = false,
    onAddClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(title, color = WealthVaultBrown, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            if (showInfo) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = WealthVaultBrown.copy(alpha = 0.5f)
                )
            }
        }
        IconButton(onClick = onAddClick) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = WealthVaultBrown, modifier = Modifier.size(28.dp))
        }
    }
}


// สร้าง Data Class เพื่อให้จัดการข้อมูลได้ง่ายขึ้น

@Composable
fun ShareItemWithDelete(
    data: ShareInfo,
    onDelete: () -> Unit // Callback เมื่อกดลบ
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Box
            Box(
                modifier = Modifier.size(48.dp).background(Color(0xFFD9D9D9), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (data.userId == "") Icon(Icons.Default.Email, null, tint = WealthVaultBrown)
                else Icon(Icons.Default.Groups, null, tint = Color.Gray)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(data.name ?: "", fontSize = 16.sp, color = Color.Black)
                Text(data.date ?: "", fontSize = 16.sp, color = Color.Black)

            }

            // ปุ่มลบ
            IconButton(onClick = onDelete) { // ✅ เรียกใช้ onDelete
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null,
                    tint = Color(0xFFD32F2F).copy(alpha = 0.7f)
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
    // 1. เตรียมข้อมูล
    val allSelectableData = remember(friendData, groupData) {
        val friends = friendData.map { ShareInfo(name = it.username, userId = it.id, typeData = "F") }
        val groups = groupData.map { ShareInfo(name = it.groupName, userId = it.id, typeData = "G") }
        friends + groups
    }

    val tempSelected = remember {
        mutableStateListOf<ShareInfo>().apply { addAll(alreadySelected) }
    }

    // 🕒 2. State สำหรับระบุว่า "กำลังแก้ปฏิทินของใครอยู่" (เก็บเป็น Object ShareInfo เลย จะได้ไม่งง)
    var editingUserDate by remember { mutableStateOf<ShareInfo?>(null) }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(
            text = "เลือกเพื่อนหรือกลุ่ม",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xFFC08064)
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f, fill = false),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(allSelectableData) { item ->
                val selectedItem = tempSelected.find { it.userId == item.userId }
                val isChecked = selectedItem != null

                Surface(
                    onClick = {
                        if (isChecked) {
                            tempSelected.removeAll { it.userId == item.userId }
                        } else {
                            tempSelected.add(item)
                            editingUserDate = item // เปิดปฏิทินทันทีที่ติ๊กเลือก
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isChecked) Color(0xFFF2E8E1) else Color(0xFFFFF8F3),
                    border = if (isChecked) BorderStroke(1.dp, Color(0xFFC08064)) else null,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(item.name ?: "", modifier = Modifier.weight(1f), fontSize = 16.sp)
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = null,
                                colors = CheckboxDefaults.colors(checkedColor = Color(0xFFC08064))
                            )
                        }

                        // แสดงกล่องปฏิทินจำลอง
                        AnimatedVisibility(visible = isChecked) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                                    .background(Color.White)
                                    .clickable { editingUserDate = selectedItem } // กดแก้ปฏิทินทีหลังได้
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val hasDate = selectedItem?.date != null
                                Text(
                                    text = if (hasDate) selectedItem!!.date!! else "ว/ด/ป",
                                    fontSize = 14.sp,
                                    color = if (hasDate) Color.Black else Color.Gray
                                )
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Select Date",
                                    tint = Color(0xFFC08064),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onConfirm(tempSelected.toList()) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC08064))
        ) {
            Text("เลือก (${tempSelected.size}) รายการ", color = Color.White)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }

    // 🕒 3. เรียกใช้งาน Custom Composable ปฏิทินแบบคลีนๆ
    if (editingUserDate != null) {
        CustomDatePickerDialog(
            onDismiss = { editingUserDate = null }, // ปิดป๊อปอัพ
            onDateConfirm = { dateStr ->
                // นำวันที่มาอัปเดตใส่ List
                val index = tempSelected.indexOfFirst { it.userId == editingUserDate?.userId }
                if (index != -1) {
                    tempSelected[index] = tempSelected[index].copy(date = dateStr)
                }
                editingUserDate = null // เซฟเสร็จแล้วปิดป๊อปอัพ
            }
        )
    }
}

// ---------------------------------------------------------
// 🚀 แยก DatePickerDialog ออกมาเป็นฟังก์ชันต่างหาก
// ---------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePickerDialog(
    onDismiss: () -> Unit,
    onDateConfirm: (String) -> Unit
) {
    // ย้าย DatePickerState มาไว้ที่นี่ ทำให้ State ถูก Reset ใหม่ทุกครั้งที่เปิดป๊อปอัพ
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val millis = datePickerState.selectedDateMillis
                if (millis != null) {
                    val dateStr = Instant.fromEpochMilliseconds(millis)
                        .toLocalDateTime(TimeZone.currentSystemDefault())
                        .date.toString()

                    onDateConfirm(dateStr) // ส่งวันที่กลับไปให้หน้าจอหลัก
                } else {
                    onDismiss()
                }
            }) {
                Text("ตกลง", color = Color(0xFFC08064))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("ยกเลิก", color = Color.Gray)
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEmailContent(
    onConfirm: (email: String, date: String?) -> Unit
) {
    var emailText by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<String?>(null) }

    // 🕒 State ควบคุมการเปิด/ปิด ปฏิทิน
    var showDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .navigationBarsPadding() // เว้นพื้นที่คีย์บอร์ด
    ) {
        Text(
            "เพิ่มอีเมลผู้รับ",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFC08064) // WealthVaultBrown
        )
        Text(
            "ระบบจะส่งคำเชิญไปยังอีเมลนี้เพื่อให้เข้าถึงข้อมูล",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 1. ช่องกรอกอีเมล
        OutlinedTextField(
            value = emailText,
            onValueChange = { emailText = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("อีเมล (example@gmail.com)") },
            placeholder = { Text("ระบุอีเมลผู้รับ") },
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFC08064),
                focusedLabelColor = Color(0xFFC08064),
                cursorColor = Color(0xFFC08064)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. กล่องจำลองสำหรับกดเลือกวันที่
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(
                    1.dp,
                    if (selectedDate != null) Color(0xFFC08064) else Color.Gray,
                    RoundedCornerShape(16.dp)
                )
                .clickable { showDatePicker = true } // เปิดปฏิทิน
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedDate ?: "เลือกวันที่ (ว/ด/ป)",
                fontSize = 16.sp,
                color = if (selectedDate != null) Color.Black else Color.Gray
            )
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Select Date",
                tint = Color(0xFFC08064)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 3. ปุ่มเพิ่มรายชื่อ
        Button(
            onClick = { onConfirm(emailText, selectedDate) },
            enabled = emailText.contains("@") && emailText.contains(".") && selectedDate != null,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFC08064),
                disabledContainerColor = Color(0xFFD7CCC8)
            )
        ) {
            Text("เพิ่มรายชื่อ", color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    // 🕒 4. เรียกใช้ CustomDatePickerDialog แยกออกมาให้โค้ดคลีน
    if (showDatePicker) {
        CustomDatePickerDialog(
            onDismiss = { showDatePicker = false },
            onDateConfirm = { dateStr ->
                selectedDate = dateStr // รับวันที่มาใส่ State
                showDatePicker = false // ปิดป๊อปอัพ
            }
        )
    }
}

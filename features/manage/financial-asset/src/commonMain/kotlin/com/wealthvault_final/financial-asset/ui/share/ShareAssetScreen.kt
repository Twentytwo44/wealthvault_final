package com.wealthvault_final.`financial-asset`.ui.share

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.launch

val WealthVaultBrown = Color(0xFFB37E61)
val WealthVaultBackground = Color(0xFFFFF8F3)
val WealthVaultCardHeader = Color(0xFF6D4C41)

class ShareAsset : Screen {
    @Composable
    override fun Content() {


        ShareAssetScreen()

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareAssetScreen(
    onBackClick: () -> Unit = {},
    onNextClick: () -> Unit = {},
    onAddFriendClick: () -> Unit = {},
    onAddExternalClick: () -> Unit = {}
) {
    val selectedFriends = remember { mutableStateListOf<FriendItemData>() }

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    val openSheet = { showBottomSheet = true }

    var showEmailSheet by remember { mutableStateOf(false) }
    val selectedEmails = remember { mutableStateListOf<String>() }
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
                    onClick = onNextClick,
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
                    .height(280.dp),
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
                                data = FriendItemData(name = email, isEmail = true),
                                onDelete = { selectedEmails.remove(email) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp)) // เผื่อที่ให้ scroll พ้นปุ่ม

            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showBottomSheet = false },
                    sheetState = sheetState,
                    containerColor = Color.White
                ) {
                    FriendSelectionList(
                        alreadySelected = selectedFriends.map { it.name }, // ส่งชื่อที่มีอยู่แล้วไป
                        onConfirm = { names ->
                            // ✅ ล้างค่าเก่าแล้วแอดค่าใหม่ที่เลือกมาทั้งหมด (หรือจะทำ logic merge ก็ได้)
                            selectedFriends.clear()
                            names.forEach { name ->
                                selectedFriends.add(FriendItemData(name = name))
                            }

                            // ปิด Sheet
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                showBottomSheet = false
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
                        onConfirm = { email ->
                            if (email.isNotBlank() && !selectedEmails.contains(email)) {
                                selectedEmails.add(email)
                            }
                            showEmailSheet = false
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
data class FriendItemData(
    val name: String,
    val groupCount: String? = null,
    val date: String? = null,
    val isEmail: Boolean = false
)

@Composable
fun ShareItemWithDelete(
    data: FriendItemData,
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
                if (data.isEmail) Icon(Icons.Default.Email, null, tint = WealthVaultBrown)
                else Icon(Icons.Default.Groups, null, tint = Color.Gray)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(data.name, fontSize = 16.sp, color = Color.Black)
                // ... badge ส่วน groupCount/date ...
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
    alreadySelected: List<String>, // รับรายชื่อที่เคยเลือกไปแล้วมาด้วย (เพื่อติ๊กไว้ก่อน)
    onConfirm: (List<String>) -> Unit
) {
    val mockFriends = listOf("Two", "Group 1", "Anawat", "Development Team", "Investment Club")

    // เก็บสถานะการติ๊ก Checkbox ไว้ในตัวแปรชั่วคราวภายใน Sheet
    val tempSelected = remember {
        mutableStateListOf<String>().apply { addAll(alreadySelected) }
    }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(
            "เลือกเพื่อนหรือกลุ่ม",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = WealthVaultBrown
        )
        Spacer(modifier = Modifier.height(16.dp))

        // ใช้ LazyColumn เผื่อรายชื่อเพื่อนยาว
        LazyColumn(
            modifier = Modifier.weight(1f, fill = false),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(mockFriends) { name ->
                val isChecked = tempSelected.contains(name)

                Surface(
                    onClick = {
                        if (isChecked) tempSelected.remove(name) else tempSelected.add(name)
                    },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isChecked) Color(0xFFF2E8E1) else Color(0xFFFFF8F3),
                    border = if (isChecked) BorderStroke(1.dp, WealthVaultBrown) else null,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(name, modifier = Modifier.weight(1f), fontSize = 16.sp)

                        // ✅ เพิ่ม Checkbox มาตรฐาน
                        androidx.compose.material3.Checkbox(
                            checked = isChecked,
                            onCheckedChange = null, // ให้ Surface จัดการ Click แทนเพื่อให้กดง่ายทั้งแถว
                            colors = androidx.compose.material3.CheckboxDefaults.colors(
                                checkedColor = WealthVaultBrown
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ ปุ่มยืนยันการเลือก
        Button(
            onClick = { onConfirm(tempSelected.toList()) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(containerColor = WealthVaultBrown)
        ) {
            Text("เลือก (${tempSelected.size}) คน", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}



@Composable
fun AddEmailContent(onConfirm: (String) -> Unit) {
    var emailText by remember { mutableStateOf("") }

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
            color = WealthVaultBrown
        )
        Text(
            "ระบบจะส่งคำเชิญไปยังอีเมลนี้เพื่อให้เข้าถึงข้อมูล",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(20.dp))

        androidx.compose.material3.OutlinedTextField(
            value = emailText,
            onValueChange = { emailText = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("อีเมล (example@gmail.com)") },
            placeholder = { Text("ระบุอีเมลผู้รับ") },
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                focusedBorderColor = WealthVaultBrown,
                focusedLabelColor = WealthVaultBrown,
                cursorColor = WealthVaultBrown
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onConfirm(emailText) },
            enabled = emailText.contains("@"), // เช็คเบื้องต้น
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = WealthVaultBrown)
        ) {
            Text("เพิ่มรายชื่อ", color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

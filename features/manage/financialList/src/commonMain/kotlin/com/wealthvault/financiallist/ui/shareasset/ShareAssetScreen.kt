package com.wealthvault.financiallist.ui.shareasset


import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.financiallist.ui.shareasset.component.AddEmailContent
import com.wealthvault.financiallist.ui.shareasset.component.FriendSelectionList
import com.wealthvault.financiallist.ui.shareasset.component.ShareItemWithDelete
import com.wealthvault.financiallist.ui.shareasset.model.FriendTargetModel
import com.wealthvault.financiallist.ui.shareasset.model.GroupTargetModel
import com.wealthvault.financiallist.ui.shareasset.model.ShareInfo
import com.wealthvault.financiallist.ui.shareasset.model.ShareTo
import kotlinx.coroutines.launch

val WealthVaultBrown = Color(0xFFB37E61)



// 💡 1. เติม data class ให้สมบูรณ์
data class ShareAssetScreen(val type: String, val id: String) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        // 💡 2. เอา <ShareAssetState> ออก เหลือแค่นี้ครับ
        val screenModel = getScreenModel<ShareScreenModel>()

        val friendState by screenModel.friendState.collectAsState()
        val groupState by screenModel.groupState.collectAsState()

        LaunchedEffect(Unit) {
            println("Test: type=$type, id=$id")
            screenModel.initData(id, type)

        }

        ShareAssetContent(
            onBackClick = { navigator.pop() },
            onNextClick = { shareTo -> // 🌟 รับค่า ShareTo เข้ามาตรงนี้
                screenModel.initShareData(shareTo) // 🌟 ส่งต่อให้ initShareData
                screenModel.submitShare(id, type)
//                navigator.pop()
            },
            friendData = friendState,
            groupData = groupState
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareAssetContent(
    onBackClick: () -> Unit = {},
    onNextClick: (ShareTo) -> Unit = {},
    friendData: List<FriendTargetModel> = emptyList(),
    groupData: List<GroupTargetModel> = emptyList(),
) {
//    val bottomBarState = LocalBottomBarState.current
//
//    // 🌟 2. สั่งปิดตอนเข้ามา และสั่งเปิดคืนตอนกดออก
//    DisposableEffect(Unit) {
//        bottomBarState.value = false // หน้าแชร์โผล่ปุ๊บ สั่งปิดเมนูปั๊บ! (จอเต็มทันที)
//
//        onDispose {
//            bottomBarState.value = true  // พอกด back หรือ pop ออกไป สั่งเปิดเมนูคืนด้วย
//        }
//    }

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
                            friend =selectedFriends.filter { it.typeData == "F" },
                            email = selectedEmails.toList(),
                            group = selectedFriends.filter { it.typeData == "G" },
                        )
                        print("data for share: ${dataToSend}")
                        onNextClick(dataToSend)
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = WealthVaultBrown)
                ) {
                    Text("ยืนยัน", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
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




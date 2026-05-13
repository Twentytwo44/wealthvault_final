package com.wealthvault.social.ui.profile

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.WealthVaultTheme
import com.wealthvault.core.utils.formatAmount
import com.wealthvault.core.utils.formatThaiDate
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.social.ui.components.profile.ExpandableCategoryCard
import com.wealthvault.social.ui.components.profile.ProfileHeader
import com.wealthvault.social.ui.components.profile.RealItemCard
import com.wealthvault.social.ui.components.profile.SmartAssetDetailDialog
import com.wealthvault.social.ui.components.space.SpaceTopBar
import com.wealthvault.`user-api`.model.FriendProfileData
import com.wealthvault.`user-api`.model.ItemPreview

class FriendProfileScreen(
    private val friendId: String,
    private val friendName: String
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<FriendProfileScreenModel>()

        val profileData by screenModel.profileData.collectAsState()
        val isLoading by screenModel.isLoading.collectAsState()
        val isRemoveSuccess by screenModel.isRemoveSuccess.collectAsState()
        val isSuccess by screenModel.isSuccess.collectAsState()
        val isAlreadySent by screenModel.isAlreadySent.collectAsState()

        var showRemoveConfirm by remember { mutableStateOf(false) }

        // 🌟 1. ดึง Lifecycle มา
        val lifecycleOwner = LocalLifecycleOwner.current

        // 🌟 2. ดัก ON_RESUME เพื่ออัปเดตข้อมูลโปรไฟล์เพื่อนเวลาสลับแอปหรือสลับหน้า
        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    println("🔄 FriendProfile ตื่นแล้ว! โหลดข้อมูลโปรไฟล์ของ $friendName ใหม่...")
                    screenModel.fetchProfile(friendId)
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }

        // 🌟 3. อันนี้เก็บไว้เหมือนเดิม! เอาไว้รีเฟรชหน้าจอทันทีตอนที่กดยืนยันการลบแล้ว Backend ตอบกลับมาว่าสำเร็จ
        LaunchedEffect(isRemoveSuccess) {
            if (isRemoveSuccess) {
                screenModel.fetchProfile(friendId) // 🔄 รีโหลดเพื่อให้ปุ่มกลับเป็น "เพิ่มเพื่อน"

                // 💡 Pro Tip: หากหน้าจอนี้มีการ recompose บ่อยๆ แนะนำให้เพิ่มฟังก์ชันเคลียร์สถานะด้วยครับ
                // เพื่อป้องกันไม่ให้มันวิ่งเข้ามาโหลด fetchProfile() ซ้ำรัวๆ
                // screenModel.resetRemoveState()
            }
        }

        WealthVaultTheme {
            Box(modifier = Modifier.fillMaxSize()) {
                FriendProfileContent(
                    friendName = friendName,
                    profileData = profileData,
                    isLoading = isLoading,
                    isSuccess = isSuccess,
                    isAlreadySent = isAlreadySent,
                    onBackClick = { navigator.pop() },
                    onRemoveFriendClick = { showRemoveConfirm = true },
                    onAddFriendClick = { screenModel.addFriend(friendId) }
                )

                // ส่วนของ AlertDialog ทำมาได้สวยงามและครอบคลุมดีแล้วครับ!
                if (showRemoveConfirm) {
                    AlertDialog(
                        onDismissRequest = { showRemoveConfirm = false },
                        containerColor = Color.White,
                        shape = RoundedCornerShape(20.dp),
                        title = { Text("ลบเพื่อน?", fontWeight = FontWeight.Bold, color = Color(0xFF3A2F2A)) },
                        text = { Text("คุณต้องการลบ $friendName ออกจากรายชื่อเพื่อนใช่หรือไม่?", color = Color.Gray) },
                        confirmButton = {
                            TextButton(onClick = {
                                showRemoveConfirm = false
                                screenModel.removeFriend(friendId) // สั่งลบ
                            }) {
                                Text("ลบออก", color = Color(0xFFE55A5A), fontWeight = FontWeight.Bold)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showRemoveConfirm = false }) {
                                Text("ยกเลิก", color = Color.Gray)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FriendProfileContent(
    friendName: String,
    profileData: FriendProfileData?,
    isLoading: Boolean,
    isSuccess: Boolean,
    isAlreadySent: Boolean,
    onBackClick: () -> Unit,
    onRemoveFriendClick: () -> Unit,
    onAddFriendClick: () -> Unit
) {
    val themeColor = Color(0xFFC27A5A)
    val redColor = Color(0xFFE55A5A)

    var selectedItem by remember { mutableStateOf<ItemPreview?>(null) }

    val userInfo = profileData?.userInfo
    val displayEmail = userInfo?.email ?: "กำลังโหลด..."
    val displayName = userInfo?.username?.takeIf { it.isNotBlank() }
        ?: userInfo?.firstName?.takeIf { it.isNotBlank() }
        ?: "ไม่ระบุชื่อ"

    // ดึงสถานะความเป็นเพื่อนมาเก็บไว้เช็ค
    val isFriend = userInfo?.isFriend == true

    val allItems = profileData?.itemPreview ?: emptyList()
    val liabilities = allItems.filter { it.type?.lowercase() == "liability" }
    val assets = allItems.filter { it.type?.lowercase() != "liability" }
    val fullName = listOfNotNull(
        userInfo?.firstName?.takeIf { it.isNotBlank() },
        userInfo?.lastName?.takeIf { it.isNotBlank() }
    ).joinToString(" ").ifEmpty { "-" }

    val birthDate = userInfo?.birthday?.let { formatThaiDate(it) } ?: "-"


    Box(modifier = Modifier.fillMaxSize().background(LightBg)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(top = 24.dp)
        ) {
            SpaceTopBar(title = "โปรไฟล์เพื่อน", onBackClick = onBackClick)
            HorizontalDivider(color = themeColor.copy(alpha = 0.3f), thickness = 1.dp)

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = themeColor)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(24.dp))

                        ProfileHeader(
                            name = displayName,
                            subtitle = displayEmail,
                            profileImageUrl = userInfo?.profile,
                            isFriend = isFriend,
                            username = userInfo?.username?.takeIf { it.isNotBlank() } ?: "-",
                            email = userInfo?.email ?: "-",
                            fullName = fullName,
                            phoneNumber = userInfo?.phoneNumber ?: "-",
                            birthDate = birthDate
                        )

                        Spacer(modifier = Modifier.height(32.dp))
                    }

                    if (assets.isNotEmpty()) {
                        item {
                            ExpandableCategoryCard(
                                title = "สินทรัพย์",
                                itemCount = assets.size,
                                themeColor = "asset",
                                initiallyExpanded = true
                            ) {
                                assets.forEach { item ->
                                    val detail = item.assetDetail

                                    val subLabel = when(item.type?.lowercase()) {
                                        "land" -> "เลขโฉนด"
                                        "insurance" -> "บริษัท"
                                        "investment" -> "สัญลักษณ์"
                                        "account" -> "ธนาคาร"
                                        else -> "ประเภท"
                                    }
                                    val subValue = when(item.type?.lowercase()) {
                                        "land" -> detail?.deedNum ?: "-"
                                        "insurance" -> detail?.companyName ?: "-"
                                        "investment" -> detail?.symbol ?: "-"
                                        "building" -> detail?.locationText ?: "-"
                                        "account" -> detail?.bankName ?: "-"
                                        else -> detail?.type ?: item.type?.uppercase() ?: "-"
                                    }
                                    val amountValue = when(item.type?.lowercase()) {
                                        "land", "building", "account" -> formatAmount(detail?.amount ?: 0.0)
                                        "insurance" -> formatAmount(detail?.coverageAmount ?: 0.0)
                                        "investment" -> formatAmount(detail?.amount ?: 0.0)
                                        else -> detail?.type ?: item.type?.uppercase() ?: "ASSET"
                                    }

                                    RealItemCard(
                                        title = detail?.name ?: "ไม่ระบุชื่อ",
                                        subtitleLabel = subLabel,
                                        subtitleValue = subValue,
                                        amountLabel = "มูลค่า/จำนวนเงิน",
                                        amountValue = amountValue,
                                        onClick = { selectedItem = item }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    if (liabilities.isNotEmpty()) {
                        item {
                            ExpandableCategoryCard(
                                title = "หนี้สิน",
                                itemCount = liabilities.size,
                                themeColor = "debt",
                                initiallyExpanded = true
                            ) {
                                liabilities.forEach { item ->
                                    val detail = item.assetDetail
                                    RealItemCard(
                                        title = detail?.name ?: "ไม่ระบุชื่อ",
                                        subtitleLabel = "เจ้าหนี้",
                                        subtitleValue = detail?.creditor ?: "-",
                                        amountLabel = "เงินต้น",
                                        amountValue = formatAmount(detail?.principal ?: 0.0),
                                        onClick = { selectedItem = item }
                                    )
                                }
                            }
                        }
                    }

                    if (assets.isEmpty() && liabilities.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                                Text("เพื่อนคนนี้ยังไม่ได้แชร์สินทรัพย์/หนี้สินใดๆ", color = Color.Gray, fontSize = 14.sp)
                            }
                        }
                    }

                    // 🌟 ย้ายส่วนของปุ่มจัดการเพื่อนมาไว้ที่นี่ครับ เป็น item ท้ายสุดของ LazyColumn
                    item {
                        if (profileData != null) {
                            val isAdding = isLoading

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 48.dp, bottom = 16.dp), // เพิ่ม padding ให้ห่างจากกล่องข้างบน
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (isFriend) {
                                    // 🌟 ปุ่มลบเพื่อน
                                    Text(
                                        text = if (isLoading) "กำลังลบ..." else "ลบเพื่อน",
                                        color = if (isLoading) redColor.copy(alpha = 0.5f) else redColor,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier
                                            .clickable(enabled = !isLoading) { onRemoveFriendClick() }
                                            .padding(8.dp)
                                    )
                                } else {
                                    // 🌟 ปุ่มเพิ่มเพื่อน
                                    val buttonText = when {
                                        isAlreadySent -> "คุณหรือเพื่อนได้ส่งคำขอหากันแล้ว เช็คได้ที่กล่องจดหมาย"
                                        isSuccess -> "ส่งคำขอเรียบร้อย"
                                        isLoading -> "กำลังส่งคำขอ..."
                                        else -> "ส่งคำขอเป็นเพื่อน"
                                    }

                                    Text(
                                        text = buttonText,
                                        color = if (isSuccess || isAlreadySent || isLoading) themeColor.copy(alpha = 0.5f) else themeColor,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .clickable(enabled = !isLoading && !isSuccess && !isAlreadySent) {
                                                onAddFriendClick()
                                            }
                                            .padding(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Modal ดึงรายละเอียดสินทรัพย์
        if (selectedItem != null) {
            SmartAssetDetailDialog(
                assetId = selectedItem!!.itemId ?: "",
                assetType = selectedItem!!.type ?: "",
                showBottomMenu = false,
                onDismiss = { selectedItem = null }
            )
        }
    }
}

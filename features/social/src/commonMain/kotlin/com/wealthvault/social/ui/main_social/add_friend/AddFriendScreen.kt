package com.wealthvault.social.ui.main_social.add_friend

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_back
import com.wealthvault.core.generated.resources.ic_nav_profile
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.social.ui.components.SocialSearchBar
import com.wealthvault.`user-api`.model.FriendData
import org.jetbrains.compose.resources.painterResource

class AddFriendScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        // 🌟 1. ดึง ScreenModel มาใช้งาน
        val screenModel = getScreenModel<AddFriendScreenModel>()

        var searchQuery by remember { mutableStateOf("") }

        // 🌟 2. ดึง State จาก ScreenModel แทน Mock ของเดิม
        val isSearching by screenModel.isSearching.collectAsState()
        val hasSearched by screenModel.hasSearched.collectAsState()
        val searchResult by screenModel.searchResult.collectAsState()
        val addFriendSuccess by screenModel.addFriendSuccess.collectAsState()

        // 🌟 3. ถ้าเพิ่มเพื่อนสำเร็จ ให้เด้งกลับหน้าเดิมอัตโนมัติ
        LaunchedEffect(addFriendSuccess) {
            if (addFriendSuccess) {
                navigator.pop()
            }
        }

        AddFriendContent(
            searchQuery = searchQuery,
            onSearchChange = { searchQuery = it },
            onSearchClick = {
                // 🌟 สั่งค้นหา API จริง
                screenModel.searchUser(searchQuery.trim())
            },
            searchResult = searchResult,
            hasSearched = hasSearched,
            isSearching = isSearching, // ส่งสถานะไปคุม UI
            onBackClick = { navigator.pop() },
            onAddFriendClick = { friend ->
                // 🌟 สั่งเพิ่มเพื่อน API จริง
                friend.id?.let { targetId ->
                    screenModel.addFriend(targetId)
                }
            }
        )
    }
}

@Composable
fun AddFriendContent(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    searchResult: FriendData?,
    hasSearched: Boolean,
    isSearching: Boolean, // 🌟 เพิ่มตัวรับสถานะกำลังค้นหา
    onBackClick: () -> Unit,
    onAddFriendClick: (FriendData) -> Unit
) {
    val themeColor = Color(0xFFC27A5A)

    // 🌟 สร้าง State จดจำว่าผู้ใช้กดส่งคำขอไปหรือยัง (รีเซ็ตใหม่ทุกครั้งที่ผลค้นหาเปลี่ยน)
    var requestSent by remember(searchResult) { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBg)
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        // --- 1. Header ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 18.dp)
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_common_back),
                contentDescription = "Back",
                tint = LightPrimary,
                modifier = Modifier.size(24.dp).clickable { onBackClick() }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "เพิ่มเพื่อน", style = MaterialTheme.typography.titleLarge, color = LightPrimary)
        }

        // --- 2. ช่องค้นหา + ปุ่มค้นหา ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SocialSearchBar(
                searchQuery = searchQuery,
                onSearchChange = onSearchChange,
                placeholderText = "ค้นหาด้วย Email",
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Button(
                onClick = onSearchClick,
                // 🌟 ห้ามกดถ้าช่องค้นหาว่าง หรือกำลังโหลดอยู่
                enabled = searchQuery.isNotBlank() && !isSearching,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = themeColor,
                    disabledContainerColor = themeColor.copy(alpha = 0.5f)
                ),
                modifier = Modifier.height(52.dp)
            ) {
                // 🌟 ถ้ากำลังค้นหา ให้โชว์หมุนๆ แทนคำว่า "ค้นหา"
                if (isSearching) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text("ค้นหา", color = Color.White, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- 3. แสดงผลลัพธ์การค้นหา ---
        // 🌟 โชว์ผลลัพธ์ก็ต่อเมื่อ ค้นหาเสร็จแล้ว (!isSearching)
        if (hasSearched && !isSearching) {
            Text(
                text = "ผลการค้นหา",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF3A2F2A),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (searchResult != null) {
                // กรณีเจอผู้ใช้
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(LightBg),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!searchResult.profile.isNullOrEmpty()) {
                            AsyncImage(
                                model = searchResult.profile,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                painter = painterResource(Res.drawable.ic_nav_profile),
                                contentDescription = null,
                                tint = LightPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = searchResult.username ?: searchResult.firstName ?: "ไม่ระบุชื่อ",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF3A2F2A)
                        )
                        Text(
                            text = searchResult.email ?: "",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray
                        )
                    }

                    // 🌟 เช็คว่าเป็นเพื่อนกันอยู่แล้วหรือไม่
                    if (searchResult.isFriend == true) {
                        Text(
                            text = "เป็นเพื่อนแล้ว",
                            color = Color(0xFF9E918B), // สีเทา
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    } else {
                        // 🌟 ปุ่มส่งคำขอ (ถ้ากดแล้วจะเปลี่ยนสีเป็นสีเทา)
                        Button(
                            onClick = {
                                requestSent = true // 🌟 ล็อคปุ่มทันทีที่กด
                                onAddFriendClick(searchResult)
                            },
                            enabled = !requestSent, // 🌟 ล็อคปุ่มถ้ากดไปแล้ว
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = themeColor,
                                disabledContainerColor = Color.LightGray // 🌟 เปลี่ยนเป็นสีเทาเมื่อกดแล้ว
                            ),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = if (requestSent) "กำลังส่ง..." else "ส่งคำขอ", // 🌟 เปลี่ยนข้อความ
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            } else {
                // กรณีไม่พบผู้ใช้
                Box(
                    modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "ไม่พบผู้ใช้นี้\nโปรดตรวจสอบความถูกต้องอีกครั้ง",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
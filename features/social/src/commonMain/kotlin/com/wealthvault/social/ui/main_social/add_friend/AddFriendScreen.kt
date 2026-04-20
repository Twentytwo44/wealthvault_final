package com.wealthvault.social.ui.main_social.add_friend

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.wealthvault.`user-api`.model.PendingFriendData
import org.jetbrains.compose.resources.painterResource

class AddFriendScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<AddFriendScreenModel>()

        var searchQuery by remember { mutableStateOf("") }

        val isSearching by screenModel.isSearching.collectAsState()
        val hasSearched by screenModel.hasSearched.collectAsState()
        val searchResult by screenModel.searchResult.collectAsState()
        val addFriendSuccess by screenModel.addFriendSuccess.collectAsState()
        val pendingFriends by screenModel.pendingFriends.collectAsState()

        // 🌟 ดึง State Popup จาก ScreenModel
        val popupMessage by screenModel.popupMessage.collectAsState()

        LaunchedEffect(Unit) {
            screenModel.fetchPendingFriends()
        }

        LaunchedEffect(addFriendSuccess) {
            if (addFriendSuccess) {
                navigator.pop()
            }
        }

        // 🌟 ครอบ Box ไว้ เพื่อให้ Popup ลอยอยู่ตรงกลางจอได้
        Box(modifier = Modifier.fillMaxSize()) {
            AddFriendContent(
                searchQuery = searchQuery,
                onSearchChange = { searchQuery = it },
                onSearchClick = { screenModel.searchUser(searchQuery.trim()) },
                searchResult = searchResult,
                hasSearched = hasSearched,
                isSearching = isSearching,
                pendingFriends = pendingFriends,
                onBackClick = { navigator.pop() },
                onAddFriendClick = { friend ->
                    friend.id?.let { targetId -> screenModel.addFriend(targetId) }
                },
                onRespondToRequest = { targetId, isAccept ->
                    screenModel.respondToFriendRequest(targetId, isAccept)
                }
            )

            // 🌟 2. แจ้งเตือน Popup (AlertDialog)
            if (popupMessage != null) {
                AlertDialog(
                    onDismissRequest = { screenModel.clearPopupMessage() },
                    title = {
                        Text(
                            text = "แจ้งเตือน",
                            color = LightPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    text = {
                        Text(
                            text = popupMessage ?: "",
                            color = Color(0xFF3A2F2A),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = { screenModel.clearPopupMessage() },
                            colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("ตกลง", color = Color.White)
                        }
                    },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }
    }
}

@Composable
fun AddFriendContent(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    searchResult: FriendData?,
    hasSearched: Boolean,
    isSearching: Boolean,
    pendingFriends: List<PendingFriendData>,
    onBackClick: () -> Unit,
    onAddFriendClick: (FriendData) -> Unit,
    onRespondToRequest: (String, Boolean) -> Unit
) {
    val themeColor = Color(0xFFC27A5A)
    var requestSent by remember(searchResult) { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBg)
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        // --- 1. Header ---
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 18.dp)) {
            Icon(painter = painterResource(Res.drawable.ic_common_back), contentDescription = "Back", tint = LightPrimary, modifier = Modifier.size(24.dp).clickable { onBackClick() })
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "เพิ่มเพื่อน", style = MaterialTheme.typography.titleLarge, color = LightPrimary)
        }

        // --- 2. ช่องค้นหา + ปุ่มค้นหา ---
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            SocialSearchBar(
                searchQuery = searchQuery,
                onSearchChange = { onSearchChange(it) },
                placeholderText = "ค้นหาด้วย Email",
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Button(
                onClick = onSearchClick,
                enabled = searchQuery.isNotBlank() && !isSearching,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = themeColor, disabledContainerColor = themeColor.copy(alpha = 0.5f)),
                modifier = Modifier.height(52.dp)
            ) {
                if (isSearching) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text("ค้นหา", color = Color.White, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- 3. ลอจิกสลับหน้าจอ ---
        if (hasSearched && searchQuery.isNotEmpty()) {

            Text("ผลการค้นหา", style = MaterialTheme.typography.titleMedium, color = Color(0xFF3A2F2A), modifier = Modifier.padding(bottom = 16.dp))

            if (!isSearching) {
                if (searchResult != null) {
                    // เจอคน
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(16.dp)).padding(16.dp)
                    ) {
                        Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(LightBg), contentAlignment = Alignment.Center) {
                            if (!searchResult.profile.isNullOrEmpty()) {
                                AsyncImage(model = searchResult.profile, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                            } else {
                                Icon(painter = painterResource(Res.drawable.ic_nav_profile), contentDescription = null, tint = LightPrimary, modifier = Modifier.size(28.dp))
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = searchResult.username ?: searchResult.firstName ?: "ไม่ระบุชื่อ", style = MaterialTheme.typography.titleMedium, color = Color(0xFF3A2F2A))
                            Text(text = searchResult.email ?: "", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                        }

                        if (searchResult.isFriend == true) {
                            Text("เป็นเพื่อนแล้ว", color = Color(0xFF9E918B), style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(end = 8.dp))
                        } else {
                            Button(
                                onClick = { requestSent = true; onAddFriendClick(searchResult) },
                                enabled = !requestSent,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = themeColor, disabledContainerColor = Color.LightGray),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(text = if (requestSent) "กำลังส่ง..." else "ส่งคำขอ", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                } else {
                    // ไม่เจอคน
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                        Text(text = "ไม่พบผู้ใช้นี้\nโปรดตรวจสอบความถูกต้องอีกครั้ง", style = MaterialTheme.typography.bodyMedium, color = Color.Gray, textAlign = TextAlign.Center)
                    }
                }
            }

        } else {
            // คำขอเป็นเพื่อน
            Text("คำขอเป็นเพื่อน", style = MaterialTheme.typography.titleMedium, color = Color(0xFF3A2F2A), modifier = Modifier.padding(bottom = 16.dp))

            if (pendingFriends.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("ยังไม่มีคำขอเป็นเพื่อนในขณะนี้", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(pendingFriends) { request ->
                        FriendRequestItem(
                            request = request,
                            themeColor = themeColor,
                            onAccept = { request.id?.let { onRespondToRequest(it, true) } },
                            onReject = { request.id?.let { onRespondToRequest(it, false) } }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun FriendRequestItem(
    request: PendingFriendData,
    themeColor: Color,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    val displayName = request.username ?: request.firstName ?: "ไม่ระบุชื่อ"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(50.dp).clip(CircleShape).background(LightBg), contentAlignment = Alignment.Center) {
            if (!request.profile.isNullOrEmpty()) {
                AsyncImage(model = request.profile, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            } else {
                Icon(painter = painterResource(Res.drawable.ic_nav_profile), contentDescription = null, tint = LightPrimary, modifier = Modifier.size(24.dp))
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = displayName, style = MaterialTheme.typography.titleMedium, color = Color(0xFF3A2F2A))
            Text(text = request.email ?: "", color = Color.Gray, fontSize = 12.sp)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = onReject,
                modifier = Modifier.height(36.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Text("ลบ", color = Color.Gray, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
            Button(
                onClick = onAccept,
                modifier = Modifier.height(36.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = themeColor)
            ) {
                Text("ยืนยัน", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}
package com.wealthvault.social.ui.group

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wealthvault.social.ui.components.GroupListItem
import com.wealthvault.social.ui.components.SocialSearchBar

@Composable
fun GroupScreen() {
    var searchQuery by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(16.dp))

        // ช่องค้นหา
        SocialSearchBar(
            searchQuery = searchQuery,
            onSearchChange = { searchQuery = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // รายชื่อกลุ่ม
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item { GroupListItem(groupName = "Family", memberCount = 4) }
            // TODO: ใส่ข้อมูลจาก ViewModel วนลูป (items) ตรงนี้
        }
    }
}
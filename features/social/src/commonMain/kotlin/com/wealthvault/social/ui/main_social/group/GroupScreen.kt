package com.wealthvault.social.ui.main_social.group

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.group_api.model.GetAllGroupData
import com.wealthvault.social.ui.components.GroupListItem
import com.wealthvault.social.ui.components.SocialSearchBar
import com.wealthvault.social.ui.space.GroupSpaceScreen

// 🌟 สร้างคลาส Screen
class GroupScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<GroupScreenModel>()

        // 🌟 1. ดึง Root Navigator เพื่อซ่อน Bottom Bar
        val navigator = LocalNavigator.currentOrThrow
        var rootNavigator = navigator
        while (rootNavigator.parent != null) {
            rootNavigator = rootNavigator.parent!!
        }

        LaunchedEffect(Unit) {
            screenModel.fetchGroups()
        }

        val groups by screenModel.groups.collectAsState()

        GroupContent(
            groups = groups,
            // 🌟 2. ส่งคำสั่งคลิกไป
            onGroupClick = { id, name ->
                rootNavigator.push(GroupSpaceScreen(groupId = id, groupName = name))
            }
        )
    }
}

@Composable
fun GroupContent(
    groups: List<GetAllGroupData>,
    modifier: Modifier = Modifier,
    onGroupClick: (String, String) -> Unit // 🌟 เพิ่ม Parameter
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredGroups = groups.filter {
        it.groupName?.contains(searchQuery, ignoreCase = true) == true
    }

    Column(modifier = modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(16.dp))
        SocialSearchBar(searchQuery = searchQuery, onSearchChange = { searchQuery = it })
        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(filteredGroups) { group ->
                // 🌟 3. ใส่ onClick ให้ Item
                GroupListItem(
                    group = group,
                    onClick = {
                        onGroupClick(group.id ?: "", group.groupName ?: "")
                    }
                )
            }
        }
    }
}
package com.wealthvault.profile.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

// Import หน้าจอที่เราเพิ่งสร้างไป
import com.wealthvault.profile.ui.ProfileScreen
import com.wealthvault.profile.ui.EditProfileScreen

// 1. พรีวิวหน้า Profile หลัก (ฝั่งซ้าย)
@Preview(showBackground = true, name = "1. Profile Screen", device = "id:pixel_7")
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(
        onEditProfileClick = {},
        onLogoutClick = {}
    )
}

// 2. พรีวิวหน้า Edit Profile (ฝั่งขวา)
@Preview(showBackground = true, name = "2. Edit Profile Screen", device = "id:pixel_7")
@Composable
fun EditProfileScreenPreview() {
    EditProfileScreen(
        onBackClick = {},
        onSaveClick = {}
    )
}
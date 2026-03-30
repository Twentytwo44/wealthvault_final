package com.wealthvault.profile.ui

// 🌟 Import หน้าจอทั้งหมดในหมวด Profile
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

// 1. พรีวิวหน้า Profile หลัก
@Preview(showBackground = true, name = "1. Profile Screen", device = "id:pixel_7")
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(
        onSettingsClick = {},
    )
}

// 2. พรีวิวหน้ารวมเมนูตั้งค่า (Menu Settings)
@Preview(showBackground = true, name = "2. Menu Settings Screen", device = "id:pixel_7")
@Composable
fun MenuProfileSettingScreenPreview() {
    MenuProfileSettingScreen(
        onBackClick = {},
        onEditProfileClick = {},
        onShareSettingClick = {},
        onLogoutClick = {}
    )
}

// 3. พรีวิวหน้าแก้ไขข้อมูลส่วนตัว (Edit Profile)
@Preview(showBackground = true, name = "3. Edit Profile Screen", device = "id:pixel_7")
@Composable
fun EditProfileScreenPreview() {
    EditProfileScreen(
        onBackClick = {},
        onSaveClick = {}
    )
}

// 4. พรีวิวหน้าตั้งค่าการแชร์ทรัพย์สิน (Share Settings)
@Preview(showBackground = true, name = "4. Share Setting Screen", device = "id:pixel_7")
@Composable
fun ShareSettingScreenPreview() {
    ShareSettingScreen(
        onBackClick = {},
//        onSaveClick = {}
    )
}

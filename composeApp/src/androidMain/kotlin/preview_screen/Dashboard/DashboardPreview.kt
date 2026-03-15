package preview_screen.Dashboard

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.wealthvault.dashboard.ui.DashboardScreen

// หน้าที่ 1: หน้าหลัก (Main/Dashboard)
@Preview(showBackground = true,
    name = "1. Main/Dashboard Screen",
//    showSystemUi = true,
    device = "id:pixel_7_pro")
@Composable
fun MainScreenPreview() {
    // 💡 ข้อควรระวัง: ในโค้ดเดิมคุณ import DashboardScreen มา แต่เรียกใช้ MainScreen()
    // ผมเลยคอมเมนต์ไว้ให้เลือกใช้นะครับว่าจริงๆ แล้วคือหน้าไหน

    // MainScreen()
    DashboardScreen()
}
package preview_screen.auth // แก้ package ให้ตรงกับไฟล์

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.wealthvault.login.ui.LoginContent
import com.wealthvault.dashboard.ui.DashboardScreen

@Preview(showBackground = true, name = "Preview")
@Composable
fun LoginScreenPreview() {
    DashboardScreen(
    )
}
//fun MainScreenPreview(){
//    MainScreen()
//}



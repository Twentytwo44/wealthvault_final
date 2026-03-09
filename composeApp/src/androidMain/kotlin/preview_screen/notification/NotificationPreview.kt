package preview_screen.notification


import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.notification.ui.NotificationScreen

@Preview(showBackground = true, name = "Preview")
@Composable
fun NotificationPreview() {
    NotificationScreen(
        onBackClick = {}
    )
}
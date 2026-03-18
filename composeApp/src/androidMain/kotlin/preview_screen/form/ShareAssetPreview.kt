package preview_screen.form

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.wealthvault_final.`financial-asset`.ui.share.ShareAssetScreen

@Preview(showBackground = true, name = "Preview")
@Composable
fun ShareAssetPreview() {
    ShareAssetScreen(
        onBackClick = {},
        onNextClick = {},
        onAddFriendClick = {},
        onAddExternalClick = {}
    )
}

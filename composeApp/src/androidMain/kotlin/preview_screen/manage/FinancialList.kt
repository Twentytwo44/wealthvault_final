package preview_screen.manage

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.wealthvault.financiallist.ui.asset.AssetScreen
import com.wealthvault.financiallist.ui.debt.DebtScreen

@Preview(showBackground = true, name = "1. Asset Screen (ทรัพย์สิน)")
@Composable
fun AssetScreenPreview() {
    AssetScreen(onAddClick = {})
}

@Preview(showBackground = true, name = "2. Debt Screen (หนี้สิน)")
@Composable
fun DebtScreenPreview() {
    DebtScreen(onAddClick = {})
}
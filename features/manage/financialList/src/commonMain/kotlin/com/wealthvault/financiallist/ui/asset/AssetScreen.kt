package com.wealthvault.financiallist.ui.asset

// Import Utils & Resources

// Import Components ของฝั่ง Financial
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.navigator.Navigator
import com.wealthvault.domain.portfolio.AccountData
import com.wealthvault.domain.portfolio.BankAccountData
import com.wealthvault.domain.portfolio.BuildingIdData
import com.wealthvault.domain.portfolio.GetBuildingData
import com.wealthvault.domain.portfolio.CashIdData
import com.wealthvault.domain.portfolio.GetCashData
import com.wealthvault.core.components.ConfirmDeleteDialog
import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_plus
import com.wealthvault.core.generated.resources.ic_nav_asset
import com.wealthvault.core.theme.LightAsset
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault.core.utils.LocalRootNavigator
import com.wealthvault.core.utils.formatAmount
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.core.model.FixedDecimal
import com.wealthvault.core.model.Money
import com.wealthvault.core.navigation.SharedScreen
import com.wealthvault.financiallist.ui.FinancialListTemplate
import com.wealthvault.financiallist.ui.FinancialListEmptyState
import com.wealthvault.financiallist.ui.FinancialListNoResultsState
import com.wealthvault.financiallist.ui.asset.form.account.BankAccountFormScreen
import com.wealthvault.financiallist.ui.asset.form.building.BuildingFormScreen
import com.wealthvault.financiallist.ui.asset.form.cash.CashFormScreen
import com.wealthvault.financiallist.ui.asset.form.insurance.InsuranceFormScreen
import com.wealthvault.financiallist.ui.asset.form.investment.StockFormScreen
import com.wealthvault.financiallist.ui.asset.form.land.LandFormScreen
import com.wealthvault.financiallist.ui.component.ExpandableCategoryCard
import com.wealthvault.financiallist.ui.component.RealItemCard
import com.wealthvault.financiallist.ui.component.SmartAssetDetailDialog
import com.wealthvault.financiallist.ui.shareasset.ShareAssetScreen
import com.wealthvault.domain.portfolio.GetInsuranceData
import com.wealthvault.domain.portfolio.InsuranceIdData
import com.wealthvault.domain.portfolio.GetInvestmentData
import com.wealthvault.domain.portfolio.InvestmentIdData
import com.wealthvault.domain.portfolio.GetLandData
import com.wealthvault.domain.portfolio.LandIdData
import com.wealthvault.financiallist.ui.toAttachment
import com.wealthvault.domain.portfolio.BankAccountModel
import com.wealthvault.domain.portfolio.BuildingModel
import com.wealthvault.domain.portfolio.CashModel
import com.wealthvault.domain.portfolio.InsRefModel
import com.wealthvault.domain.portfolio.InsuranceModel
import com.wealthvault.domain.portfolio.LandModel
import com.wealthvault.domain.portfolio.RefModel
import com.wealthvault.domain.portfolio.StockModel
import org.jetbrains.compose.resources.painterResource

class AssetScreen() : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalRootNavigator.current
        val screenModel = getScreenModel<AssetScreenModel>()
        val financialMenuScreen = rememberScreen(SharedScreen.FinancialMenu)

        // Initial loading is explicit and deduplicated by the ScreenModel.
        LaunchedEffect(screenModel) {
            screenModel.fetchAllAssets()
        }

        val accounts by screenModel.accounts.collectAsStateWithLifecycle()
        val cashes by screenModel.cashes.collectAsStateWithLifecycle()
        val investments by screenModel.investments.collectAsStateWithLifecycle()
        val insurances by screenModel.insurances.collectAsStateWithLifecycle()
        val buildings by screenModel.buildings.collectAsStateWithLifecycle()
        val lands by screenModel.lands.collectAsStateWithLifecycle()

        AssetContent(
            screenModel = screenModel,
            onAddClick = {
                navigator.push(financialMenuScreen)
            },
            accounts = accounts,
            cashes = cashes,
            investments = investments,
            insurances = insurances,
            buildings = buildings,
            lands = lands,
            navigatorContent = navigator
        )
    }
}

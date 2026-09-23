package com.wealthvault.financiallist.ui.debt

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
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.components.ConfirmDeleteDialog
import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_plus
import com.wealthvault.core.generated.resources.ic_nav_debt
import com.wealthvault.core.theme.LightDebt
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault.core.utils.LocalRootNavigator
import com.wealthvault.core.utils.formatAmount
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.core.model.Money
import com.wealthvault.core.navigation.SharedScreen
import com.wealthvault.financiallist.ui.FinancialListTemplate
import com.wealthvault.financiallist.ui.FinancialListEmptyState
import com.wealthvault.financiallist.ui.FinancialListNoResultsState
import com.wealthvault.financiallist.ui.component.ExpandableCategoryCard
import com.wealthvault.financiallist.ui.component.RealItemCard
import com.wealthvault.financiallist.ui.component.SmartAssetDetailDialog
import com.wealthvault.financiallist.ui.debt.form.debt.LiabilityFormScreen
import com.wealthvault.financiallist.ui.debt.form.expense.ExpenseFormScreen
import com.wealthvault.financiallist.ui.shareasset.ShareAssetScreen
import com.wealthvault.domain.portfolio.GetLiabilityData
import com.wealthvault.domain.portfolio.LiabilityIdData
import com.wealthvault.financiallist.ui.toAttachment
import com.wealthvault.domain.portfolio.ExpenseModel
import com.wealthvault.domain.portfolio.LiabilityModel
import org.jetbrains.compose.resources.painterResource

class DebtScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val debtMenuScreen = rememberScreen(SharedScreen.DebtMenu)

        var rootNavigator = navigator
        while (rootNavigator.parent != null) {
            rootNavigator = rootNavigator.parent!!
        }

        val screenModel = getScreenModel<DebtScreenModel>()
        val navigatorContent = LocalRootNavigator.current

        LaunchedEffect(screenModel) {
            screenModel.fetchLiabilities()
        }

        val loans by screenModel.loans.collectAsStateWithLifecycle()
        val expenses by screenModel.expenses.collectAsStateWithLifecycle()

        DebtContent(
            onAddClick = {
                navigatorContent.push(debtMenuScreen)
            },
            loans = loans,
            expenses = expenses,
            screenModel = screenModel,
            navigatorContent = navigatorContent
        )
    }
}

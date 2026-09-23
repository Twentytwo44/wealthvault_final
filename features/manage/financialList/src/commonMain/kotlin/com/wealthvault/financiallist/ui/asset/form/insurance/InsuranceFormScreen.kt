package com.wealthvault.financiallist.ui.asset.form.insurance

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_back
import com.wealthvault.core.generated.resources.ic_common_calendar
import com.wealthvault.core.theme.*
import com.wealthvault.core.utils.formatThaiDate
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.core.model.Attachment
import com.wealthvault.financiallist.ui.form.*
import com.wealthvault.domain.portfolio.InsuranceModel
import com.wealthvault.core.model.Money
import com.wealthvault.financiallist.ui.form.FormErrorBanner
import com.wealthvault.financiallist.ui.form.formErrorMessage
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource

class InsuranceFormScreen(val id: String, val insuranceData: InsuranceModel) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<com.wealthvault.financiallist.ui.asset.form.insurance.InsuranceScreenModel>()
        val uiState by screenModel.uiState.collectAsStateWithLifecycle()

        InsuranceInputForm(
            isLoading = uiState.isLoading,
            errorMessage = uiState.error?.let(::formErrorMessage),
            onBackClick = { navigator.pop() },
            onNextClick = { data, addedList, deletedList ->
                screenModel.updateForm(data)
                screenModel.updateAttachment(addedList, deletedList)
                screenModel.submitInsurance(id,
                    onSuccess = {
                        // 💡 หลังจากแก้ไขสำเร็จ จะส่งกลับหน้าลิสต์
                        navigator.pop()
                    })
            },
            insuranceData = insuranceData
        )
    }
}

/** Create flow owned by the financial-list feature. */
data object CreateInsuranceFormScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<InsuranceScreenModel>()
        val uiState by screenModel.uiState.collectAsStateWithLifecycle()
        val initialData = remember {
            InsuranceModel(
                policyNumber = "",
                type = "",
                companyName = "",
                coverageAmount = Money(0),
                coveragePeriod = "",
                expDate = "",
                description = "",
                attachments = emptyList(),
                conDate = "",
                name = "",
            )
        }

        InsuranceInputForm(
            title = "ข้อมูลประกัน",
            submitLabel = "ต่อไป",
            isLoading = uiState.isLoading,
            errorMessage = uiState.error?.let(::formErrorMessage),
            onBackClick = { navigator.pop() },
            onNextClick = { data, addedList, deletedList ->
                screenModel.updateForm(data)
                screenModel.updateAttachment(addedList, deletedList)
                screenModel.submitCreate { id ->
                    navigator.push(com.wealthvault.financiallist.ui.shareasset.ShareAssetScreen("insurance", id))
                }
            },
            insuranceData = initialData,
        )
    }
}

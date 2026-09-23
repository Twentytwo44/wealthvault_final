package com.wealthvault.financiallist.ui.asset.form.building

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_back
import com.wealthvault.core.generated.resources.ic_common_bin
import com.wealthvault.core.generated.resources.ic_common_plus
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault.core.theme.LightText
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.domain.portfolio.GetInsuranceData
import com.wealthvault.domain.portfolio.GetLandData
import com.wealthvault.core.model.Attachment
import com.wealthvault.financiallist.ui.form.*
import com.wealthvault.domain.portfolio.BuildingModel
import com.wealthvault.core.model.Money
import com.wealthvault.domain.portfolio.InsRefModel
import com.wealthvault.domain.portfolio.RefModel
import com.wealthvault.financiallist.ui.form.FormErrorBanner
import com.wealthvault.financiallist.ui.form.formErrorMessage
import org.jetbrains.compose.resources.painterResource

class BuildingFormScreen(val id: String, val buildingData: BuildingModel) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<BuildingScreenModel>()
        val landState by screenModel.LandState.collectAsStateWithLifecycle()
        val insState by screenModel.InsState.collectAsStateWithLifecycle()
        val formUiState by screenModel.uiState.collectAsStateWithLifecycle()

        LaunchedEffect(screenModel) {
            screenModel.fetchData()
        }

        // 🌟 2. ตั้งค่าข้อมูลตั้งต้น (Initial Data) เข้าไปใน Model เพียงครั้งเดียว
        LaunchedEffect(Unit) {
            screenModel.updateForm(buildingData)
        }

        BuildingInputForm(
            isLoading = formUiState.isLoading,
            errorMessage = formUiState.error?.let(::formErrorMessage),
            onBackClick = { navigator.pop() },
            onNextClick = { data, addedList, deletedList, addRef, deleteRef, addIns, deleteIns ->
                // 🌟 แนะนำ: ควรมี Loading State ระหว่างรอ API ด้วยครับ
                screenModel.updateForm(data)
                screenModel.updateAttachment(addedList, deletedList, addRef, deleteRef, addIns, deleteIns)

                screenModel.submitLand(id, onSuccess = {
                    // ✅ หลังจากแก้ไขสำเร็จ กลับไปหน้าก่อนหน้า
                    navigator.pop()
                })
            },
            landData = landState,
            insData = insState,
            buildingData = buildingData
        )
    }
}

/** Create flow owned by the financial-list feature. */
data object CreateBuildingFormScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<BuildingScreenModel>()
        val landState by screenModel.LandState.collectAsStateWithLifecycle()
        val insState by screenModel.InsState.collectAsStateWithLifecycle()
        val uiState by screenModel.uiState.collectAsStateWithLifecycle()
        val initialData = remember {
            BuildingModel(
                type = "",
                buildingName = "",
                area = 0.0,
                amount = Money(0),
                description = "",
                attachments = emptyList(),
                referenceIds = emptyList(),
                locationAddress = "",
                locationSubDistrict = "",
                locationDistrict = "",
                locationProvince = "",
                locationPostalCode = "",
                insIds = emptyList(),
            )
        }

        LaunchedEffect(screenModel) { screenModel.fetchData() }
        BuildingInputForm(
            title = "ข้อมูลอาคาร ตึก",
            submitLabel = "ต่อไป",
            isLoading = uiState.isLoading,
            errorMessage = uiState.error?.let(::formErrorMessage),
            onBackClick = { navigator.pop() },
            onNextClick = { data, addedList, deletedList, addRef, deleteRef, addIns, deleteIns ->
                screenModel.updateForm(data)
                screenModel.updateAttachment(addedList, deletedList, addRef, deleteRef, addIns, deleteIns)
                screenModel.submitCreate { id ->
                    navigator.push(com.wealthvault.financiallist.ui.shareasset.ShareAssetScreen("building", id))
                }
            },
            landData = landState,
            insData = insState,
            buildingData = initialData,
        )
    }
}

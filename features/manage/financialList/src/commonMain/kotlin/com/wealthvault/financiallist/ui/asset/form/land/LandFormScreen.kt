package com.wealthvault.financiallist.ui.asset.form.land

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
import com.wealthvault.domain.portfolio.GetBuildingData
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
import com.wealthvault.core.model.Attachment
import com.wealthvault.financiallist.ui.form.*
import com.wealthvault.domain.portfolio.LandModel
import com.wealthvault.core.model.Money
import com.wealthvault.domain.portfolio.RefModel
import com.wealthvault.financiallist.ui.form.FormErrorBanner
import com.wealthvault.financiallist.ui.form.formErrorMessage
import org.jetbrains.compose.resources.painterResource


class LandFormScreen(val id: String, val landData: LandModel) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<LandScreenModel>()

        LaunchedEffect(screenModel) {
            screenModel.fetchData()
        }

        // 🌟 2. ตั้งค่าข้อมูลที่ส่งมา (Initial Data) เข้าสู่ Model เพียงครั้งเดียว
        LaunchedEffect(Unit) {
            screenModel.updateForm(landData)
        }

        val buildState by screenModel.BuildingState.collectAsStateWithLifecycle()
        val formUiState by screenModel.uiState.collectAsStateWithLifecycle()

        LandInputForm(
            isLoading = formUiState.isLoading,
            errorMessage = formUiState.error?.let(::formErrorMessage),
            onBackClick = { navigator.pop() },
            onNextClick = { data, addedList, deletedList, addRef, deleteRef ->
                // 🌟 บันทึกการเปลี่ยนแปลงและไฟล์แนบ
                screenModel.updateForm(data)
                screenModel.updateAttachment(addedList, deletedList, addRef, deleteRef)

                screenModel.submitLand(id,
                    onSuccess = {
                        // ✅ เด้งกลับหน้าก่อนหน้าเมื่อบันทึกสำเร็จ
                        navigator.pop()
                    })
            },
            landData = landData, // ข้อมูลเดิมสำหรับแสดงผลตั้งต้น
            buildingData = buildState // รายการสิ่งปลูกสร้างที่ดึงมาใหม่
        )
    }
}

/** Create flow owned by the financial-list feature. */
data object CreateLandFormScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<LandScreenModel>()
        val buildingState by screenModel.BuildingState.collectAsStateWithLifecycle()
        val uiState by screenModel.uiState.collectAsStateWithLifecycle()
        val initialData = remember {
            LandModel(
                deedNum = "",
                landName = "",
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
            )
        }

        LaunchedEffect(screenModel) { screenModel.fetchData() }
        LandInputForm(
            title = "ข้อมูลโฉนดที่ดิน",
            submitLabel = "ต่อไป",
            isLoading = uiState.isLoading,
            errorMessage = uiState.error?.let(::formErrorMessage),
            onBackClick = { navigator.pop() },
            onNextClick = { data, addedList, deletedList, addRef, deleteRef ->
                screenModel.updateForm(data)
                screenModel.updateAttachment(addedList, deletedList, addRef, deleteRef)
                screenModel.submitCreate { id ->
                    navigator.push(com.wealthvault.financiallist.ui.shareasset.ShareAssetScreen("land", id))
                }
            },
            landData = initialData,
            buildingData = buildingState,
        )
    }
}

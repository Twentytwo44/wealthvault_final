package com.wealthvault.`financial-asset`.ui.realestate

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.flow.StateFlow
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_back
import com.wealthvault.core.architecture.UiAction
import com.wealthvault.core.architecture.UiEffect
import com.wealthvault.core.architecture.UiState
import com.wealthvault.core.architecture.UiStateHolder
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.`financial-asset`.ui.components.maptype.DropdownInput
import com.wealthvault.`financial-asset`.ui.realestate.building.BuildingFormScreen
import com.wealthvault.`financial-asset`.ui.realestate.land.LandFormScreen
import org.jetbrains.compose.resources.painterResource

data class RealEstateUiData(val selectedType: String = "building")

sealed interface RealEstateUiAction : UiAction {
    data class SelectType(val type: String) : RealEstateUiAction
}

sealed interface RealEstateUiEffect : UiEffect

class RealEstateScreenModel : ScreenModel {
    private val stateHolder = UiStateHolder(RealEstateUiData())
    val uiState: StateFlow<UiState<RealEstateUiData>> = stateHolder.state
    val effects = stateHolder.effects

    var selectedType: String
        get() = uiState.value.data?.selectedType ?: "building"
        set(value) { onAction(RealEstateUiAction.SelectType(value)) }

    fun onAction(action: RealEstateUiAction) {
        when (action) {
            is RealEstateUiAction.SelectType -> stateHolder.success(RealEstateUiData(action.type))
        }
    }
}

class RealEstateScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        // 🌟 2. เรียกใช้ ScreenModel
        val screenModel = getScreenModel<RealEstateScreenModel>()
        val state by screenModel.uiState.collectAsState()
        val selectedType = state.data?.selectedType ?: "building"

        RealEstateContent(
            selectedType = selectedType,
            onTypeChange = { screenModel.onAction(RealEstateUiAction.SelectType(it)) },
            onBackClick = { navigator.pop() },
            onClickToLand = { navigator.push(LandFormScreen()) },
            onClickToBuilding = { navigator.push(BuildingFormScreen()) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RealEstateContent(
    selectedType: String,
    onTypeChange: (String) -> Unit,
    onBackClick: () -> Unit = {},
    onClickToLand: () -> Unit = {},
    onClickToBuilding: () -> Unit = {},
) {
    val options = listOf(
        "building" to "บ้าน ตึก อาคาร",
        "land" to "ที่ดิน"
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = LightBg,
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    // 🌟 มาตรฐาน Master UI: Padding ขอบ 24.dp
                    modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 16.dp, top = 24.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_common_back),
                        contentDescription = "Back",
                        tint = LightPrimary,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onBackClick() }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "ข้อมูลอสังหาริมทรัพย์",
                        style = MaterialTheme.typography.titleLarge,
                        color = LightPrimary
                    )
                }
            }
        },
        bottomBar = {
            // 🌟 มาตรฐาน Master UI: ปุ่มสูง 46.dp และ Padding 24.dp
            Box(modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(24.dp)) {
                Button(
                    onClick = {
                        if (selectedType == "building") {
                            onClickToBuilding()
                        } else {
                            onClickToLand()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "ต่อไป",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {

            // 🌟 เรียกใช้ DropdownInput มาตรฐานความสูง 44.dp
            DropdownInput(
                label = "เลือกประเภทอสังหาริมทรัพย์",
                options = options,
                selectedValue = selectedType,
                onValueChange = onTypeChange,
                placeholder = "กรุณาเลือกประเภท"
            )
        }
    }
}

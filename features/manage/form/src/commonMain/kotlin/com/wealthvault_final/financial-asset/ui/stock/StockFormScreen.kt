package com.wealthvault_final.`financial-asset`.ui.stock

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_back
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault_final.`financial-asset`.Imagepicker.Attachment
import com.wealthvault_final.`financial-asset`.Imagepicker.rememberFilePicker
import com.wealthvault_final.`financial-asset`.model.StockModel
import com.wealthvault_final.`financial-asset`.ui.components.AssetTextField
import com.wealthvault_final.`financial-asset`.ui.components.ReferenceImagepicker
import com.wealthvault_final.`financial-asset`.ui.components.maptype.DropdownInput
import com.wealthvault_final.`financial-asset`.ui.components.maptype.investmentTypes
import com.wealthvault_final.`financial-asset`.ui.share.ShareAssetScreen
import com.wealthvault_final.`financial-asset`.ui.stock.viewmodel.StockScreenModel
import org.jetbrains.compose.resources.painterResource

class StockFormScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { StockScreenModel() }

        val state by screenModel.state.collectAsStateWithLifecycle()

        AssetInputForm(
            initialData = state,
            onBackClick = { navigator.pop() },
            onNextClick = { data ->
                screenModel.updateForm(data)
                navigator.push(ShareAssetScreen(request = data))
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetInputForm(
    initialData: StockModel,
    onBackClick: () -> Unit = {},
    onNextClick: (StockModel) -> Unit
) {
    var type by remember { mutableStateOf(initialData.type) }
    var stockName by remember { mutableStateOf(initialData.stockName) }
    var stockSymbol by remember { mutableStateOf(initialData.stockSymbol) }
    var description by remember { mutableStateOf(initialData.description) }
    var brokerName by remember { mutableStateOf(initialData.brokerName) }

    var quantity by remember { mutableStateOf(if (initialData.quantity == 0.0) "" else initialData.quantity.toString()) }
    var costPerPrice by remember { mutableStateOf(if (initialData.costPerPrice == 0.0) "" else initialData.costPerPrice.toString()) }

    val attachments = remember { mutableStateListOf<Attachment>().apply { addAll(initialData.attachments) } }
    val filePicker = rememberFilePicker { newFiles -> attachments.addAll(newFiles) }

    val isFormValid = type.isNotBlank() && stockName.isNotBlank() && quantity.isNotBlank() && costPerPrice.isNotBlank()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = LightBg,
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
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
                        text = "ข้อมูลการลงทุน",
                        style = MaterialTheme.typography.titleLarge,
                        color = LightPrimary
                    )
                }
            }
        },
        bottomBar = {
            Box(modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(24.dp)) {
                Button(
                    onClick = {
                        val data = StockModel(
                            stockName = stockName,
                            stockSymbol = stockSymbol,
                            quantity = quantity.toDoubleOrNull() ?: 0.0,
                            costPerPrice = costPerPrice.toDoubleOrNull() ?: 0.0,
                            brokerName = brokerName,
                            description = description,
                            attachments = attachments.toList(),
                            type = type
                        )
                        onNextClick(data)
                    },
                    modifier = Modifier.fillMaxWidth().height(46.dp), // 🌟 ปรับสูง 46.dp ตามมาตรฐาน
                    colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
                    shape = RoundedCornerShape(12.dp),
                    enabled = isFormValid
                ) {
                    Text("ต่อไป", style = MaterialTheme.typography.bodyLarge, color = Color.White)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {

            // 🌟 Dropdown ประเภทการลงทุน
            DropdownInput(
                label = "ประเภทการลงทุน*",
                options = investmentTypes,
                selectedValue = type,
                onValueChange = { type = it },
                placeholder = "กรุณาเลือกประเภท"
            )

            // 🌟 ใช้ AssetTextField สำหรับข้อความทั่วไป
            AssetTextField(value = brokerName, onValueChange = { brokerName = it }, label = "แอป / โบรกเกอร์", placeholder = "เช่น Dime, Streaming")
            AssetTextField(value = stockName, onValueChange = { stockName = it }, label = "ชื่อหุ้น / กองทุน*", placeholder = "กรอกชื่อการลงทุน")
            AssetTextField(value = stockSymbol, onValueChange = { stockSymbol = it }, label = "สัญลักษณ์ (Ticker)", placeholder = "เช่น PTT, AAPL")

            // 🌟 ใช้ CustomTextField สำหรับตัวเลขเพื่อให้ UI เหมือน Liability
            CustomTextField(
                value = quantity,
                onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) quantity = it },
                label = "จำนวน*",
                placeholder = "0.00",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            CustomTextField(
                value = costPerPrice,
                onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) costPerPrice = it },
                label = "ราคาทุน (ต่อหน่วย)*",
                placeholder = "0.00",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // 🌟 คำอธิบายแบบ MultiLine
            AssetTextField(value = description, onValueChange = { description = it }, label = "คำอธิบาย", placeholder = "รายละเอียดเพิ่มเติม", isMultiLine = true)

            Spacer(modifier = Modifier.height(8.dp))

            ReferenceImagepicker(
                attachments = attachments,
                onAddImage = { filePicker.launchImage() },
                onAddPdf = { filePicker.launchPdf() },
                onRemove = { item -> attachments.remove(item) }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// 🌟 Component ย่อย CustomTextField (มาตรฐาน Master UI)
@Composable
private fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    readOnly: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onClick: (() -> Unit)? = null
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = LightPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))

        Box {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                readOnly = readOnly,
                singleLine = true,
                keyboardOptions = keyboardOptions,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF3A2F2A)),
                cursorBrush = SolidColor(LightPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(LightSoftWhite, RoundedCornerShape(12.dp))
                            .border(
                                width = 1.dp,
                                color = LightBorder.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            if (value.isEmpty()) {
                                Text(
                                    text = placeholder,
                                    color = Color.LightGray,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                            innerTextField()
                        }

                        if (trailingIcon != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            trailingIcon()
                        }
                    }
                }
            )

            if (onClick != null) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onClick() }
                )
            }
        }
    }
}

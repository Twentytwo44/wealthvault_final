package com.wealthvault.financiallist.ui.asset.form.investment

// 🌟 Import Theme ของแอป

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_back
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault_final.`financial-asset`.Imagepicker.Attachment
import com.wealthvault_final.`financial-asset`.Imagepicker.rememberFilePicker
import com.wealthvault_final.`financial-asset`.model.StockModel
import com.wealthvault_final.`financial-asset`.ui.components.AssetTextField
import com.wealthvault_final.`financial-asset`.ui.components.ReferenceImagepicker
import com.wealthvault_final.`financial-asset`.ui.components.maptype.DropdownInput
import com.wealthvault_final.`financial-asset`.ui.components.maptype.investmentTypes
import org.jetbrains.compose.resources.painterResource

class StockFormScreen(val id: String, val assetData: StockModel? = null) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<StockScreenModel>()

        AssetInputForm(
            onBackClick = { navigator.pop() },
            onNextClick = { data, addedList, deletedList ->
                screenModel.updateForm(data)
                screenModel.updateAttachment(addedList, deletedList)
                screenModel.submitAsset(id)
                navigator.pop() // แก้ไขเสร็จแล้วย้อนกลับ
            },
            assetData = assetData
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetInputForm(
    onBackClick: () -> Unit = {},
    onNextClick: (StockModel, List<Attachment>, List<Attachment>) -> Unit,
    assetData: StockModel? = null
) {
    // 🌟 1. ตั้งค่าเริ่มต้นจากข้อมูลเก่าเพื่อให้พร้อมแก้ไข
    var stockName by remember { mutableStateOf(assetData?.stockName ?: "") }
    var type by remember { mutableStateOf(assetData?.type ?: "") }
    var quantity by remember { mutableStateOf(assetData?.quantity?.toString() ?: "") }
    var costPerPrice by remember { mutableStateOf(assetData?.costPerPrice?.toString() ?: "") }
    var description by remember { mutableStateOf(assetData?.description ?: "") }
    var brokerName by remember { mutableStateOf(assetData?.brokerName ?: "") }
    var stockSymbol by remember { mutableStateOf(assetData?.stockSymbol ?: "") }

    val originalAssets = remember {
        mutableStateListOf<Attachment>().apply {
            addAll(assetData?.attachments ?: emptyList())
        }
    }

    val currentAssets = remember {
        mutableStateListOf<Attachment>().apply {
            addAll(assetData?.attachments ?: emptyList())
        }
    }

    val filePicker = rememberFilePicker { newFiles ->
        currentAssets.addAll(newFiles)
    }

    // เช็คข้อมูลจำเป็น (*)
    val isFormValid = stockName.isNotBlank() && type.isNotBlank() && quantity.isNotBlank() && costPerPrice.isNotBlank()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = LightBg,
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    // 🌟 1. ปรับ Padding เป็น 24.dp ให้ขอบเท่ากัน
                    modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 16.dp, top = 24.dp)
                ) {
                    // 🌟 ถอด IconButton ออก ใช้ Icon + clickable แทน เพื่อแก้ปัญหาขอบดัน
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
                        text = "ข้อมูลหุ้น/กองทุน",
                        style = MaterialTheme.typography.titleLarge,
                        color = LightPrimary
                    )
                }
            }
        },
        bottomBar = {
            Box(modifier = Modifier.navigationBarsPadding().padding(24.dp)) {
                Button(
                    onClick = {
                        val data = StockModel(
                            stockName = stockName,
                            quantity = quantity.toDoubleOrNull() ?: 0.0,
                            description = description,
                            stockSymbol = stockSymbol,
                            brokerName = brokerName,
                            costPerPrice = costPerPrice.toDoubleOrNull() ?: 0.0,
                            attachments = currentAssets, // 🌟 ใช้รายการภาพล่าสุด
                            type = type
                        )

                        val addList = currentAssets.filter { it.id.isNullOrEmpty() }
                        val deleteList = originalAssets.filter { old ->
                            currentAssets.none { it.id == old.id }
                        }

                        onNextClick(data, addList, deleteList)
                    },
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
                    shape = RoundedCornerShape(12.dp),
                    enabled = isFormValid
                ) {
                    Text("ยืนยันการแก้ไข", style = MaterialTheme.typography.bodyLarge, color = Color.White)
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

            DropdownInput(
                label = "ประเภทการลงทุน",
                options = investmentTypes,
                selectedValue = type,
                onValueChange = { type = it },
                placeholder = "เลือกประเภท เช่น หุ้น, กองทุน"
            )

            AssetTextField(
                value = stockName,
                onValueChange = { stockName = it },
                label = "ชื่อหุ้น / กองทุน*",
                placeholder = "ระบุชื่อย่อหรือชื่อเต็ม"
            )

            AssetTextField(
                value = quantity,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                        quantity = newValue
                    }
                },
                label = "จำนวนหุ้น / หน่วย*",
                placeholder = "0.00",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            AssetTextField(
                value = costPerPrice,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                        costPerPrice = newValue
                    }
                },
                label = "ราคาทุนต่อหน่วย*",
                placeholder = "0.00",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            AssetTextField(
                value = brokerName,
                onValueChange = { brokerName = it },
                label = "โบรกเกอร์ / แอปที่ใช้ซื้อ",
                placeholder = "เช่น Streaming, Finansia, K-My Funds"
            )

            AssetTextField(
                value = description,
                onValueChange = { description = it },
                label = "รายละเอียดเพิ่มเติม",
                placeholder = "ระบุรายละเอียดเพิ่มเติม",
                isMultiLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            ReferenceImagepicker(
                attachments = currentAssets,
                onAddImage = { filePicker.launchImage() },
                onAddPdf = { filePicker.launchPdf() },
                onRemove = { item -> currentAssets.remove(item) }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
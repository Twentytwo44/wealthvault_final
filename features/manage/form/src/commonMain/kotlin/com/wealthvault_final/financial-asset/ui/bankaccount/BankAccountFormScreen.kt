package com.wealthvault_final.`financial-asset`.ui.bankaccount

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
import androidx.compose.runtime.collectAsState
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
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_back
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault_final.`financial-asset`.Imagepicker.Attachment
import com.wealthvault_final.`financial-asset`.Imagepicker.rememberFilePicker
import com.wealthvault_final.`financial-asset`.model.BankAccountModel
import com.wealthvault_final.`financial-asset`.ui.bankaccount.viewmodel.BankAccountScreenModel
import com.wealthvault_final.`financial-asset`.ui.components.AssetTextField
import com.wealthvault_final.`financial-asset`.ui.components.ReferenceImagepicker
import com.wealthvault_final.`financial-asset`.ui.components.maptype.DropdownInput
import com.wealthvault_final.`financial-asset`.ui.components.maptype.bankAccountTypes
import com.wealthvault_final.`financial-asset`.ui.share.ShareAssetScreen
import org.jetbrains.compose.resources.painterResource

class BankAccountFormScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { BankAccountScreenModel() }
        val state by screenModel.state.collectAsState()

        BankAccountInputForm(
            initialData = state,
            onBackClick = { navigator.pop() } ,
            onNextClick = { data ->
                println("data asset input: ${data.attachments}")
                // 1. อัปเดตข้อมูลที่รับมาจาก Form เข้าไปใน Model ก่อน
                screenModel.updateForm(data)

                // 2. 🌟 ส่งไปหน้า ShareAssetScreen ด้วย state.value ตามโครงสร้างที่ถูกต้อง
                navigator.push(ShareAssetScreen(
                    request = screenModel.state.value
                ))
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankAccountInputForm(
    initialData: BankAccountModel,
    onBackClick: () -> Unit = {},
    onNextClick: (BankAccountModel) -> Unit
) {
    // variables
    var name by remember { mutableStateOf(initialData.name) }
    var type by remember { mutableStateOf(initialData.type) }
    var bankName by remember { mutableStateOf(initialData.bankName) }
    var bankId by remember { mutableStateOf(initialData.bankId) }
    var description by remember { mutableStateOf(initialData.description) }

    // เรื่องตัวเลข ถ้าเป็น 0.0 ให้แสดงหน้าว่างๆ ดีกว่าครับ
    var amount by remember { mutableStateOf(if (initialData.amount == 0.0) "" else initialData.amount.toString()) }

    // สำหรับลิสต์ ดึงค่าเก่ามายัดใส่แบบนี้
    val attachments = remember { mutableStateListOf<Attachment>().apply { addAll(initialData.attachments) } }

    val filePicker = rememberFilePicker { newFiles ->
        attachments.addAll(newFiles)
    }

    // 🌟 เช็คข้อมูลที่จำเป็นก่อนให้กด "ต่อไป"
    val isFormValid = name.isNotBlank() && type.isNotBlank() && bankName.isNotBlank() && bankId.isNotBlank() && amount.isNotBlank()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = LightBg, // 🌟 ใช้ LightBg
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) { // 🌟 กันขอบขาว

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    // 🌟 1. ปรับ Padding ของ TopBar ขอบซ้าย-ขวา เป็น 24.dp
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
                        text = "ข้อมูลบัญชีเงินฝาก",
                        style = MaterialTheme.typography.titleLarge,
                        color = LightPrimary
                    )
                }
            }
        },
        bottomBar = {
            // 🌟 ย้ายปุ่มมาไว้ BottomBar ให้ใช้งานง่ายขึ้น และปรับดีไซน์ใหม่
            Box(modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(24.dp)) {
                Button(
                    onClick = {
                        val data = BankAccountModel(
                            name = name,
                            type = type,
                            bankName = bankName,
                            bankId = bankId,
                            description = description,
                            amount = amount.toDoubleOrNull() ?: 0.0,
                            attachments = attachments.toList()
                        )
                        println("data attachment: ${data.attachments}")
                        onNextClick(data)
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
                    shape = RoundedCornerShape(12.dp),
                    enabled = isFormValid // 🌟 ปุ่มจะกดได้ก็ต่อเมื่อข้อมูลครบ
                ) {
                    Text("ต่อไป", style = MaterialTheme.typography.titleMedium, color = Color.White)
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
            Spacer(modifier = Modifier.height(16.dp))

            DropdownInput(
                label = "ประเภทบัญชีธนาคาร*",
                options = bankAccountTypes,
                selectedValue = type,
                onValueChange = { type = it },
                placeholder = "กรุณาเลือกประเภทบัญชี"
            )

            AssetTextField(value = name, onValueChange = { name = it }, label = "ชื่อบัญชี*", placeholder = "กรอกชื่อบัญชี")
            AssetTextField(value = bankName, onValueChange = { bankName = it }, label = "ธนาคาร*", placeholder = "กรอกชื่อธนาคาร")

            AssetTextField(
                value = bankId,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                        bankId = newValue
                    }
                },
                label = "เลขบัญชี*",
                placeholder = "กรอกเลขบัญชี",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            AssetTextField(
                value = amount,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*\$"))) {
                        amount = newValue
                    }
                },
                label = "จำนวนเงิน*",
                placeholder = "0.00",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            AssetTextField(value = description, onValueChange = { description = it }, label = "คำอธิบาย", placeholder = "รายละเอียดเพิ่มเติม", isMultiLine = true)

            Spacer(modifier = Modifier.height(24.dp))

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
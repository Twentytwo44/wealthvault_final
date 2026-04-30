package com.wealthvault_final.`financial-asset`.ui.bankaccount

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

// 🌟 Import Theme ของแอป
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

class BankAccountFormScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { BankAccountScreenModel() }

        BankAccountInputForm(
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
    onBackClick: () -> Unit = {},
    onNextClick: (BankAccountModel) -> Unit
) {
    // variables
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var bankName by remember { mutableStateOf("") }
    var bankId by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    val attachments = remember { mutableStateListOf<Attachment>() }

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
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    title = {
                        Text(
                            "ข้อมูลบัญชีเงินฝาก",
                            color = LightPrimary, // 🌟 ใช้ LightPrimary
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null, tint = LightPrimary)
                        }
                    }
                )
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
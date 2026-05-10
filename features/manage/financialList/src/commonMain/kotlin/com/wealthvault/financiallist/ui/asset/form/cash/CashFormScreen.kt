package com.wealthvault.financiallist.ui.asset.form.cash

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
import com.wealthvault_final.`financial-asset`.model.CashModel
import com.wealthvault_final.`financial-asset`.ui.components.AssetTextField
import com.wealthvault_final.`financial-asset`.ui.components.ReferenceImagepicker
import org.jetbrains.compose.resources.painterResource

data class CashFormScreen(val id: String, val cashData: CashModel) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<CashScreenModel>()

        CashInputForm(
            onBackClick = { navigator.pop() },
            onNextClick = { data, addedList, deletedList ->
                screenModel.updateForm(data)
                screenModel.updateAttachment(addedList, deletedList)
                screenModel.submitCash(id,
                    onSuccess = {
                        // 💡 หลังจากแก้ไขสำเร็จ จะส่งกลับหน้าลิสต์
                        navigator.pop()
                    })

            },
            cashData = cashData
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashInputForm(
    onBackClick: () -> Unit = {},
    onNextClick: (CashModel, List<Attachment>, List<Attachment>) -> Unit,
    cashData: CashModel? = null
) {
    // 🌟 1. ดึงข้อมูลเดิมมาตั้งค่าเริ่มต้นเพื่อให้ User แก้ไขง่ายๆ
    var cashName by remember { mutableStateOf(cashData?.cashName ?: "") }
    var amount by remember { mutableStateOf(cashData?.amount?.toString() ?: "") }
    var description by remember { mutableStateOf(cashData?.description ?: "") }

    val originalAssets = remember {
        mutableStateListOf<Attachment>().apply {
            addAll(cashData?.attachments ?: emptyList())
        }
    }

    val currentAssets = remember {
        mutableStateListOf<Attachment>().apply {
            addAll(cashData?.attachments ?: emptyList())
        }
    }

    val filePicker = rememberFilePicker { newFiles ->
        currentAssets.addAll(newFiles)
    }

    // 🌟 ตรวจสอบความถูกต้องของข้อมูล
    val isFormValid = cashName.isNotBlank() && amount.isNotBlank()

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
                        text = "แก้ไขข้อมูลเงินสด",
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
                        val data = CashModel(
                            cashName = cashName,
                            description = description,
                            amount = amount.toDoubleOrNull() ?: 0.0,
                            attachments = currentAssets
                        )

                        val addList = currentAssets.filter { it.id.isNullOrEmpty() }
                        val deleteList = originalAssets.filter { originalItem ->
                            currentAssets.none { it.id == originalItem.id }
                        }

                        onNextClick(data, addList, deleteList)
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp), // 🌟 สูง 50.dp
                    colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
                    shape = RoundedCornerShape(12.dp), // 🌟 โค้ง 12.dp
                    enabled = isFormValid
                ) {
                    Text("ยืนยันการแก้ไข", style = MaterialTheme.typography.titleMedium, color = Color.White)
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

            AssetTextField(
                value = cashName,
                onValueChange = { cashName = it },
                label = "ชื่อรายการ*",
                placeholder = "เช่น เงินสดในกระเป๋า, ทองคำแท่ง"
            )

            AssetTextField(
                value = amount,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.all { it.isDigit() || it == '.' }) {
                        amount = newValue
                    }
                },
                label = "จำนวนเงิน / มูลค่า*",
                placeholder = "0.00",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            AssetTextField(
                value = description,
                onValueChange = { description = it },
                label = "คำอธิบาย",
                placeholder = "ระบุรายละเอียดเพิ่มเติม",
                isMultiLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

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

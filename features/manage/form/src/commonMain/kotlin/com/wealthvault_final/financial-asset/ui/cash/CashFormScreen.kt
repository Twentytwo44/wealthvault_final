package com.wealthvault_final.`financial-asset`.ui.cash

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
import com.wealthvault_final.`financial-asset`.model.CashModel
import com.wealthvault_final.`financial-asset`.ui.cash.viewmodel.CashScreenModel
import com.wealthvault_final.`financial-asset`.ui.components.AssetTextField
import com.wealthvault_final.`financial-asset`.ui.components.ReferenceImagepicker
import com.wealthvault_final.`financial-asset`.ui.share.ShareAssetScreen
import org.jetbrains.compose.resources.painterResource

class CashFormScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { CashScreenModel() }

        // 🌟 1. ดึง State ปัจจุบันออกมา
        val state by screenModel.state.collectAsState()

        CashInputForm(
            initialData = state, // 🌟 2. โยนค่าเดิมเข้าไปตั้งต้นให้ฟอร์ม
            onBackClick = { navigator.pop() },
            onNextClick = { data ->
                println("data asset input: ${data.attachments}")

                // อัปเดตข้อมูลที่รับมาจาก Form เข้าไปใน Model ก่อน
                screenModel.updateForm(data)

                // ส่งไปหน้า ShareAssetScreen ด้วย state.value ตามโครงสร้างที่ถูกต้อง
                navigator.push(ShareAssetScreen(request = screenModel.state.value))
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashInputForm(
    initialData: CashModel, // 🌟 รับค่าเริ่มต้น
    onBackClick: () -> Unit = {},
    onNextClick: (CashModel) -> Unit
) {
    // 🌟 ดึงค่าจาก initialData มาใส่ตอนเริ่มต้น แทนที่จะเริ่มด้วย ""
    var cashName by remember { mutableStateOf(initialData.cashName) }
    var description by remember { mutableStateOf(initialData.description) }

    // เรื่องตัวเลข ถ้าเป็น 0.0 ให้แสดงหน้าว่างๆ
    var amount by remember { mutableStateOf(if (initialData.amount == 0.0) "" else initialData.amount.toString()) }

    // สำหรับลิสต์ ดึงค่าเก่ามายัดใส่แบบนี้
    val attachments = remember { mutableStateListOf<Attachment>().apply { addAll(initialData.attachments) } }

    val filePicker = rememberFilePicker { newFiles ->
        attachments.addAll(newFiles)
    }

    // 🌟 เช็คข้อมูลจำเป็น (กันแอปพัง/ข้อมูลโหว่)
    val isFormValid = cashName.isNotBlank() && amount.isNotBlank()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = LightBg, // 🌟 ใช้ LightBg
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
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
                        text = "ข้อมูลเงินสด ทองคำ",
                        style = MaterialTheme.typography.titleLarge,
                        color = LightPrimary
                    )
                }
            }
        },
        bottomBar = {
            // 🌟 ย้ายปุ่มมาไว้ BottomBar และปรับดีไซน์ใหม่
            Box(modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(24.dp)) {
                Button(
                    onClick = {
                        val data = CashModel(
                            cashName = cashName,
                            description = description,
                            amount = amount.toDoubleOrNull() ?: 0.0,
                            attachments = attachments.toList() // ใช้ toList เพื่อความปลอดภัย
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

            // ส่วนกรอกข้อมูลหลัก
            AssetTextField(value = cashName, onValueChange = { cashName = it }, label = "ชื่อรายการ*", placeholder = "เช่น เงินสดในมือ, ทองคำแท่ง")

            AssetTextField(
                value = amount,
                onValueChange = { newValue ->
                    // ✅ ใช้ Regex เพื่อให้พิมพ์ตัวเลขและจุดทศนิยมได้แค่ 1 จุด (เช่น 1500.50)
                    if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*\$"))) {
                        amount = newValue
                    }
                },
                label = "จำนวน*",
                placeholder = "0.00",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number) // 🌟 บังคับเด้งแป้นตัวเลข
            )

            AssetTextField(value = description, onValueChange = { description = it }, label = "คำอธิบาย", placeholder = "รายละเอียดเพิ่มเติม", isMultiLine = true)

            Spacer(modifier = Modifier.height(24.dp))

            // ส่ง State และคำสั่ง Launch เข้าไปใน ReferenceImagepicker
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
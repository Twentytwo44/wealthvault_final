package com.wealthvault.financiallist.ui.asset.form.insurance

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
import com.wealthvault_final.`financial-asset`.model.InsuranceModel
import com.wealthvault_final.`financial-asset`.ui.components.AssetTextField
import com.wealthvault_final.`financial-asset`.ui.components.ReferenceImagepicker
import com.wealthvault_final.`financial-asset`.ui.components.maptype.DropdownInput
import com.wealthvault_final.`financial-asset`.ui.components.maptype.insuranceTypes
import org.jetbrains.compose.resources.painterResource

class InsuranceFormScreen(val id: String, val insuranceData: InsuranceModel) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<InsuranceScreenModel>()

        InsuranceInputForm(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsuranceInputForm(
    onBackClick: () -> Unit = {},
    onNextClick: (InsuranceModel, List<Attachment>, List<Attachment>) -> Unit,
    insuranceData: InsuranceModel? = null
) {
    // 🌟 1. ดึงข้อมูลเดิมมาตั้งค่าเริ่มต้น (Support Edit Mode)
    var name by remember { mutableStateOf(insuranceData?.name ?: "") }
    var policyNumber by remember { mutableStateOf(insuranceData?.policyNumber ?: "") }
    var type by remember { mutableStateOf(insuranceData?.type ?: "") }
    var companyName by remember { mutableStateOf(insuranceData?.companyName ?: "") }
    var coverageAmount by remember { mutableStateOf(insuranceData?.coverageAmount?.toString() ?: "") }
    var coveragePeriod by remember { mutableStateOf(insuranceData?.coveragePeriod ?: "") }
    var expDate by remember { mutableStateOf(insuranceData?.expDate ?: "") }
    var conDate by remember { mutableStateOf(insuranceData?.conDate ?: "") }
    var description by remember { mutableStateOf(insuranceData?.description ?: "") }

    val originalAssets = remember {
        mutableStateListOf<Attachment>().apply {
            addAll(insuranceData?.attachments ?: emptyList())
        }
    }

    val currentAssets = remember {
        mutableStateListOf<Attachment>().apply {
            addAll(insuranceData?.attachments ?: emptyList())
        }
    }

    val filePicker = rememberFilePicker { newFiles ->
        currentAssets.addAll(newFiles)
    }

    // เช็คข้อมูลจำเป็น
    val isFormValid = name.isNotBlank() && policyNumber.isNotBlank() && companyName.isNotBlank() && coverageAmount.isNotBlank()

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
                        text = "แก้ไขข้อมูลประกัน",
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
                        val data = InsuranceModel(
                            policyNumber = policyNumber,
                            type = type,
                            companyName = companyName,
                            coverageAmount = coverageAmount.toDoubleOrNull() ?: 0.0,
                            coveragePeriod = coveragePeriod,
                            expDate = expDate,
                            conDate = conDate,
                            description = description,
                            name = name,
                            attachments = currentAssets // 🌟 ใช้รายการล่าสุด (รวมที่เพิ่มใหม่และลบออก)
                        )

                        val addList = currentAssets.filter { it.id.isNullOrEmpty() }
                        val deleteList = originalAssets.filter { old ->
                            currentAssets.none { it.id == old.id }
                        }

                        onNextClick(data, addList, deleteList)
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
                    shape = RoundedCornerShape(12.dp),
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

            DropdownInput(
                label = "ประเภทประกัน",
                options = insuranceTypes,
                selectedValue = type,
                onValueChange = { type = it },
                placeholder = "กรุณาเลือกประเภทประกัน"
            )

            AssetTextField(value = name, onValueChange = { name = it }, label = "ชื่อประกัน*", placeholder = "ระบุชื่อเรียกประกัน")
            AssetTextField(value = policyNumber, onValueChange = { policyNumber = it }, label = "เลขกรมธรรม์*", placeholder = "ระบุเลขที่กรมธรรม์")
            AssetTextField(value = companyName, onValueChange = { companyName = it }, label = "บริษัทประกัน*", placeholder = "ระบุชื่อบริษัท")

            AssetTextField(
                value = coverageAmount,
                onValueChange = { if (it.isEmpty() || it.all { char -> char.isDigit() }) coverageAmount = it },
                label = "วงเงินคุ้มครอง*",
                placeholder = "0.00",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            AssetTextField(value = coveragePeriod, onValueChange = { coveragePeriod = it }, label = "ระยะเวลาคุ้มครอง*", placeholder = "เช่น 10 ปี, ตลอดชีพ")
            AssetTextField(value = expDate, onValueChange = { expDate = it }, label = "วันหมดอายุ*", placeholder = "วว/ดด/ปปปป")
            AssetTextField(value = conDate, onValueChange = { conDate = it }, label = "วันที่เริ่มสัญญา*", placeholder = "วว/ดด/ปปปป")

            AssetTextField(
                value = description,
                onValueChange = { description = it },
                label = "รายละเอียดเพิ่มเติม",
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

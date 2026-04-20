package com.wealthvault.financiallist.ui.asset.form.insurance

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault_final.`financial-asset`.Imagepicker.Attachment
import com.wealthvault_final.`financial-asset`.Imagepicker.rememberFilePicker
import com.wealthvault_final.`financial-asset`.model.InsuranceModel
import com.wealthvault_final.`financial-asset`.ui.components.AssetTextField
import com.wealthvault_final.`financial-asset`.ui.components.ReferenceImagepicker
import com.wealthvault_final.`financial-asset`.ui.components.maptype.DropdownInput
import com.wealthvault_final.`financial-asset`.ui.components.maptype.insuranceTypes

class InsuranceFormScreen(val id:String,val insuranceData: InsuranceModel) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<InsuranceScreenModel>()

        InsuranceInputForm(
            onBackClick = { navigator.pop() } ,
            onNextClick = { data,addedList,deletedList ->
                screenModel.updateForm(data)
                screenModel.updateAttachment(addedList,deletedList)
                screenModel.submitInsurance(id)
            },
            insuranceData = insuranceData
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsuranceInputForm(
    onBackClick: () -> Unit = {},
    onNextClick: (InsuranceModel,List<Attachment>,List<Attachment>) -> Unit,
    insuranceData: InsuranceModel? = null
) {
    // 1. สร้าง State เก็บรายการไฟล์ที่เลือกมา

    // 2. เรียกใช้ File Picker แบบ Native


    // variable


    var policyNumber by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var companyName by remember { mutableStateOf("")}
    var coverageAmount by remember { mutableStateOf("")}
    var coveragePeriod by remember { mutableStateOf("")}
    var expDate by remember { mutableStateOf("")}
    var conDate by remember { mutableStateOf("")}
    var description by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }


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
    val attachments = remember { mutableStateListOf<Attachment>() }

    val filePicker = rememberFilePicker { newFiles ->
        currentAssets.addAll(newFiles)
    }



    Scaffold(
        containerColor = Color(0xFFFFF8F3),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                ),
                title = {
                    Text(
                        "ข้อมูลประกัน",
                        color = Color(0xFF8D6E63),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color(0xFF8D6E63))
                    }
                }
            )
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
                data = insuranceData?.type
            )
            AssetTextField(value = name, onValueChange = { name = it }, label = "ชื่อประกัน", placeholder = insuranceData?.name ?: "")
            AssetTextField(value = policyNumber, onValueChange = { policyNumber = it }, label = "เลขประกัน*", placeholder = insuranceData?.policyNumber ?: "")
            AssetTextField(value = companyName, onValueChange = { companyName = it }, label = "ชื่อบริษัทประกัน*", placeholder = insuranceData?.companyName ?: "")

            AssetTextField(
                value = coverageAmount,
                onValueChange = { newValue ->
                    // ดักให้พิมพ์ได้แค่ตัวเลขเท่านั้น ป้องกัน NumberFormatException 100%
                    if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                        coverageAmount = newValue
                    }
                },
                label = "จำนวนวงเงินคุ้มครอง*",
                placeholder = insuranceData?.coverageAmount?.toString() ?: "",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number) // 🌟 เด้งคีย์บอร์ดตัวเลข
            )

            AssetTextField(value = coveragePeriod, onValueChange = { coveragePeriod = it }, label = "ระยะเวลาคุ้มครอง*", placeholder = insuranceData?.coveragePeriod ?: "")
            AssetTextField(value = expDate, onValueChange = { expDate = it }, label = "วันหมดอายุ*", placeholder = insuranceData?.expDate ?: "")
            AssetTextField(value = conDate, onValueChange = { conDate = it }, label = "วันที่สัญญา*", placeholder = insuranceData?.conDate ?: "")

            AssetTextField(
                value = description,
                onValueChange = { description = it },
                label = "คำอธิบาย*",
                placeholder = insuranceData?.description ?: "",
                isMultiLine = true // 🌟 ทำให้กล่องข้อความใหญ่ขึ้นตามที่คุณทำไว้ใน Component
            )
            Spacer(modifier = Modifier.height(24.dp))

            // 3. ส่ง State และคำสั่ง Launch เข้าไปใน ReferenceSection
            ReferenceImagepicker(
                attachments = currentAssets, // ใช้ตัวแปรตัวที่ 2 (image2)
                onAddImage = { filePicker.launchImage() },
                onAddPdf = { filePicker.launchPdf() },
                onRemove = { item -> currentAssets.remove(item) } // ลบออกจากรายการที่โชว์
            )

            Spacer(modifier = Modifier.height(40.dp))

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
                        attachments = attachments.toList()
                    )
                    val addList = currentAssets.filter { it.id.isNullOrEmpty() }

                    val deleteList = originalAssets.filter { originalItem ->
                        currentAssets.none { it.id == originalItem.id }
                    }

                    println("Added List Size: ${addList.size} รายการ")
                    println("Delete List Size: ${deleteList.size} รายการ")
                    println("Delete ID: ${deleteList.map { it.id }}")
                    println("Data Attachment: ${data.attachments}")
                    onNextClick(data,addList,deleteList)

                }, // อนาคตสามารถโยนค่า attachments ไปให้ ViewModel ตรงนี้ได้เลย
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFB08968)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("ต่อไป", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}



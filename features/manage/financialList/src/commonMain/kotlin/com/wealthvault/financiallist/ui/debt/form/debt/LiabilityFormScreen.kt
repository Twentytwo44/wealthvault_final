package com.wealthvault.financiallist.ui.debt.form.debt

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault_final.`financial-asset`.Imagepicker.Attachment
import com.wealthvault_final.`financial-asset`.Imagepicker.rememberFilePicker
import com.wealthvault_final.`financial-asset`.ui.components.AssetTextField
import com.wealthvault_final.`financial-asset`.ui.components.ReferenceImagepicker
import com.wealthvault_final.`financial-obligations`.model.LiabilityModel


class LiabilityFormScreen(val id:String,val debtData: LiabilityModel) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<LiabilityScreenModel>()

        LiabilityInputForm(
            onBackClick = { navigator.pop() } ,
            onNextClick = { data,addedList,deletedList ->
                println("data asset input: ${data.attachments}")
                screenModel.updateForm(data)
                screenModel.updateAttachment(addedList,deletedList)
                screenModel.submitLiability(id)
            },
            debtData = debtData
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiabilityInputForm(
    onBackClick: () -> Unit = {},
    onNextClick: (LiabilityModel,List<Attachment>,List<Attachment>) -> Unit,
    debtData: LiabilityModel? = null
) {
    // 1. สร้าง State เก็บรายการไฟล์ที่เลือกมา

    // 2. เรียกใช้ File Picker แบบ Native


    // variable
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var statedAt by remember { mutableStateOf("")}
    var principal by remember { mutableStateOf("")}
    var description by remember { mutableStateOf("") }
    var endedAt by remember { mutableStateOf("") }
    var interedRate by remember { mutableStateOf("") }
    var creditor by remember { mutableStateOf("") }


    val originalAssets = remember {
        mutableStateListOf<Attachment>().apply {
            addAll(debtData?.attachments ?: emptyList())
        }
    }

    val currentAssets = remember {
        mutableStateListOf<Attachment>().apply {
            addAll(debtData?.attachments ?: emptyList())
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
                        "ข้อมูลหนี้",
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

            // ส่วนกรอกข้อมูลหลัก
            AssetTextField(value = name, onValueChange = { name = it }, label = "ชื่อ*", placeholder = debtData?.name ?: "")
            AssetTextField(value = interedRate, onValueChange = { interedRate = it }, label = "ดอกเบี้ย*", placeholder = debtData?.interestRate.toString())
            AssetTextField(value = principal, onValueChange = { principal = it }, label = "จำนวน*", placeholder = debtData?.principal.toString())
            AssetTextField(value = statedAt, onValueChange = { statedAt = it }, label = "วันที่เริ่มต้น*", placeholder = debtData?.startedAt.toString())
            AssetTextField(value = endedAt, onValueChange = { endedAt = it }, label = "วันที่สิ้นสุด*", placeholder = debtData?.endedAt.toString())
            AssetTextField(value = creditor, onValueChange = { creditor =it }, label = "เจ้าหนี้*", placeholder = debtData?.creditor.toString())
            AssetTextField(value = description, onValueChange = { description = it }, label = "คำอธิบาย", placeholder = debtData?.description ?: "", isMultiLine = true)

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
                    val data = LiabilityModel(
                        name = name,
                        type = type,
                        principal = principal.toDouble(),
                        interestRate = "",
                        startedAt = statedAt,
                        endedAt = "",
                        creditor = "",
                        description = description,
                        attachments = attachments
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



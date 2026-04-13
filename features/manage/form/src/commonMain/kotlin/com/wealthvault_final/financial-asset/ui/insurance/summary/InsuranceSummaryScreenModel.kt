package com.wealthvault_final.`financial-asset`.ui.insurance.summary

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.insurance_api.model.InsuranceFileUploadData
import com.wealthvault.insurance_api.model.InsuranceRequest
import com.wealthvault.share_api.model.ShareItemRequest
import com.wealthvault.share_api.model.TargetItem
import com.wealthvault_final.`financial-asset`.data.insurance.InsuranceRepositoryImpl
import com.wealthvault_final.`financial-asset`.data.share.ShareItemRepositoryImpl
import com.wealthvault_final.`financial-asset`.model.InsuranceModel
import com.wealthvault_final.`financial-asset`.model.ShareTo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class InsuranceSummaryState(
    val insuranceRequest: InsuranceModel? = null,
    val shareTo: ShareTo? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
)
class InsuranceSummaryScreenModel(
    private val insuranceRepository: InsuranceRepositoryImpl,
    private val shareItemRepository: ShareItemRepositoryImpl,
) : ScreenModel {

    private val _state = MutableStateFlow(InsuranceSummaryState())
    val state = _state.asStateFlow()
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    fun initData(request: InsuranceModel) {
        _state.update { it.copy(insuranceRequest = request) }
        println("state"+ _state.value)

    }
    fun initShareInfo(request: ShareTo) {
        _state.update { it.copy(shareTo = request) }
        println("state"+ _state.value)

    }
    private fun asRequest(): InsuranceRequest {
        val current = _state.value.insuranceRequest

        // ✅ Map ข้อมูลให้มีทั้ง Byte, MimeType และ ชื่อไฟล์
        val allFiles = current?.attachments?.mapNotNull { attachment ->
            val bytes = attachment.platformData as? ByteArray ?: return@mapNotNull null

            // เช็กว่าเป็น PDF หรือ รูปภาพ
            val isPdf = attachment.name.endsWith(".pdf", ignoreCase = true) || attachment.type.toString().contains("PDF")
            val mimeType = if (isPdf) "application/pdf" else "image/jpeg"
            val extension = if (isPdf) "pdf" else "jpg"

            // ตั้งชื่อไฟล์ (เอา symbol มาต่อกับ index หรือเวลาเพื่อไม่ให้ซ้ำ)
            val fileName = "${current.name}.$extension"

            InsuranceFileUploadData(bytes = bytes, mimeType = mimeType, fileName = fileName)
        } ?: emptyList()

        return InsuranceRequest(
            name = current?.name ?: "",
            policyNumber = current?.policyNumber ?: "",
            type = current?.type ?: "",
            companyName = current?.companyName ?: "",
            coverageAmount = current?.coverageAmount ?: 0.0,
            coveragePeriod = current?.coveragePeroid ?: "",
            conDate = current?.conDate ?: "",
            expDate = current?.expDate ?: "",
            description = current?.description ?: "",

            // ส่ง List ของไฟล์ที่เราแปลงเสร็จแล้วในข้อ 1 เข้าไป
            files = allFiles
        )
    }



    fun submitInsurance() {
        val shareToData = _state.value.shareTo ?: return
        screenModelScope.launch {
            try {
                isLoading = true
                errorMessage = null

                // --- ขั้นตอนที่ 1: สร้าง Insurance ก่อน ---

                val requestBody = asRequest()

                val insuranceResult = insuranceRepository.createInsurance(requestBody)

                // ดึงข้อมูลออกมาจาก Result Wrapper
                val insuranceResponse = insuranceResult.getOrNull()

                if (insuranceResult.isSuccess && insuranceResponse != null) {
                    // ✅ ดึง ID ที่ได้จาก API ของการสร้าง Insurance
                    // สมมติว่า field id อยู่ใน insuranceResponse.data.id หรือตาม Model ของคุณ
                    val createdItemId = insuranceResponse.id.toString()
                    println("✅ [ScreenModel] Insurance Created ID: $createdItemId")

                    delay(10000)
                    // --- ขั้นตอนที่ 2: เตรียมข้อมูลเพื่อ Share โดยใช้ ID ที่เพิ่งได้มา ---
                    val requestShareItem = ShareItemRequest(
                        itemIds = createdItemId, // 👈 ใส่ ID ที่ได้จากขั้นตอนที่ 1
                        itemTypes = "insurance",
                        emails = shareToData.email.map {
                            TargetItem(
                                id = it.name,
                                shareAt = shareToData.shareAt
                            )
                        },
                        friends = shareToData.friend.map {
                            TargetItem(
                                id = it.userId,
                                shareAt = shareToData.shareAt
                            )
                        },
                        groups = shareToData.group.map {
                            TargetItem(
                                id = it.userId,
                                shareAt = shareToData.shareAt
                            )
                        }
                    )

                    // --- ขั้นตอนที่ 3: ยิง API แชร์ทรัพย์สิน ---
                    val shareResult = shareItemRepository.shareItem(requestShareItem)
                    println(" [SummaryScreenModel] Share result: $shareResult")

                }
                else {
                    println("❌ [ScreenModel] Create Insurance Failed")
            }

            } catch (e: Exception) {
                println("❌ [ScreenModel] Exception: ${e.message}")
                errorMessage = e.message ?: "เกิดข้อผิดพลาดในการเชื่อมต่อ"
            } finally {
                isLoading = false
                println("🏁 [ScreenModel] Process Finished.")
            }
        }
    }







}

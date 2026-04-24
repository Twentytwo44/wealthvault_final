package com.wealthvault_final.`financial-obligations`.ui.liability.summary

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.liability_api.model.LiabilityRequest
import com.wealthvault.liability_api.model.LiabilityUploadData
import com.wealthvault.share_api.model.ShareItemRequest
import com.wealthvault.share_api.model.TargetItem
import com.wealthvault_final.`financial-asset`.data.share.ShareItemRepositoryImpl
import com.wealthvault_final.`financial-asset`.model.ShareTo
import com.wealthvault_final.`financial-obligations`.data.liability.LiabilityRepositoryImpl
import com.wealthvault_final.`financial-obligations`.model.LiabilityModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class LiabilitySummaryState(
    val liabilityRequest: LiabilityModel? = null,
    val shareTo: ShareTo? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
)
class LiabilitySummaryScreenModel(
    private val liabilityRepository: LiabilityRepositoryImpl,
    private val shareItemRepository: ShareItemRepositoryImpl,
) : ScreenModel {

    private val _state = MutableStateFlow(LiabilitySummaryState())
    val state = _state.asStateFlow()
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    fun initData(request: LiabilityModel) {
        _state.update { it.copy(liabilityRequest = request) }
        println("state"+ _state.value)

    }
    fun initShareInfo(request: ShareTo) {
        _state.update { it.copy(shareTo = request) }
        println("state"+ _state.value)

    }
    private fun asRequest(): LiabilityRequest {
        val current = _state.value.liabilityRequest

        // ✅ Map ข้อมูลให้มีทั้ง Byte, MimeType และ ชื่อไฟล์
        val allFiles = current?.attachments?.mapNotNull { attachment ->
            val bytes = attachment.platformData as? ByteArray ?: return@mapNotNull null

            // เช็กว่าเป็น PDF หรือ รูปภาพ
            val isPdf = attachment.name.endsWith(".pdf", ignoreCase = true) || attachment.type.toString().contains("PDF")
            val mimeType = if (isPdf) "application/pdf" else "image/jpeg"
            val extension = if (isPdf) "pdf" else "jpg"

            // ตั้งชื่อไฟล์ (เอา symbol มาต่อกับ index หรือเวลาเพื่อไม่ให้ซ้ำ)
            val fileName = "${current.name}.$extension"

            LiabilityUploadData(bytes = bytes, mimeType = mimeType, fileName = fileName)
        } ?: emptyList()

        return LiabilityRequest(
            name = current?.name ?: "",
            type = "LIABILITY_TYPE_DEBT",
            description = current?.description ?: "",
            startedAt = current?.startedAt ?: "",
            endedAt = current?.endedAt ?: "",
            creditor = current?.creditor ?: "",
            interestRate = current?.interestRate ?: "",
            principal = current?.principal ?: 0.0,
            files = allFiles
        )
    }



    fun submitLiability(onSuccess: () -> Unit) {
        val shareToData = _state.value.shareTo ?: return
        screenModelScope.launch {
            try {
                isLoading = true
                errorMessage = null

                // --- ขั้นตอนที่ 1: สร้าง Liability ก่อน ---

                val requestBody = asRequest()

                val liabilityResult = liabilityRepository.createLiability(requestBody)

                // ดึงข้อมูลออกมาจาก Result Wrapper
                val liabilityResponse = liabilityResult.getOrNull()

                if (liabilityResult.isSuccess && liabilityResponse != null) {
                    // ✅ ดึง ID ที่ได้จาก API ของการสร้าง Liability
                    // สมมติว่า field id อยู่ใน liabilityResponse.data.id หรือตาม Model ของคุณ
                    val createdItemId = liabilityResponse.id.toString()
                    println("✅ [ScreenModel] Liability Created ID: $createdItemId")

                    delay(10000)
                    // --- ขั้นตอนที่ 2: เตรียมข้อมูลเพื่อ Share โดยใช้ ID ที่เพิ่งได้มา ---
                    val requestShareItem = ShareItemRequest(
                        itemIds = createdItemId, // 👈 ใส่ ID ที่ได้จากขั้นตอนที่ 1
                        itemTypes = "liability",
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
                    onSuccess()
                }
                else {
                    println("❌ [ScreenModel] Create Liability Failed")
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

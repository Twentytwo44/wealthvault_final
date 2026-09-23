package com.wealthvault.`financial-asset`.ui.realestate.land.summary

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.core.architecture.FormEffect
import com.wealthvault.core.architecture.UiState
import com.wealthvault.core.architecture.UiStateHolder
import com.wealthvault.financialcommon.architecture.SummaryAction
import com.wealthvault.domain.portfolio.LandFileUploadData
import com.wealthvault.domain.portfolio.LandReferenceData
import com.wealthvault.domain.portfolio.LandRequest
import com.wealthvault.domain.social.ShareItems
import com.wealthvault.domain.social.ShareTarget
import com.wealthvault.domain.portfolio.CreateLandRepository
import com.wealthvault.domain.social.ShareItemRepository
import com.wealthvault.domain.portfolio.LandModel
import com.wealthvault.domain.social.ShareTo
import com.wealthvault.core.model.Money
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


data class LandSummaryState(
    val landRequest: LandModel? = null,
    val shareTo: ShareTo? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
)
class LandSummaryScreenModel(
    private val landRepository: CreateLandRepository,
    private val shareItemRepository: ShareItemRepository,
) : ScreenModel {

    private val _state = MutableStateFlow(LandSummaryState())
    val state = _state.asStateFlow()
    private val udf = UiStateHolder(_state.value)
    val uiState: StateFlow<UiState<LandSummaryState>> = udf.state
    val effects = udf.effects
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    private var submitJob: Job? = null
    fun initData(request: LandModel) {
        _state.update { it.copy(landRequest = request) }
        syncUdf()

    }
    fun initShareInfo(request: ShareTo) {
        _state.update { it.copy(shareTo = request) }
        syncUdf()

    }
    fun onAction(action: SummaryAction<LandModel>, onSuccess: () -> Unit = {}) {
        when (action) {
            is SummaryAction.Changed -> initData(action.value)
            is SummaryAction.ShareChanged -> initShareInfo(action.value)
            SummaryAction.Submit -> submitLand(onSuccess)
        }
    }
    private fun syncUdf() {
        udf.set(_state.value)
    }
    private fun asRequest(): LandRequest {
        val current = _state.value.landRequest

        // ✅ Map ข้อมูลให้มีทั้ง Byte, MimeType และ ชื่อไฟล์
        val allFiles = current?.attachments?.mapNotNull { attachment ->
            val bytes = attachment.platformData as? ByteArray ?: return@mapNotNull null

            // เช็กว่าเป็น PDF หรือ รูปภาพ
            val isPdf = attachment.name.endsWith(".pdf", ignoreCase = true) || attachment.type.toString().contains("PDF")
            val mimeType = if (isPdf) "application/pdf" else "image/jpeg"
            val extension = if (isPdf) "pdf" else "jpg"

            // ตั้งชื่อไฟล์ (เอา symbol มาต่อกับ index หรือเวลาเพื่อไม่ให้ซ้ำ)
            val fileName = "${current.landName}.$extension"

            LandFileUploadData(bytes = bytes, mimeType = mimeType, fileName = fileName)
        } ?: emptyList()

        val allRefIds = current?.referenceIds?.mapNotNull { ref ->
            LandReferenceData(areaName = ref.areaName, areaId = ref.areaId)
        } ?: emptyList()

        return LandRequest(
            name = current?.landName ?: "",
            area = current?.area ?: 0.0,
            amount = current?.amount ?: Money(0),

            // 🌟 ใช้ .takeIf { it.isNotBlank() } เพื่อถ้าเป็นค่าว่างให้เปลี่ยนเป็น null
            description = current?.description?.takeIf { it.isNotBlank() },
            locationAddress = current?.locationAddress?.takeIf { it.isNotBlank() },
            locationSubDistrict = current?.locationSubDistrict?.takeIf { it.isNotBlank() },
            locationDistrict = current?.locationDistrict?.takeIf { it.isNotBlank() },
            locationProvince = current?.locationProvince?.takeIf { it.isNotBlank() },
            locationPostalCode = current?.locationPostalCode?.takeIf { it.isNotBlank() },

            // เลขโฉนดก็อาจจะว่างได้ ถ้าไม่ได้บังคับให้กรอกก็ใส่ไว้ด้วยครับ
            deedNum = current?.deedNum?.takeIf { it.isNotBlank() },

            files = allFiles,
            referenceIds = allRefIds
        )
    }

    fun submitLand(onSuccess: () -> Unit) {
        if (_state.value.isLoading || submitJob?.isActive == true) return
        val shareToData = _state.value.shareTo ?: return

        _state.update { it.copy(isLoading = true) }
        udf.loading()
        val job = screenModelScope.launch {
            try {
                errorMessage = null

                // --- ขั้นตอนที่ 1: สร้าง Land ก่อน ---
                val requestBody = asRequest()
                val landResult = landRepository.create(requestBody)

                // ดึงข้อมูลออกมาจาก Result Wrapper
                val landResponse = landResult.getOrNull()

                if (landResult.isSuccess && landResponse != null) {
                    // ✅ ดึง ID ที่ได้จาก API ของการสร้าง Land
                    val createdItemId = landResponse.id.toString()

                    // --- ขั้นตอนที่ 2: เตรียมข้อมูลเพื่อ Share โดยใช้ ID ที่เพิ่งได้มา ---
                    val hasShareData = shareToData.email.isNotEmpty() ||
                            shareToData.friend.isNotEmpty() ||
                            shareToData.group.isNotEmpty()

                    if (hasShareData) {
                        val requestShareItem = ShareItems(
                            itemIds = createdItemId,
                            itemTypes = "land",

                            emails = shareToData.email.map { ShareTarget(id = it.userId, shareAt = it.apiDate) },
                            friends = shareToData.friend.map { ShareTarget(id = it.userId, shareAt = it.apiDate) },
                            groups = shareToData.group.map { ShareTarget(id = it.userId, shareAt = it.apiDate) }
                        )

                        // --- ขั้นตอนที่ 3: ยิง API แชร์ทรัพย์สิน ---
                        shareItemRepository.shareItem(requestShareItem)
                    }

                    // 🌟 ส่งสัญญาณกลับไปหน้า UI ให้เด้งกลับหน้าแรก
                    udf.success(_state.value)
                    udf.emit(FormEffect.Saved)
                    onSuccess()
                } else {
                    val failure = landResult.exceptionOrNull()
                        ?: IllegalStateException("Unable to create land asset")
                    errorMessage = failure.message
                    udf.failure(com.wealthvault.core.architecture.AppError.Unknown(failure))
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                errorMessage = e.message ?: "เกิดข้อผิดพลาดในการเชื่อมต่อ"
                udf.failure(com.wealthvault.core.architecture.AppError.Unknown(e))
            } finally {
                // 🌟 2. อัปเดต StateFlow ปิดปุ่มโหลด
                _state.update { it.copy(isLoading = false) }
                syncUdf()
            }
        }
        submitJob = job
        job.invokeOnCompletion { if (submitJob === job) submitJob = null }

    }







}

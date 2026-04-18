package com.wealthvault.financiallist.ui.debt

// 🌟 Import Data Class
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.financiallist.data.share.ShareTargetsRepositoryImpl
import com.wealthvault.financiallist.usecase.FinanciallistUseCase
import com.wealthvault.liability_api.model.GetLiabilityData
import com.wealthvault.liability_api.model.LiabilityIdData
import com.wealthvault.share_api.model.ItemShareTargetsResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DebtScreenModel(
    private val useCase: FinanciallistUseCase,
    private val shareTargetsRepository: ShareTargetsRepositoryImpl
) : ScreenModel {

    // 🌟 แยกเก็บ 2 หมวด
    private val _loans = MutableStateFlow<List<GetLiabilityData>>(emptyList())
    val loans = _loans.asStateFlow()

    private val _expenses = MutableStateFlow<List<GetLiabilityData>>(emptyList())
    val expenses = _expenses.asStateFlow()

    private val _shareTargets = MutableStateFlow<ItemShareTargetsResponse>(ItemShareTargetsResponse())
    val shareTargets = _shareTargets.asStateFlow()


    fun fetchLiabilities() {
        screenModelScope.launch {
            useCase.getLiabilities()
                .onSuccess { allLiabilities ->
                    // 🌟 ดักฟังข้อมูลดิบที่ได้มาก่อนจะทำการ Filter
                    println("✅ [API SUCCESS] Liabilities Data (ทั้งหมด): $allLiabilities")

                    // 🌟 Filter ตาม Type
                    _loans.value = allLiabilities.filter { it.type == "LIABILITY_TYPE_LOAN" }
                    _expenses.value = allLiabilities.filter { it.type == "LIABILITY_TYPE_EXPENSE" }

                    println("🔍 กรองแล้วได้หนี้สิน (_loans): ${_loans.value.size} รายการ")
                    println("🔍 กรองแล้วได้รายจ่าย (_expenses): ${_expenses.value.size} รายการ")
                }
                .onFailure {
                    println("❌ [API ERROR] Liabilities พังเพราะ: ${it.message}")
                }
        }
    }
    suspend fun getLiabilityById(id: String): LiabilityIdData? {
        return useCase.getLiabilityById(id)
            .onSuccess { println("✅ โหลด Liability สำเร็จ: ${it.name}") }
            .onFailure { println("🚨 โหลด Liability ล้มเหลว: ${it.message}") }
            .getOrNull()
    }
    fun deleteLiability(id: String, type: String) {
        screenModelScope.launch {
            // useCase ตัวเดียวกับหน้า Asset ได้เลยถ้า inject มาถูกตัว
            val result = useCase.deleteAsset(id, type)

            result.onSuccess {
                println("✅ ลบหนี้สินสำเร็จ!")
                fetchLiabilities() // 🌟 พอลบเสร็จ สั่งโหลดข้อมูลหน้านี้ใหม่ทันที
            }.onFailure { error ->
                println("🚨 ลบหนี้สินล้มเหลว: ${error.message}")
            }
        }
    }

    fun getShareTarget(id:String,type:String) {
        screenModelScope.launch {
            val shareTargetsResponse = async { shareTargetsRepository.shareTargets(id,type) }

            // รอรับผลลัพธ์จากทั้ง 2 API
            val shareTargetsResult = shareTargetsResponse.await()
            shareTargetsResult.onSuccess { data ->
                _shareTargets.value = data
                println("[Asset ScreenModel Fetch Share Target ] sucess")


            }.onFailure { error ->
                println("[Asset ScreenModel Fetch Share Target fail] ${error}")
            }

        } }
}

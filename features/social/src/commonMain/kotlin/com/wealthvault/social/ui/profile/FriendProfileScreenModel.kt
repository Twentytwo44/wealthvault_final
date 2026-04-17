package com.wealthvault.social.ui.profile

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.account_api.model.BankAccountData
import com.wealthvault.building_api.model.BuildingIdData
import com.wealthvault.cash_api.model.CashIdData
import com.wealthvault.insurance_api.model.InsuranceIdData
import com.wealthvault.investment_api.model.InvestmentIdData
import com.wealthvault.land_api.model.LandIdData
import com.wealthvault.liability_api.model.LiabilityIdData
import com.wealthvault.social.data.SocialRepositoryImpl
import com.wealthvault.`user-api`.model.FriendProfileData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FriendProfileScreenModel(
    private val repository: SocialRepositoryImpl
) : ScreenModel {

    private val _profileData = MutableStateFlow<FriendProfileData?>(null)
    val profileData: StateFlow<FriendProfileData?> = _profileData.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // 🌟 ใช้ State ตัวเดียวคุมความสำเร็จ ทั้งเพิ่มและลบเพื่อน
    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess.asStateFlow()


    // 🌟 ฟังก์ชันเพิ่มเพื่อน
    private val _isRemoveSuccess = MutableStateFlow(false)
    val isRemoveSuccess = _isRemoveSuccess.asStateFlow()

    private val _isAlreadySent = MutableStateFlow(false)
    val isAlreadySent: StateFlow<Boolean> = _isAlreadySent.asStateFlow()

    fun addFriend(targetId: String) {
        screenModelScope.launch {
            _isLoading.value = true
            _isSuccess.value = false
            _isAlreadySent.value = false // รีเซ็ตค่าก่อนเริ่ม

            repository.addFriend(targetId).onSuccess {
                _isSuccess.value = true
                println("✅ ส่งคำขอเพื่อนสำเร็จ")
            }.onFailure { error ->
                // 🌟 เช็คว่า Error Message มีคำว่า friend request already sent หรือไม่
                if (error.message?.contains("friend request already sent") == true) {
                    _isAlreadySent.value = true
                } else {
                    _isAlreadySent.value = false
                    println("🚨 เพิ่มเพื่อนไม่สำเร็จ: ${error.message}")
                }
            }
            _isLoading.value = false
        }
    }

    // --- ใน FriendProfileScreenModel.kt ---

    fun fetchProfile(friendId: String) {
        screenModelScope.launch {
            _isLoading.value = true

            // 🌟 รีเซ็ตสถานะทั้งหมดก่อนโหลดใหม่
            _isSuccess.value = false
            _isRemoveSuccess.value = false
            _isAlreadySent.value = false

            repository.getFriendProfile(friendId).onSuccess { data ->
                _profileData.value = data
            }.onFailure {
                _profileData.value = null
            }

            _isLoading.value = false
        }
    }

    fun removeFriend(targetId: String) {
        screenModelScope.launch {
            _isLoading.value = true
            repository.removeFriend(targetId).onSuccess {
                // 🌟 เมื่อ API ลบสำเร็จ ตัวแปรนี้จะไปสะกิด LaunchedEffect ใน Screen ให้รีโหลด
                _isRemoveSuccess.value = true
                println("✅ ลบเพื่อนสำเร็จ")
            }.onFailure {
                _isRemoveSuccess.value = false
                println("🚨 ลบเพื่อนล้มเหลว: ${it.message}")
            }
            _isLoading.value = false
        }
    }
    // --- ฟังก์ชันโหลดสินทรัพย์ต่างๆ คงเดิม ---
    suspend fun getAccountById(id: String): BankAccountData? {
        return repository.getAccountById(id).getOrNull()
    }

    suspend fun getBuildingById(id: String): BuildingIdData? {
        return repository.getBuildingById(id).getOrNull()
    }

    suspend fun getCashById(id: String): CashIdData? {
        return repository.getCashById(id).getOrNull()
    }

    suspend fun getInsuranceById(id: String): InsuranceIdData? {
        return repository.getInsuranceById(id).getOrNull()
    }

    suspend fun getInvestmentById(id: String): InvestmentIdData? {
        return repository.getInvestmentById(id).getOrNull()
    }

    suspend fun getLandById(id: String): LandIdData? {
        return repository.getLandById(id).getOrNull()
    }

    suspend fun getLiabilityById(id: String): LiabilityIdData? {
        return repository.getLiabilityById(id).getOrNull()
    }
}
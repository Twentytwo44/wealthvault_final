package com.wealthvault.profile.ui

import LineRepositoryImpl
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.`auth-api`.model.TokenRequest
import com.wealthvault.data_store.TokenStore
import com.wealthvault.notification_api.model.UnDeviceRequest
import com.wealthvault.profile.data.device.UnRegisterDeviceRepositoryImpl
import com.wealthvault_final.line_auth.LineAuth
import com.wealthvault_final.line_auth.model.LineUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull // 🌟 Import เพิ่มเติม

class MenuProfileSettingScreenModel(
    private val unRegisterDeviceRepository: UnRegisterDeviceRepositoryImpl,
    private val tokenStore: TokenStore,
    private val lineRepositoryImpl: LineRepositoryImpl
) : ScreenModel {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun onLineClick(lineAuth: LineAuth) {
        println("🚀 [LoginScreenModel] onLineClick triggered")
        lineAuth.login()
    }

    fun onLineSuccess(user: LineUser, onSuccess: () -> Unit) {
        println("🎉 [LoginScreenModel] LINE Success: ${user.displayName} (${user.userId})")

        screenModelScope.launch {
            _isLoading.value = true

            try {
                val request = TokenRequest(
                    token = user.idToken ?: ""
                )

                // 🌟 1. ใส่ Time-out 10 วินาที ถ้าเซิร์ฟเวอร์ค้าง แอปจะได้ไม่ค้างตาม
                val response = withTimeoutOrNull(10000) {
                    lineRepositoryImpl.link(request)
                }

                if (response != null && response.isSuccess) {
                    println("✅ เชื่อมต่อ LINE กับ Backend สำเร็จ!")
                    onSuccess()
                } else {
                    val errorMsg = response?.exceptionOrNull()?.message ?: "การเชื่อมต่อใช้เวลานานเกินไป (Timeout)"
                    println("❌ Backend แจ้งว่าไม่สำเร็จ: $errorMsg")
                }

            } catch (e: Exception) {
                println("❌ เกิดข้อผิดพลาดในการเชื่อมต่อ Backend: ${e.message}")
                e.printStackTrace()

            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onLineError(error: String) {
        println("❌ [LoginScreenModel] LINE Error: $error")
    }

    fun unRegisterDevice(onLogoutComplete: () -> Unit) {
        screenModelScope.launch {
            _isLoading.value = true

            try {
                // 🌟 2. ดึง Token แบบมี Time-out เผื่อ Flow ค้าง (รอเต็มที่ 2 วินาที)
                val tokenStr = withTimeoutOrNull(2000) {
                    tokenStore.fcmToken.firstOrNull()
                } ?: "" // ถ้าหาไม่ได้ หรือหมดเวลา ให้ใช้เป็น String ว่างแทน

                val request = UnDeviceRequest(token = tokenStr)

                // 🌟 3. ยิง API ลบเครื่อง แบบมี Time-out 10 วินาที
                withTimeoutOrNull(10000) {
                    unRegisterDeviceRepository.unDevice(request)
                }

                tokenStore.clear()
                println("✅ [UnregisterDevice] ยกเลิกการเชื่อมต่ออุปกรณ์สำเร็จ!")

            } catch (e: Exception) {
                println("❌ [UnregisterDevice] ยกเลิกอุปกรณ์ไม่สำเร็จ: ${e.message}")
            } finally {
                _isLoading.value = false
                onLogoutComplete()
            }
        }
    }
}
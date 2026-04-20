package com.wealthvault.profile.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.data_store.TokenStore
import com.wealthvault.notification_api.model.UnDeviceRequest
import com.wealthvault.profile.data.device.UnRegisterDeviceRepositoryImpl
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MenuProfileSettingScreenModel(
    private val unRegisterDeviceRepository: UnRegisterDeviceRepositoryImpl,
    private val tokenStore: TokenStore
) : ScreenModel {


    fun unRegisterDevice() {
        // 🚨 อัปเกรด 1: ครอบด้วย Coroutine Scope เพื่อให้ทำงานเบื้องหลัง (ไม่บล็อกหน้าจอ)
        screenModelScope.launch {
            try {
                // 💡 อัปเกรด 2: ถ้า Backend ต้องการ FCM Token ต้องเปลี่ยนไปดึง FCM Token แทนนะครับ
                // แต่ถ้า Backend ดึงจาก Access Token ได้เลย ก็ใช้บรรทัดนี้ตามเดิมครับ
                val token = tokenStore.fcmToken.firstOrNull() ?: ""
                println("fcmToken: ${token}")
                tokenStore.clear()

                val request = UnDeviceRequest(
                    token = token.toString()
                )

                // 🚨 อัปเกรด 3: เรียก Repository (จะรอจนกว่าจะทำเสร็จ)
                unRegisterDeviceRepository.unDevice(request)

                println("✅ [UnregisterDevice] ยกเลิกการเชื่อมต่ออุปกรณ์สำเร็จ!")

                // 🌟 ออปชันเสริม: เมื่อลบเสร็จแล้ว อาจจะสั่งให้แอป Logout หรือเคลียร์ข้อมูลในเครื่อง
                // tokenStore.clearToken()

            } catch (e: Exception) {
                // 🚨 อัปเกรด 4: ดักจับ Error ป้องกันแอปแครช
                println("❌ [UnregisterDevice] ยกเลิกอุปกรณ์ไม่สำเร็จ: ${e.message}")
            }
        }
    }


}

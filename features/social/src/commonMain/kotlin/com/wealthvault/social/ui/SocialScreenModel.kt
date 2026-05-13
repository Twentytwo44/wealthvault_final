package com.wealthvault.social.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.social.data.SocialRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SocialScreenModel(
    private val repository: SocialRepositoryImpl
) : ScreenModel {

    // 🌟 สร้าง State ไว้เก็บค่าว่ามีคำขอค้างอยู่ไหม
    private val _hasPendingRequest = MutableStateFlow(false)
    val hasPendingRequest = _hasPendingRequest.asStateFlow()

    fun fetchPendingFriendsBadge() {
        screenModelScope.launch {
            // 🌟 เรียกใช้ฟังก์ชันจาก Repository ที่ไปดึง API มา
            val result = repository.getPendingFriends()

            result.onSuccess { pendingList ->
                // ถ้าลิสต์ไม่ว่าง (isNotEmpty == true) แปลว่ามีคำขอค้างอยู่! โชว์จุดแดงเลย
                _hasPendingRequest.value = pendingList.isNotEmpty()
            }.onFailure {
                // ถ้าดึงข้อมูลพัง หรือล้มเหลว ก็ปิดจุดแดงไว้ก่อน
                _hasPendingRequest.value = false
            }
        }
    }
}
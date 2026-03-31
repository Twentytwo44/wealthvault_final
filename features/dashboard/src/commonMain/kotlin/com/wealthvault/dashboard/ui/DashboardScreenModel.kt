package com.wealthvault.dashboard.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.dashboard.data.DashboardRepositoryImpl
import com.wealthvault.`user-api`.model.DashboardDataResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardScreenModel(
    private val repository: DashboardRepositoryImpl
) : ScreenModel {

    // 🌟 เก็บข้อมูล Dashboard ที่ดึงมาได้
    private val _dashboardState = MutableStateFlow<DashboardDataResponse?>(null)
    val dashboardState = _dashboardState.asStateFlow()

    // 🌟 เก็บสถานะการโหลด
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        fetchDashboard()
    }

    fun fetchDashboard() {
        screenModelScope.launch {
            _isLoading.value = true
            repository.getDashboardData()
                .onSuccess { data ->
                    _dashboardState.value = data
                }
                .onFailure { error ->
                    println("🚨 Dashboard Error: ${error.message}")
                }
            _isLoading.value = false
        }
    }
}
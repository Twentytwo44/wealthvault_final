package com.wealthvault.di // 🌟 เปลี่ยน package ให้ตรงกับของคุณ

import com.wealthvault.dashboard.data.DashboardDataSource
import com.wealthvault.dashboard.data.DashboardRepositoryImpl
import com.wealthvault.dashboard.ui.DashboardScreenModel
import org.koin.dsl.module

val dashboardModule = module {
    single { DashboardDataSource(dashboardApi = get()) }
    single { DashboardRepositoryImpl(networkDataSource = get()) }

    // 🌟 เพิ่ม get() ตัวที่สองเข้าไป เพื่อให้มันไปดึง NotificationUseCase มาใส่ให้อัตโนมัติ
    factory { DashboardScreenModel(repository = get(), notificationUseCase = get()) }
}
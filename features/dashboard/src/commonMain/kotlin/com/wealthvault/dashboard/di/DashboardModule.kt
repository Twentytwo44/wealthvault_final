package com.wealthvault.di // 🌟 เปลี่ยน package ให้ตรงกับที่เก็บไฟล์ Module ของคุณ Champ นะครับ

import com.wealthvault.dashboard.data.DashboardDataSource
import com.wealthvault.dashboard.data.DashboardRepositoryImpl
import com.wealthvault.dashboard.ui.DashboardScreenModel
import com.wealthvault.`user-api`.dashboard.DashboardApi
import com.wealthvault.`user-api`.dashboard.DashboardApiImpl
import org.koin.dsl.module

val dashboardModule = module {
    single { DashboardDataSource(dashboardApi = get()) }
    single { DashboardRepositoryImpl(networkDataSource = get()) }
    factory { DashboardScreenModel(repository = get()) }
}
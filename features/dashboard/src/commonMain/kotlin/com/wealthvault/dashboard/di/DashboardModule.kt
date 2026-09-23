package com.wealthvault.di // 🌟 เปลี่ยน package ให้ตรงกับของคุณ

import com.wealthvault.dashboard.ui.DashboardScreenModel
import org.koin.dsl.module

val dashboardModule = module {
    factory {
        DashboardScreenModel(
            repository = get(),
            notificationBadgeProvider = get(),
        )
    }
}

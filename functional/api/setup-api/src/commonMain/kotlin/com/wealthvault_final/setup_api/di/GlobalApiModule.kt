package com.wealthvault_final.setup_api.di

import com.wealthvault.config.Config
import com.wealthvault.core.KoinConst
import com.wealthvault_final.setup_api.HttpClientBuilder
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module

object GlobalApiModule {
    val allModules = module {

        // --- 1. Global HttpClient ---
        single<HttpClient>(named(KoinConst.HttpClient.GLOBAL)) {
            // มั่นใจว่า HttpClientBuilder รับ (Json, TokenStore?)
            HttpClientBuilder(
                json = get(named(KoinConst.KotlinSerialization.GLOBAL)),
                tokenStore = get() // ดึง TokenStore (ที่ปกติลงทะเบียนแบบไม่มี named)
            ).build(withAuth = true) // เรียกใช้ฟังก์ชัน build ที่เราแก้ชื่อใหม่
        }

        // --- 2. Global Ktorfit ---
        single<Ktorfit>(named(KoinConst.Ktor.GLOBAL)) {

            // 🌟 เช็คและเติมเครื่องหมาย / ต่อท้ายให้อัตโนมัติ (ถ้ายังไม่มี)
//            val safeBaseUrl = if (Config.localhost_android.endsWith("/")) {
//                Config.localhost_android
//            } else {
//                "${Config.localhost_android}/"
//            }

            Ktorfit.Builder()
                .baseUrl(Config.localhost_android) // 🌟 ใช้ URL ที่ปลอดภัยแล้ว
                // ต้องระบุ named ให้ชัดเจนตอน get HttpClient
                .httpClient(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL)))
                .build()
        }
    }
}
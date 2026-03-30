//package com.wealthvault.user_api.closefriend
//
//import com.wealthvault.config.Config // 🌟 นำเข้า Config (ถ้ามี)
//import com.wealthvault.`user-api`.closefriend.CloseFriendApi
//import com.wealthvault.`user-api`.model.CloseFriendResponse
//import de.jensklingenberg.ktorfit.Ktorfit
//import io.ktor.client.call.body
//import io.ktor.client.request.get
//// 🌟 อย่าลืม import UserDataResponse ด้วยนะครับ
//
//class UpdateCloseFriendApiImpl(private val ktorfit: Ktorfit) : CloseFriendApi {
//    override suspend fun getCloseFriend(): CloseFriendResponse {
//        val client = ktorfit.httpClient
//
//        // 🌟 เปลี่ยน URL ให้ตรงกับที่ Backend ของคุณ Champ ตั้งไว้
//        return client.get("${Config.localhost_android}closefriend") {
//            // ถ้าต้องแนบ Token ก็ใส่ headers ตรงนี้ครับ
//        }.body()
//    }
//}
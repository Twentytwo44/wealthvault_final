//package com.wealthvault.login

//
//class LoginTest {
//
//@Test
//fun testLoginIntegration() = runTest {
//        val ktorfit = Ktorfit.Builder()
//            .baseUrl("http://10.0.2.2:8080/")
//            .build()
//
//
//        val dataStore = DataStoreBuilder().buildDefaultDataStore()
//
//        // 2. สร้าง TokenStore (Local DataSource)
//        // โดยส่ง dataStore ที่สร้างเสร็จแล้วเข้าไป
//        val tokenStore = TokenStore(dataStore)
//
//        // 3. เตรียมระบบเน็ตเวิร์ก (API จริง)
//       val loginApi: LoginApi = LoginApiImpl(ktorfit)
//        val networkDataSource = AuthNetworkDataSource(loginApi)
//
//        // 4. สร้าง Repository เพื่อทดสอบ Logic การเชื่อมต่อ
//        val repository = AuthRepositoryImpl(
//            networkDataSource = networkDataSource,
//            localDataSource = tokenStore
//        )
//
//        // --- เริ่มการทดสอบจริง ---
//        println("🚀 Attempting to login...")
//        val loginResult = repository.login(LoginRequest("test_user", "password123"))
//
//        loginResult.onSuccess {
//            println("✅ Login API Success!")
//
//            // เช็คว่า Flow พ่นค่าออกมาเป็น true หรือไม่ (หลังจาก saveToken)
//            val isAuthenticated = repository.observeAuthState().first()
//            println("🔑 Is Authenticated in DataStore: $isAuthenticated")
//
//            if (isAuthenticated) {
//                println("🎉 Success: Token is physically stored on your Mac!")
//            }
//        }.onFailure {
//            println("❌ Login Failed: ${it.message}")
//        }
//    }
//}

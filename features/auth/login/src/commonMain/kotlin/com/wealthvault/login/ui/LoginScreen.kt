package com.wealthvault.login.ui

//
//class LoginScreen : Screen {
//
//    @Composable
//    override fun Content() {
//        // 1. ดึง ViewModel มาใช้ (Koin จะจัดการ Injection ให้เอง)
//        val viewModel = koinInject<LoginViewModel>()
//
//        // 2. ดึง State ปัจจุบันมาวาดหน้าจอ
//        val state = viewModel.state
//
//        // 3. State สำหรับปุ่มรูปตา (เปิด/ปิด รหัสผ่าน) - เฉพาะ UI ไม่ต้องเก็บใน ViewModel ก็ได้
//        var isPasswordVisible by remember { mutableStateOf(false) }
//
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(24.dp), // เว้นขอบสวยๆ
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text(
//                text = "Welcome Back",
//                style = MaterialTheme.typography.headlineMedium,
//                color = MaterialTheme.colorScheme.primary
//            )
//
//            Spacer(modifier = Modifier.height(32.dp))
//
//            // --- ช่องกรอก Username ---
//            OutlinedTextField(
//                value = state.username,
//                onValueChange = { viewModel.onUsernameChange(it) }, // ส่งค่าไปอัปเดต
//                label = { Text("Username") },
//                singleLine = true,
//                modifier = Modifier.fillMaxWidth(),
//                enabled = !state.isLoading // ห้ามพิมพ์ตอนโหลด
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // --- ช่องกรอก Password (มีปุ่มตา) ---
//            OutlinedTextField(
//                value = state.password,
//                onValueChange = { viewModel.onPasswordChange(it) },
//                label = { Text("Password") },
//                singleLine = true,
//                modifier = Modifier.fillMaxWidth(),
//                enabled = !state.isLoading,
//                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
//                trailingIcon = {
//                    val image = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
//                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
//                        Icon(imageVector = image, contentDescription = "Toggle Password")
//
//                    }
//                }
//            )
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            // --- ส่วนแสดงผล Error / Success ---
//            if (state.error != null) {
//                Text(
//                    text = state.error,
//                    color = MaterialTheme.colorScheme.error,
//                    style = MaterialTheme.typography.bodyMedium
//                )
//                Spacer(modifier = Modifier.height(16.dp))
//            }
//
//            if (state.isLoggedIn) {
//                Text(
//                    text = "Login Successful! 🎉",
//                    color = Color(0xFF4CAF50), // สีเขียว
//                    style = MaterialTheme.typography.titleMedium
//                )
//                // TODO: ตรงนี้สั่ง navigator.push(HomeScreen()) ได้เลย
//                Spacer(modifier = Modifier.height(16.dp))
//            }
//
//            // --- ปุ่ม Login ---
//            Button(
//                onClick = { viewModel.login() },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(50.dp),
//                enabled = !state.isLoading // ปิดปุ่มตอนโหลด กันกดเบิ้ล
//            ) {
//                if (state.isLoading) {
//                    CircularProgressIndicator(
//                        color = Color.White,
//                        modifier = Modifier.size(24.dp),
//                        strokeWidth = 2.dp
//                    )
//                } else {
//                    Text("LOGIN")
//                }
//            }
//            Box(
//                modifier = Modifier.fillMaxSize(),
//                contentAlignment = Alignment.BottomCenter
//            ) {
////                GoogleButtonUiContainer(
////                    onGoogleSignInResult = { googleUser ->
////                        val tokenId = googleUser?.
////                        println("Google ID Token: $tokenId")
////                    }
////                )  {
////                    GoogleSignInButton {
////                        onClick( )
////                    }
////                }
//            }
//        }
//    }
//}

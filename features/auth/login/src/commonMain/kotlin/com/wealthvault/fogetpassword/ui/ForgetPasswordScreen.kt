package com.wealthvault.fogetpassword.ui

//
//class LoginScreen : Screen {
//    @Composable
//    override fun Content() {
//        val navigator = LocalNavigator.currentOrThrow
//        val screenModel = getScreenModel<LoginScreenModel>()
//
//        // เรียกใช้ Stateless UI ที่เราแยกไว้
//        LoginContent(
//            username = screenModel.username,
//            onUsernameChange = { screenModel.username = it },
//            password = screenModel.password,
//            onPasswordChange = { screenModel.password = it },
//            isLoading = screenModel.isLoading,
//            onLoginClick = {
//                screenModel.onLoginClick {
//                    // จัดการการเปลี่ยนหน้าตรงนี้
//                }
//            }
//        )
//    }
//}
//
//@Composable
//fun LoginContent(
//    username: String,
//    onUsernameChange: (String) -> Unit,
//    password: String,
//    onPasswordChange: (String) -> Unit,
//    isLoading: Boolean,
//    onLoginClick: () -> Unit
//) {
//    val primaryColor = Color(0xFFC47B5D) // สีน้ำตาลอมส้ม
//    val inputBgColor = Color.White
//    val borderColor = primaryColor.copy(alpha = 0.3f)
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(
//                brush = Brush.verticalGradient(
//                    colors = listOf(
//                        Color(0xFFFFF8F3),
//                        Color(0xFFFFF0E5)
//                    )
//                )
//            )
//            .padding(24.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Text(
//            text = "Wealth & Vault",
//            color = primaryColor,
//            fontSize = 28.sp,
//            modifier = Modifier.padding(horizontal = 8.dp)
//        )
//
//        Spacer(modifier = Modifier.height(200.dp))
//
//        Column(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            // 1. กล่อง Input 2 ช่อง
//            // --- ช่องอีเมล ---
//            Column(modifier = Modifier.fillMaxWidth()) {
//                Text(
//                    text = "อีเมล",
//                    color = primaryColor,
//                    fontSize = 18.sp,
//                    modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
//                )
//                OutlinedTextField(
//                    value = username,
//                    onValueChange = onUsernameChange,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(56.dp),
//                    shape = RoundedCornerShape(percent = 30), // ขอบโค้งมนแบบในดีไซน์
//                    singleLine = true,
//                    colors = OutlinedTextFieldDefaults.colors(
//                        focusedContainerColor = inputBgColor,
//                        unfocusedContainerColor = inputBgColor,
//                        focusedBorderColor = primaryColor,
//                        unfocusedBorderColor = borderColor,
//                    )
//                )
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // --- ช่องรหัสผ่าน ---
//            Column(modifier = Modifier.fillMaxWidth()) {
//                Text(
//                    text = "รหัสผ่าน",
//                    color = primaryColor,
//                    fontSize = 18.sp,
//                    modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
//                )
//                OutlinedTextField(
//                    value = password,
//                    onValueChange = onPasswordChange,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(56.dp),
//                    shape = RoundedCornerShape(percent = 30),
//                    singleLine = true,
//                    visualTransformation = PasswordVisualTransformation(),
//                    colors = OutlinedTextFieldDefaults.colors(
//                        focusedContainerColor = inputBgColor,
//                        unfocusedContainerColor = inputBgColor,
//                        focusedBorderColor = primaryColor,
//                        unfocusedBorderColor = borderColor,
//                    )
//                )
//            }
//
//            // 2. ปุ่มลืมรหัสผ่าน (ดันไปชิดขวา)
//            Text(
//                text = "ลืมรหัสผ่าน",
//                color = primaryColor.copy(alpha = 0.8f),
//                fontSize = 16.sp,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(top = 12.dp, end = 16.dp)
//                    .clickable { /* TODO: นำทางไปหน้าลืมรหัส */ },
//                textAlign = TextAlign.End
//            )
//
//            Spacer(modifier = Modifier.height(32.dp))
//
//            // 3. ปุ่มเข้าสู่ระบบ
//            Button(
//                onClick = onLoginClick,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(56.dp),
//                shape = RoundedCornerShape(percent = 30),
//                colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
//            ) {
//                Text("เข้าสู่ระบบ", fontSize = 18.sp, color = Color.White)
//            }
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            // 4. ยังไม่มีบัญชี สร้างบัญชี?
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.Center,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(text = "ยังไม่มีบัญชี ", color = primaryColor.copy(alpha = 0.8f), fontSize = 14.sp)
//                Text(
//                    text = "สร้างบัญชี?",
//                    color = primaryColor,
//                    fontSize = 14.sp,
//                    textDecoration = TextDecoration.Underline,
//                    modifier = Modifier.clickable { /* TODO: นำทางไปหน้าสมัคร */ }
//                )
//            }
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            // 5. เส้นคั่น "หรือ"
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
//            ) {
//                HorizontalDivider(modifier = Modifier.weight(1f),
//                    color = borderColor,
//                    thickness = 2.dp)
//                Text(
//                    text = " หรือ ",
//                    color = primaryColor.copy(alpha = 0.8f),
//                    fontSize = 14.sp,
//                    modifier = Modifier.padding(horizontal = 8.dp)
//                )
//                HorizontalDivider(modifier = Modifier.weight(1f),
//                    color = borderColor,
//                    thickness = 2.dp)
//            }
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            // 6. ปุ่ม Google (ปุ่มโปร่งใสขอบสีส้ม)
//            OutlinedButton(
//                onClick = { /* TODO: Google Login */ },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(56.dp)
//                    .padding(horizontal = 48.dp), // บีบให้ปุ่มแคบลงนิดนึงตามดีไซน์
//                shape = RoundedCornerShape(percent = 30),
//                border = BorderStroke(1.dp, borderColor),
//                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
//            ) {
//                Text("Google", color = primaryColor, fontSize = 16.sp)
//            }
//
//        }
//    }
//}

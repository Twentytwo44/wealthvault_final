package com.example.login.ui



//class LoginViewModel(private val loginUseCase: LoginUseCase) : ScreenModel {
//
//    // ✅ เรียกใช้ LoginState ได้เลย (เพราะอยู่ใน package เดียวกัน)
//    var state by mutableStateOf(LoginState())
//        private set
//
//    fun onUsernameChange(input: String) {
//        state = state.copy(username = input)
//    }
//
//    fun onPasswordChange(input: String) {
//        state = state.copy(password = input)
//    }
//
//    fun login() {
//        screenModelScope.launch {
//            state = state.copy(isLoading = true, error = null)
//
//            val result = loginUseCase(state.username, state.password)
//
//            result.onSuccess {
//                state = state.copy(
//                    isLoading = false,
//                    isLoggedIn = true,
//                    error = null
//                )
//            }.onFailure { e ->
//                state = state.copy(
//                    isLoading = false,
//                    error = e.message ?: "Unknown Error"
//                )
//            }
//        }
//    }
//}
//

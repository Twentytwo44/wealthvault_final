package com.wealthvault.`auth-api`.di

import com.wealthvault.`auth-api`.fgpassword.ForgetApi
import com.wealthvault.`auth-api`.fgpassword.ForgetApiImpl
import com.wealthvault.`auth-api`.googlelink.GoogleLoginApi
import com.wealthvault.`auth-api`.googlelink.GoogleLoginApiImpl
import com.wealthvault.`auth-api`.linelink.LineLinkApi
import com.wealthvault.`auth-api`.linelink.LineLinkApiImpl
import com.wealthvault.`auth-api`.login.LoginApi
import com.wealthvault.`auth-api`.login.LoginApiImpl
import com.wealthvault.`auth-api`.otp.OTPApi
import com.wealthvault.`auth-api`.otp.OTPApiImpl
import com.wealthvault.`auth-api`.refreshtoken.RefreshTokenApi
import com.wealthvault.`auth-api`.refreshtoken.RefreshTokenImpl
import com.wealthvault.`auth-api`.register.RegisterApi
import com.wealthvault.`auth-api`.register.RegisterApiImpl
import com.wealthvault.`auth-api`.rspassword.ResetApi
import com.wealthvault.`auth-api`.rspassword.ResetApiImpl
import com.wealthvault.core.KoinConst
import org.koin.core.qualifier.named
import org.koin.dsl.module

object ApiModule {
    val allModules = module {
        single<LoginApi> { LoginApiImpl(get(named(KoinConst.HttpClient.PUBLIC))) }
        single<RegisterApi> { RegisterApiImpl(get(named(KoinConst.HttpClient.PUBLIC))) }
        single<RefreshTokenApi> { RefreshTokenImpl(get(named(KoinConst.HttpClient.PUBLIC))) }
        single<ForgetApi> { ForgetApiImpl(get(named(KoinConst.HttpClient.PUBLIC))) }
        single<ResetApi> { ResetApiImpl(get(named(KoinConst.HttpClient.PUBLIC))) }
        single<OTPApi> { OTPApiImpl(get(named(KoinConst.HttpClient.PUBLIC))) }
        single<LineLinkApi> { LineLinkApiImpl(get(named(KoinConst.HttpClient.PUBLIC))) }
        single<GoogleLoginApi> { GoogleLoginApiImpl(get(named(KoinConst.HttpClient.PUBLIC))) }
    }


}

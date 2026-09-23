package com.wealthvault.data.auth.transport.di

import com.wealthvault.data.auth.transport.fgpassword.ForgetApi
import com.wealthvault.data.auth.transport.fgpassword.ForgetApiImpl
import com.wealthvault.data.auth.transport.googlelink.GoogleLoginApi
import com.wealthvault.data.auth.transport.googlelink.GoogleLoginApiImpl
import com.wealthvault.data.auth.transport.linelink.LineLinkApi
import com.wealthvault.data.auth.transport.linelink.LineLinkApiImpl
import com.wealthvault.data.auth.transport.login.LoginApi
import com.wealthvault.data.auth.transport.login.LoginApiImpl
import com.wealthvault.data.auth.transport.otp.OTPApi
import com.wealthvault.data.auth.transport.otp.OTPApiImpl
import com.wealthvault.data.auth.transport.refreshtoken.RefreshTokenApi
import com.wealthvault.data.auth.transport.refreshtoken.RefreshTokenImpl
import com.wealthvault.data.auth.transport.register.RegisterApi
import com.wealthvault.data.auth.transport.register.RegisterApiImpl
import com.wealthvault.data.auth.transport.registerdevice.RegisterDeviceApi
import com.wealthvault.data.auth.transport.registerdevice.RegisterDeviceApiImpl
import com.wealthvault.data.auth.transport.rspassword.ResetApi
import com.wealthvault.data.auth.transport.rspassword.ResetApiImpl
import com.wealthvault.data.auth.transport.unregisterdevice.UnregisterDeviceApi
import com.wealthvault.data.auth.transport.unregisterdevice.UnregisterDeviceApiImpl
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
        single<RegisterDeviceApi> { RegisterDeviceApiImpl(get(named(KoinConst.HttpClient.GLOBAL))) }
        single<UnregisterDeviceApi> { UnregisterDeviceApiImpl(get(named(KoinConst.HttpClient.GLOBAL))) }
    }


}

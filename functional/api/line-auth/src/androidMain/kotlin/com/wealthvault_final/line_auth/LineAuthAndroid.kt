package com.wealthvault_final.line_auth

import android.content.Context
import android.content.Intent
import com.linecorp.linesdk.Scope
import com.linecorp.linesdk.auth.LineAuthenticationParams
import com.linecorp.linesdk.auth.LineLoginApi

class LineAuthAndroid(
    private val context: Context,
    private val launchIntent: (Intent) -> Unit // รับฟังก์ชันสำหรับยิง Intent
) : LineAuth {
    // ⚠️ อย่าลืมใส่ Channel ID ของคุณ
    private val channelId = "2009343103"

    override fun login() {
        val params = LineAuthenticationParams.Builder()
            .scopes(listOf(Scope.PROFILE, Scope.OPENID_CONNECT))
            .botPrompt(LineAuthenticationParams.BotPrompt.aggressive)
            .build()

        val intent = LineLoginApi.getLoginIntent(context, channelId, params)
        launchIntent(intent) // สั่งให้หน้าจอ Android เด้ง Intent ไปหาแอป LINE
    }
}

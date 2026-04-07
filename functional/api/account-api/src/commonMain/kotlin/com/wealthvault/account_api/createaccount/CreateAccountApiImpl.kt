package com.wealthvault.account_api.createaccount

import com.wealthvault.account_api.model.BankAccountRequest
import com.wealthvault.account_api.model.BankAccountResponse
import com.wealthvault.config.Config
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

class CreateAccountApiImpl(private val ktorfit: Ktorfit) : CreateAccountApi {
    override suspend fun create(request: BankAccountRequest): BankAccountResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.post("${Config.localhost_android}asset/account/") {
            setBody(
                MultiPartFormDataContent(
                    formData {

                        append("name", request.name)
                        append("description", request.description)
                        append("bank_name", request.bankName)
                        append("bank_account", request.bankAccount)
                        append("type", request.type)
                        append("amount", request.amount)


                        request.files.forEach { fileData ->
                            append("files", fileData.bytes, Headers.build {

                                // ✅ 1. ใส่ ContentType ตามชนิดไฟล์จริงๆ (image/jpeg หรือ application/pdf)
                                append(HttpHeaders.ContentType, fileData.mimeType)

                                // ✅ 2. ใส่ชื่อไฟล์ที่มีนามสกุลถูกต้อง (.jpg หรือ .pdf)
                                append(HttpHeaders.ContentDisposition, "filename=\"${fileData.fileName}\"")

                            })
                        }


                    }
                )
            )
        }.body()
    }
}

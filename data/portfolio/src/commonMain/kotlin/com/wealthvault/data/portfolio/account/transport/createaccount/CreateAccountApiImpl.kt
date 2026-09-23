package com.wealthvault.data.portfolio.account.transport.createaccount

import com.wealthvault.data.portfolio.account.transport.requireDomainData
import com.wealthvault.data.portfolio.account.transport.toWire
import com.wealthvault.config.Config
import com.wealthvault.domain.portfolio.BankAccountRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

class CreateAccountApiImpl(private val client: HttpClient) : CreateAccountApi {
    override suspend fun create(request: BankAccountRequest) = client.post("${Config.localhost_android}asset/account/") {
            val wireRequest = request.toWire()
            setBody(
                MultiPartFormDataContent(
                    formData {

                        append("name", wireRequest.name)
                        append("description", wireRequest.description)
                        append("bank_name", wireRequest.bankName)
                        append("bank_account", wireRequest.bankAccount)
                        append("type", wireRequest.type)
                        append("amount", wireRequest.amount)


                        wireRequest.files.forEach { fileData ->
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
        }.body<com.wealthvault.data.portfolio.account.transport.model.BankAccountResponse>().requireDomainData()
}

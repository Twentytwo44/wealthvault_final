package com.wealthvault.cash_api.createcash

import com.wealthvault.cash_api.requireDomainData
import com.wealthvault.cash_api.toWire
import com.wealthvault.config.Config
import com.wealthvault.domain.portfolio.CashRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

class CreateCashApiImpl(private val client: HttpClient) : CreateCashApi {
    override suspend fun create(request: CashRequest) = client.post("${Config.localhost_android}asset/cash/") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        // ส่งข้อมูลที่เป็น Text/String จาก request object

                        val wireRequest = request.toWire()
                        append("name", wireRequest.name ?: "")
                        append("description", wireRequest.description ?: "")
                        append("amount", wireRequest.amount.toString())

                        wireRequest.files?.forEach { fileData ->
                            append("files", fileData.bytes ?: byteArrayOf(), Headers.build {

                                // ✅ 1. ใส่ ContentType ตามชนิดไฟล์จริงๆ (image/jpeg หรือ application/pdf)
                                append(HttpHeaders.ContentType, fileData.mimeType?: "")

                                // ✅ 2. ใส่ชื่อไฟล์ที่มีนามสกุลถูกต้อง (.jpg หรือ .pdf)
                                append(HttpHeaders.ContentDisposition, "filename=\"${fileData.fileName}\"")

                            })
                        }


                    }
                )
            )
        }.body<com.wealthvault.cash_api.model.CashResponse>().requireDomainData()
}

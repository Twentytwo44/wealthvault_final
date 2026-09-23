package com.wealthvault.data.portfolio.cash.transport.updatecash

import com.wealthvault.data.portfolio.cash.transport.requireDomainData
import com.wealthvault.data.portfolio.cash.transport.toWire
import com.wealthvault.config.Config
import com.wealthvault.domain.portfolio.CashRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

class UpdateCashApiImpl(private val client: HttpClient) : UpdateCashApi {
    override suspend fun updateCash(id: String, request: CashRequest) = client.patch("${Config.localhost_android}asset/cash/$id/") {
            val wireRequest = request.toWire()
            setBody(
                MultiPartFormDataContent(
                    formData {
                        // ส่งข้อมูลที่เป็น Text/String จาก request object

                        append("name", wireRequest.name ?: "")
                        append("description", wireRequest.description ?: "")
                        append("amount", wireRequest.amount.toString())

                        wireRequest.deleteListId?.forEach { fileData ->
                            append("delete_file_ids", fileData)
                        }

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
        }.body<com.wealthvault.data.portfolio.cash.transport.model.CashResponse>().requireDomainData()
}

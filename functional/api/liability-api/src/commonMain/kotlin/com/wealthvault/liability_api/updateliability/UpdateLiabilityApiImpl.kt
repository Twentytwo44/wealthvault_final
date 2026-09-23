package com.wealthvault.liability_api.updateliability


import com.wealthvault.config.Config
import com.wealthvault.domain.portfolio.LiabilityRequest
import com.wealthvault.liability_api.requireDomainData
import com.wealthvault.liability_api.toWire
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

class UpdateLiabilityApiImpl(private val client: HttpClient) : UpdateLiabilityApi {
    override suspend fun updateLiability(id: String, request: LiabilityRequest) = client.patch("${Config.localhost_android}lia/$id") {
            val wireRequest = request.toWire()
            setBody(
                MultiPartFormDataContent(
                    formData {

                        append("name", wireRequest.name ?: "")
                        append("type", wireRequest.type ?: "")
                        append("creditor", wireRequest.creditor ?: "")
                        append("principal", wireRequest.principal ?: 0.0)
                        append("interest_rate", wireRequest.interestRate ?: "")
                        append("description", wireRequest.description ?: "")
                        append("started_at", wireRequest.startedAt ?: "")
                        append("ended_at", wireRequest.endedAt ?: "")

                        wireRequest.deleteListId.forEach { fileData ->
                            append("delete_file_ids", fileData)
                        }

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
        }.body<com.wealthvault.liability_api.model.LiabilityResponse>().requireDomainData()
}

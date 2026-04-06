package com.wealthvault.investment_api.createcash



import com.wealthvault.config.Config
import com.wealthvault.liability_api.model.LiabilityRequest
import com.wealthvault.liability_api.model.LiabilityResponse
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

class CreateLiabilityApiImpl(private val ktorfit: Ktorfit) : CreateLiabilityApi {
    override suspend fun create(request: LiabilityRequest): LiabilityResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.post("${Config.localhost_android}asset/lia/") {
            setBody(
                MultiPartFormDataContent(
                    formData {

                        append("name", request.name ?: "")
                        append("type", request.type ?: "")
                        append("creditor", request.creditor ?: "")
                        append("principal", request.principal ?: 0.0)
                        append("interest_rate", request.interestRate ?: "")
                        append("description", request.description ?: "")
                        append("started_at", request.startedAt ?: "")
                        append("ended_at", request.endedAt ?: "")

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

package com.wealthvault.investment_api.updateinvestment


import com.wealthvault.config.Config
import com.wealthvault.investment_api.model.InvestmentRequest
import com.wealthvault.investment_api.model.InvestmentResponse
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

class UpdateInvestmentApiImpl(private val ktorfit: Ktorfit) : UpdateInvestmentApi {
    override suspend fun updateInvestment(id: String, request: InvestmentRequest): InvestmentResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.patch("${Config.localhost_android}asset/invest/${id}") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        // ส่งข้อมูลที่เป็น Text/String จาก request object

                        append("name", request.name ?: "")
                        append("description", request.description ?: "")
                        append("symbol", request.symbol ?: "")
                        append("broker_name", request.brokerName ?: "")
                        append("quantity", request.quantity ?: "")
                        append("cost_per_price", request.costPerPrice ?: "")

                        append("type", request.type ?: "")
                        append("amount", request.quantity ?: "")
                        append("files", request.symbol ?: "")

                        request.deleteListId.forEach { fileData ->
                            append("delete_file_ids", fileData)
                        }

                        request.files.forEach { fileData ->
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
        }.body()
    }
}

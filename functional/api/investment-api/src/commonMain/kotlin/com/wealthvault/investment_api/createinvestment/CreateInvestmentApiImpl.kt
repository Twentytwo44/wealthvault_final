package com.wealthvault.investment_api.createinvestment


import com.wealthvault.config.Config
import com.wealthvault.domain.portfolio.InvestmentData
import com.wealthvault.domain.portfolio.InvestmentRequest
import com.wealthvault.investment_api.requireDomainData
import com.wealthvault.investment_api.toWire
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

class CreateInvestmentApiImpl(private val client: HttpClient) : CreateInvestmentApi {
    override suspend fun create(request: InvestmentRequest): InvestmentData {
        return client.post("${Config.localhost_android}asset/invest/") {
            val wireRequest = request.toWire()

            setBody(
                MultiPartFormDataContent(
                    formData {
                        // ส่งข้อมูลที่เป็น Text/String จาก request object

                        append("name", wireRequest.name ?: "")
                        append("description", wireRequest.description ?: "")
                        append("symbol", wireRequest.symbol ?: "")
                        append("broker_name", wireRequest.brokerName ?: "")
                        append("quantity", wireRequest.quantity ?: "")
                        append("cost_per_price", wireRequest.costPerPrice ?: "")

                        append("type", wireRequest.type ?: "")
                        append("amount", wireRequest.quantity ?: "")

                        wireRequest.files.forEach { fileData ->
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
        }.body<com.wealthvault.investment_api.model.InvestmentResponse>().requireDomainData()
    }
}

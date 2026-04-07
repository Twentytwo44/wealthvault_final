package com.wealthvault.insurance_api.createcash


import com.wealthvault.config.Config
import com.wealthvault.insurance_api.model.InsuranceRequest
import com.wealthvault.insurance_api.model.InsuranceResponse
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

class CreateInsuranceApiImpl(private val ktorfit: Ktorfit) : CreateInsuranceApi {
    override suspend fun create(request: InsuranceRequest): InsuranceResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.post("${Config.localhost_android}asset/insurance/") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("name", request.name?: "")
                        append("description", request.description?: "")
                        append("policy_number", request.policyNumber?: "")
                        append("type", request.type?: "")
                        append("company_name", request.companyName?: "")
                        append("coverage_period", request.coveragePeriod?: "")
                        append("coverage_amount", request.coverageAmount.toString())
                        append("con_date", request.conDate?: "")
                        append("exp_date", request.expDate?: "")


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

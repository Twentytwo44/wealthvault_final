package com.wealthvault.data.portfolio.insurance.transport.updateinsurance

import com.wealthvault.config.Config
import com.wealthvault.domain.portfolio.InsuranceRequest
import com.wealthvault.data.portfolio.insurance.transport.requireDomainData
import com.wealthvault.data.portfolio.insurance.transport.toWire
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

class UpdateInsuranceApiImpl(private val client: HttpClient) : UpdateInsuranceApi {
    override suspend fun updateInsurance(id: String, request: InsuranceRequest) = client.patch("${Config.localhost_android}asset/insurance/$id/") {
            val wireRequest = request.toWire()
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("name", wireRequest.name ?: "")
                        append("description", wireRequest.description ?: "")
                        append("policy_number", wireRequest.policyNumber ?: "")
                        append("type", wireRequest.type ?: "")
                        append("company_name", wireRequest.companyName ?: "")
                        append("coverage_period", wireRequest.coveragePeriod ?: "")
                        append("coverage_amount", wireRequest.coverageAmount.toString())
                        append("con_date", wireRequest.conDate ?: "")
                        append("exp_date", wireRequest.expDate ?: "")

                        wireRequest.deleteListId.forEach { fileData ->
                            append("delete_file_ids", fileData)
                        }


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
        }.body<com.wealthvault.data.portfolio.insurance.transport.model.InsuranceResponse>().requireDomainData()
}

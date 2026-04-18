package com.wealthvault.cash_api.updatecash

import com.wealthvault.cash_api.model.CashRequest
import com.wealthvault.cash_api.model.CashResponse
import com.wealthvault.config.Config
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

class UpdateCashApiImpl(private val ktorfit: Ktorfit) : UpdateCashApi {
    override suspend fun updateCash(id: String, request: CashRequest): CashResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.patch("${Config.localhost_android}asset/cash/${id}/") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        // ส่งข้อมูลที่เป็น Text/String จาก request object

                        append("name", request.name?: "")
                        append("description", request.description?: "")
                        append("amount", request.ammount.toString())

                        request.deleteListId?.forEach { fileData ->
                            append("delete_file_ids", fileData)
                        }

                        request.files?.forEach { fileData ->
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

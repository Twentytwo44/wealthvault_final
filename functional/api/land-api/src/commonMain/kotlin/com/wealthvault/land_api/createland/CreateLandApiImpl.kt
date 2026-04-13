package com.wealthvault.investment_api.createcash




import com.wealthvault.config.Config
import com.wealthvault.land_api.model.LandRequest
import com.wealthvault.land_api.model.LandResponse
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

class CreateLandApiImpl(private val ktorfit: Ktorfit) : CreateLandApi {
    override suspend fun create(request: LandRequest): LandResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.post("${Config.localhost_android}asset/land/") {
            setBody(
                MultiPartFormDataContent(
                    formData {

                        append("name", request.name ?: "")
                        append("deed_num", request.deedNum ?: "")
                        append("area", request.area ?: 0.0)
                        append("amount", request.amount ?: 0.0)
                        append("description", request.description ?: "")
                        append("location.address", request.locationAddress ?: "")
                        append("location.sub_district", request.locationSubDistrict ?: "")
                        append("location.district", request.locationDistrict ?: "")
                        append("location.province", request.locationProvince ?: "")
                        append("location.postal_code", request.locationPostalCode ?: "")


                        request.referenceIds.forEach { refData ->
                            append("reference_ids", refData.areaId ?: "")
                        }

                        request.files.forEach { fileData ->
                            append("files", fileData.bytes ?: byteArrayOf() , Headers.build {

                                // ✅ 1. ใส่ ContentType ตามชนิดไฟล์จริงๆ (image/jpeg หรือ application/pdf)
                                append(HttpHeaders.ContentType, fileData.mimeType  ?: "")

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

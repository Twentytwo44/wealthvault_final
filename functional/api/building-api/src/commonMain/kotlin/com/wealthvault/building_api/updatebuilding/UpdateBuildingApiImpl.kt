package com.wealthvault.building_api.updatebuilding



import com.wealthvault.building_api.model.BuildingRequest
import com.wealthvault.building_api.model.BuildingResponse
import com.wealthvault.config.Config
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

class UpdateBuildingApiImpl(private val ktorfit: Ktorfit) : UpdateBuildingApi {
    override suspend fun updateBuilding(id: String, request: BuildingRequest): BuildingResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.patch("${Config.localhost_android}asset/building/${id}/") {
            setBody(
                MultiPartFormDataContent(
                    formData {

                        append("name", request.name ?: "")
                        append("type", request.type ?: "")
                        append("area", request.area ?: 0.0)
                        append("amount", request.amount ?: 0.0)
                        append("description", request.description ?: "")
                        append("location.address", request.locationAddress ?: "")
                        append("location.sub_district", request.locationSubDistrict ?: "")
                        append("location.district", request.locationDistrict ?: "")
                        append("location.province", request.locationProvince ?: "")
                        append("location.postal_code", request.locationPostalCode ?: "")
                        request.insIds.forEach { insData ->
                            append("ins_ids", insData.insId ?: "")
                        }
                        request.referenceIds.forEach { refData ->
                            append("reference_ids", refData.areaId ?: "")
                        }

                        request.deleteListId.forEach { fileData ->
                            append("delete_file_ids", fileData)
                        }

                        request.deleteRefListId.forEach { data ->
                            append("delete_reference_ids", data.areaId ?: "" )
                        }


                        request.files.forEach { fileData ->
                            append("files", fileData.bytes, Headers.build {

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

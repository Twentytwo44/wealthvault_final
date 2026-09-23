package com.wealthvault.data.portfolio.building.transport.updatebuilding



import com.wealthvault.data.portfolio.building.transport.model.BuildingResponse
import com.wealthvault.config.Config
import com.wealthvault.data.portfolio.building.transport.requireDomainData
import com.wealthvault.data.portfolio.building.transport.toWire
import com.wealthvault.domain.portfolio.BuildingData
import com.wealthvault.domain.portfolio.BuildingRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

class UpdateBuildingApiImpl(private val client: HttpClient) : UpdateBuildingApi {
    override suspend fun updateBuilding(id: String, request: BuildingRequest): BuildingData {
        val wireRequest = request.toWire()
        return client.patch("${Config.localhost_android}asset/building/${id}/") {
            setBody(
                MultiPartFormDataContent(
                    formData {

                        append("name", wireRequest.name ?: "")
                        append("type", wireRequest.type ?: "")
                        append("area", wireRequest.area ?: 0.0)
                        append("amount", wireRequest.amount ?: 0.0)
                        append("description", wireRequest.description ?: "")
                        append("location.address", wireRequest.locationAddress ?: "")
                        append("location.sub_district", wireRequest.locationSubDistrict ?: "")
                        append("location.district", wireRequest.locationDistrict ?: "")
                        append("location.province", wireRequest.locationProvince ?: "")
                        append("location.postal_code", wireRequest.locationPostalCode ?: "")
                        wireRequest.insIds.forEach { insData ->
                            append("ins_ids", insData.insId ?: "")
                        }
                        wireRequest.referenceIds.forEach { refData ->
                            append("reference_ids", refData.areaId ?: "")
                        }

                        wireRequest.deleteListId.forEach { fileData ->
                            append("delete_file_ids", fileData)
                        }

                        wireRequest.deleteRefListId.forEach { data ->
                            append("delete_reference_ids", data.areaId ?: "" )
                        }


                        wireRequest.files.forEach { fileData ->
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
        }.body<BuildingResponse>().requireDomainData()
    }
}

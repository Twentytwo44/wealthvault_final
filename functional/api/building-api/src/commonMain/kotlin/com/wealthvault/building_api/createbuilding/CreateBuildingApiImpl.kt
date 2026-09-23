package com.wealthvault.investment_api.createcash



import com.wealthvault.building_api.model.BuildingResponse
import com.wealthvault.config.Config
import com.wealthvault.domain.portfolio.BuildingData
import com.wealthvault.domain.portfolio.BuildingRequest
import com.wealthvault.building_api.requireDomainData
import com.wealthvault.building_api.toWire
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

class CreateBuildingApiImpl(private val client: HttpClient) : CreateBuildingApi {
    override suspend fun create(request: BuildingRequest): BuildingData {
        val wireRequest = request.toWire()
        return client.post("${Config.localhost_android}asset/building/") {
            setBody(
                MultiPartFormDataContent(
                    formData {

                        append("name", wireRequest.name ?: "")
                        append("type", "BUILDING_TYPE_HOUSE")
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

                        wireRequest.files.forEach { fileData ->
                            append("files", fileData.bytes, Headers.build {

                                // ✅ 1. ใส่ ContentType ตามชนิดไฟล์จริงๆ (image/jpeg หรือ application/pdf)
                                append(HttpHeaders.ContentType, fileData.mimeType ?: "")

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

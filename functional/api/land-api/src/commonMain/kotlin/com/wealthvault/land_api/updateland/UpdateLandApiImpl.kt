package com.wealthvault.land_api.updateland



import com.wealthvault.config.Config
import com.wealthvault.domain.portfolio.LandData
import com.wealthvault.domain.portfolio.LandRequest
import com.wealthvault.land_api.model.LandResponse
import com.wealthvault.land_api.requireDomainData
import com.wealthvault.land_api.toWire
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

class UpdateLandApiImpl(private val client: HttpClient) : UpdateLandApi {
    override suspend fun updateLand(id: String, request: LandRequest): LandData {
        val wireRequest = request.toWire()
        return client.patch("${Config.localhost_android}asset/land/${id}/") {
            setBody(
                MultiPartFormDataContent(
                    formData {

                        append("name", wireRequest.name ?: "")
                        append("deed_num", wireRequest.deedNum ?: "")
                        append("area", wireRequest.area ?: 0.0)
                        append("amount", wireRequest.amount ?: 0.0)
                        append("description", wireRequest.description ?: "")
                        append("location.address", wireRequest.locationAddress ?: "")
                        append("location.sub_district", wireRequest.locationSubDistrict ?: "")
                        append("location.district", wireRequest.locationDistrict ?: "")
                        append("location.province", wireRequest.locationProvince ?: "")
                        append("location.postal_code", wireRequest.locationPostalCode ?: "")


                        wireRequest.deleteListId.forEach { fileData ->
                            append("delete_file_ids", fileData)
                        }

                        wireRequest.referenceIds.forEach { refData ->
                            append("reference_ids", refData.areaId ?: "")
                        }

                        wireRequest.deleteRefListId.forEach { data ->
                            append("delete_reference_ids", data.areaId ?: "" )
                        }

                        wireRequest.files.forEach { fileData ->
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

        }.body<LandResponse>().requireDomainData()
    }
}

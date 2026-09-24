package com.wealthvault.financiallist.ui.asset.form.building

import com.wealthvault.core.model.Attachment
import com.wealthvault.domain.portfolio.BuildingFileUploadData
import com.wealthvault.domain.portfolio.BuildingReferenceData
import com.wealthvault.domain.portfolio.BuildingRequest
import com.wealthvault.domain.portfolio.BuildingModel
import com.wealthvault.domain.portfolio.InsRefModel
import com.wealthvault.domain.portfolio.InsReferenceData
import com.wealthvault.domain.portfolio.RefModel

internal fun buildBuildingRequest(
    current: BuildingModel,
    addedAttachments: List<Attachment>,
    deletedAttachments: List<Attachment>,
    addedReferences: List<RefModel>,
    deletedReferences: List<RefModel>,
    addedInsurance: List<InsRefModel>,
    deletedInsurance: List<InsRefModel>
): BuildingRequest {
    val allFiles = addedAttachments.mapNotNull { attachment ->
        val bytes = attachment.platformData as? ByteArray ?: return@mapNotNull null
        val isPdf = attachment.name.endsWith(".pdf", ignoreCase = true) ||
            attachment.type.toString().contains("PDF")
        val mimeType = if (isPdf) "application/pdf" else "image/jpeg"
        val extension = if (isPdf) "pdf" else "jpg"
        val fileName = "${current.buildingName}.$extension"

        BuildingFileUploadData(bytes = bytes, mimeType = mimeType, fileName = fileName)
    }

    return BuildingRequest(
        type = current.type,
        name = current.buildingName,
        area = current.area,
        amount = current.amount,
        description = current.description,
        locationAddress = current.locationAddress,
        locationSubDistrict = current.locationSubDistrict,
        locationDistrict = current.locationDistrict,
        locationProvince = current.locationProvince,
        locationPostalCode = current.locationPostalCode,
        files = allFiles,
        insIds = addedInsurance.map { data ->
            InsReferenceData(insName = data.insName, insId = data.insId)
        },
        deleteInsListId = deletedInsurance.map { data ->
            InsReferenceData(insName = data.insName, insId = data.insId)
        },
        referenceIds = addedReferences.map { data ->
            BuildingReferenceData(areaName = data.areaName, areaId = data.areaId)
        },
        deleteRefListId = deletedReferences.map { data ->
            BuildingReferenceData(areaName = data.areaName, areaId = data.areaId)
        },
        deleteListId = deletedAttachments.map { it.id ?: "" }
    )
}

package com.wealthvault.data.portfolio.form.asset.data.building


import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.map
import com.wealthvault.core.cache.FeatureCache
import com.wealthvault.core.cache.ReferenceListCache
import com.wealthvault.core.model.Money
import com.wealthvault.domain.portfolio.AssetFile
import com.wealthvault.domain.portfolio.BuildingInsurance
import com.wealthvault.domain.portfolio.BuildingLocation
import com.wealthvault.domain.portfolio.GetBuildingData
import com.wealthvault.domain.portfolio.BuildingReferenceRepository
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class GetBuildingRepositoryImpl(
    private val networkDataSource: GetBuildingNetworkDataSource,
    cache: FeatureCache? = null,
    json: Json? = null,
) : BuildingReferenceRepository {
    private val referenceCache = ReferenceListCache(
        cache = cache,
        json = json,
        namespace = CACHE_NAMESPACE,
        key = CACHE_KEY,
    )

    override suspend fun getBuilding(force: Boolean): AppResult<List<GetBuildingData>> {
        return referenceCache
            .get(CachedBuilding.serializer(), force = force) {
                networkDataSource.getBuilding().map { data -> data.map(CachedBuilding::fromDomain) }
            }
            .map { data -> data.map(CachedBuilding::toDomain) }
    }

    private companion object {
        const val CACHE_NAMESPACE = "reference-lists"
        const val CACHE_KEY = "building"
    }
}

@Serializable
private data class CachedBuilding(
    val id: String?,
    val userId: String?,
    val type: String?,
    val name: String?,
    val area: Double?,
    val amount: Money?,
    val description: String?,
    val location: CachedBuildingLocation?,
    val ins: List<CachedBuildingInsurance>?,
    val referenceIds: List<String>?,
    val files: List<CachedAssetFile>?,
    val createdAt: String?,
    val updatedAt: String?,
) {
    fun toDomain() = GetBuildingData(
        id = id,
        userId = userId,
        type = type,
        name = name,
        area = area,
        amount = amount,
        description = description,
        location = location?.toDomain(),
        ins = ins?.map(CachedBuildingInsurance::toDomain),
        referenceIds = referenceIds,
        files = files?.map(CachedAssetFile::toDomain),
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

    companion object {
        fun fromDomain(value: GetBuildingData) = CachedBuilding(
            id = value.id,
            userId = value.userId,
            type = value.type,
            name = value.name,
            area = value.area,
            amount = value.amount,
            description = value.description,
            location = value.location?.let(CachedBuildingLocation::fromDomain),
            ins = value.ins?.map(CachedBuildingInsurance::fromDomain),
            referenceIds = value.referenceIds,
            files = value.files?.map(CachedAssetFile::fromDomain),
            createdAt = value.createdAt,
            updatedAt = value.updatedAt,
        )
    }
}

@Serializable
private data class CachedBuildingLocation(
    val locationId: String,
    val address: String,
    val subDistrict: String,
    val district: String,
    val province: String,
    val postalCode: String,
    val createdAt: String?,
    val updatedAt: String?,
) {
    fun toDomain() = BuildingLocation(
        locationId = locationId,
        address = address,
        subDistrict = subDistrict,
        district = district,
        province = province,
        postalCode = postalCode,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

    companion object {
        fun fromDomain(value: BuildingLocation) = CachedBuildingLocation(
            locationId = value.locationId,
            address = value.address,
            subDistrict = value.subDistrict,
            district = value.district,
            province = value.province,
            postalCode = value.postalCode,
            createdAt = value.createdAt,
            updatedAt = value.updatedAt,
        )
    }
}

@Serializable
private data class CachedBuildingInsurance(val insId: String, val insName: String) {
    fun toDomain() = BuildingInsurance(insId = insId, insName = insName)

    companion object {
        fun fromDomain(value: BuildingInsurance) = CachedBuildingInsurance(value.insId, value.insName)
    }
}

@Serializable
private data class CachedAssetFile(val id: String, val url: String, val fileType: String) {
    fun toDomain() = AssetFile(id = id, url = url, fileType = fileType)

    companion object {
        fun fromDomain(value: AssetFile) = CachedAssetFile(value.id, value.url, value.fileType)
    }
}

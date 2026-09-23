package com.wealthvault.data.portfolio.form.asset.data.land


import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.map
import com.wealthvault.core.cache.FeatureCache
import com.wealthvault.core.cache.ReferenceListCache
import com.wealthvault.domain.portfolio.GetLandData
import com.wealthvault.domain.portfolio.LandReferenceRepository
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class GetLandRepositoryImpl(
    private val networkDataSource: GetLandNetworkDataSource,
    cache: FeatureCache? = null,
    json: Json? = null,
) : LandReferenceRepository {
    private val referenceCache = ReferenceListCache(
        cache = cache,
        json = json,
        namespace = CACHE_NAMESPACE,
        key = CACHE_KEY,
    )

    override suspend fun getLand(force: Boolean): AppResult<List<GetLandData>> {
        return referenceCache
            .get(CachedLandReference.serializer(), force = force) {
                networkDataSource.getLand().map { data -> data.map(CachedLandReference::fromDomain) }
            }
            .map { data -> data.map(CachedLandReference::toDomain) }
    }

    private companion object {
        const val CACHE_NAMESPACE = "reference-lists"
        const val CACHE_KEY = "land"
    }
}

@Serializable
private data class CachedLandReference(
    val id: String?,
    val userId: String?,
    val name: String?,
    val deedNum: String?,
    val area: Int?,
    val amount: com.wealthvault.core.model.Money?,
    val description: String?,
    val location: CachedLandLocation?,
    val createdAt: String?,
    val updatedAt: String?,
) {
    fun toDomain() = GetLandData(
        id = id,
        userId = userId,
        name = name,
        deedNum = deedNum,
        area = area,
        amount = amount,
        description = description,
        location = location?.toDomain(),
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

    companion object {
        fun fromDomain(value: GetLandData) = CachedLandReference(
            id = value.id,
            userId = value.userId,
            name = value.name,
            deedNum = value.deedNum,
            area = value.area,
            amount = value.amount,
            description = value.description,
            location = value.location?.let(CachedLandLocation::fromDomain),
            createdAt = value.createdAt,
            updatedAt = value.updatedAt,
        )
    }
}

@Serializable
private data class CachedLandLocation(
    val locationId: String,
    val address: String,
    val subDistrict: String,
    val district: String,
    val province: String,
    val postalCode: String,
    val createdAt: String?,
    val updatedAt: String?,
) {
    fun toDomain() = com.wealthvault.domain.portfolio.LandLocation(
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
        fun fromDomain(value: com.wealthvault.domain.portfolio.LandLocation) = CachedLandLocation(
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

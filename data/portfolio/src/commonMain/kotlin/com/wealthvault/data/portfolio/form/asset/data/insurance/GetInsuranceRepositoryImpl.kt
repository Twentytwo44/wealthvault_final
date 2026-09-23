package com.wealthvault.data.portfolio.form.asset.data.insurance

import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.map
import com.wealthvault.core.cache.FeatureCache
import com.wealthvault.core.cache.ReferenceListCache
import com.wealthvault.core.model.Money
import com.wealthvault.domain.portfolio.GetInsuranceData
import com.wealthvault.domain.portfolio.InsuranceReferenceRepository
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


class GetInsuranceRepositoryImpl(
    private val networkDataSource: GetInsuranceNetworkDataSource,
    cache: FeatureCache? = null,
    json: Json? = null,
) : InsuranceReferenceRepository {
    private val referenceCache = ReferenceListCache(
        cache = cache,
        json = json,
        namespace = CACHE_NAMESPACE,
        key = CACHE_KEY,
    )

    override suspend fun getInsurance(force: Boolean): AppResult<List<GetInsuranceData>> {
        return referenceCache
            .get(CachedInsuranceReference.serializer(), force = force) {
                networkDataSource.getInsurance().map { values -> values.map(CachedInsuranceReference::fromDomain) }
            }
            .map { data ->
            data.map(CachedInsuranceReference::toDomain)
        }
    }

    private companion object {
        const val CACHE_NAMESPACE = "reference-lists"
        const val CACHE_KEY = "insurance"
    }
}

@Serializable
private data class CachedInsuranceReference(
    val id: String?,
    val userId: String?,
    val name: String?,
    val policyNumber: String?,
    val type: String?,
    val companyName: String?,
    val coveragePeriod: Int?,
    val coverageAmount: Money?,
    val conDate: String?,
    val expDate: String?,
    val description: String?,
    val createdAt: String?,
    val updatedAt: String?,
) {
    fun toDomain() = GetInsuranceData(
        id = id,
        userId = userId,
        name = name,
        policyNumber = policyNumber,
        type = type,
        companyName = companyName,
        coveragePeriod = coveragePeriod,
        coverageAmount = coverageAmount,
        conDate = conDate,
        expDate = expDate,
        description = description,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

    companion object {
        fun fromDomain(value: GetInsuranceData) = CachedInsuranceReference(
            id = value.id,
            userId = value.userId,
            name = value.name,
            policyNumber = value.policyNumber,
            type = value.type,
            companyName = value.companyName,
            coveragePeriod = value.coveragePeriod,
            coverageAmount = value.coverageAmount,
            conDate = value.conDate,
            expDate = value.expDate,
            description = value.description,
            createdAt = value.createdAt,
            updatedAt = value.updatedAt,
        )
    }
}

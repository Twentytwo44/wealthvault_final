package com.wealthvault.data.portfolio.repository

import com.wealthvault.domain.portfolio.*
import com.wealthvault.core.model.FileDataModel
import com.wealthvault.core.model.Money
import com.wealthvault.core.model.FixedDecimal
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.CacheFreshness
import com.wealthvault.core.architecture.CachedValue
import com.wealthvault.core.cache.FeatureCache
import com.wealthvault.core.cache.isCacheFresh
import com.wealthvault.core.cache.sanitizeCacheTimestamp
import com.wealthvault.core.concurrency.SingleFlight
import com.wealthvault.core.architecture.runSuspendAppCatching
import kotlinx.serialization.encodeToString
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlin.time.Clock

class FinanciallistRepositoryImpl(
    private val dataSource: FinanciallistRemoteDataSource,
    private val cache: FeatureCache? = null,
    private val json: Json? = null,
) : PortfolioRepository {
    private val singleFlight = SingleFlight<String>()
    private val observedAccounts = MutableStateFlow<CachedValue<List<AccountData>>?>(null)
    private val observedCashes = MutableStateFlow<CachedValue<List<GetCashData>>?>(null)
    private val observedInvestments = MutableStateFlow<CachedValue<List<GetInvestmentData>>?>(null)
    private val observedInsurances = MutableStateFlow<CachedValue<List<GetInsuranceData>>?>(null)
    private val observedBuildings = MutableStateFlow<CachedValue<List<GetBuildingData>>?>(null)
    private val observedLands = MutableStateFlow<CachedValue<List<GetLandData>>?>(null)
    private val observedLiabilities = MutableStateFlow<CachedValue<List<GetLiabilityData>>?>(null)

    override fun observeAccounts(): Flow<CachedValue<List<AccountData>>> = observedAccounts.filterNotNull().distinctUntilChanged()
    override fun observeCashes(): Flow<CachedValue<List<GetCashData>>> = observedCashes.filterNotNull().distinctUntilChanged()
    override fun observeInvestments(): Flow<CachedValue<List<GetInvestmentData>>> = observedInvestments.filterNotNull().distinctUntilChanged()
    override fun observeInsurances(): Flow<CachedValue<List<GetInsuranceData>>> = observedInsurances.filterNotNull().distinctUntilChanged()
    override fun observeBuildings(): Flow<CachedValue<List<GetBuildingData>>> = observedBuildings.filterNotNull().distinctUntilChanged()
    override fun observeLands(): Flow<CachedValue<List<GetLandData>>> = observedLands.filterNotNull().distinctUntilChanged()
    override fun observeLiabilities(): Flow<CachedValue<List<GetLiabilityData>>> = observedLiabilities.filterNotNull().distinctUntilChanged()

    // Detail snapshots and list snapshots share the five-minute portfolio TTL.
    override suspend fun getAccountById(id: String, force: Boolean): AppResult<BankAccountData> = cachedSingle(
        key = "account:$id",
        force = force,
        load = { dataSource.getAccountById(id)?.let(CachedBankAccount::fromDomain) },
        map = CachedBankAccount::toDomain,
        missingMessage = "ไม่พบข้อมูลบัญชีเงินฝาก",
    )

    override suspend fun getBuildingById(id: String, force: Boolean): AppResult<BuildingIdData> = cachedSingle(
        key = "building:$id",
        force = force,
        load = { dataSource.getBuildingById(id)?.let(CachedBuildingId::fromDomain) },
        map = CachedBuildingId::toDomain,
        missingMessage = "ไม่พบข้อมูลอาคาร/สิ่งปลูกสร้าง",
    )

    override suspend fun getCashById(id: String, force: Boolean): AppResult<CashIdData> = cachedSingle(
        key = "cash:$id",
        force = force,
        load = { dataSource.getCashById(id)?.let(CachedCashId::fromDomain) },
        map = CachedCashId::toDomain,
        missingMessage = "ไม่พบข้อมูลเงินสด/ทองคำ",
    )

    override suspend fun getInsuranceById(id: String, force: Boolean): AppResult<InsuranceIdData> = cachedSingle(
        key = "insurance:$id",
        force = force,
        load = { dataSource.getInsuranceById(id)?.let(CachedInsuranceId::fromDomain) },
        map = CachedInsuranceId::toDomain,
        missingMessage = "ไม่พบข้อมูลประกัน",
    )

    override suspend fun getInvestmentById(id: String, force: Boolean): AppResult<InvestmentIdData> = cachedSingle(
        key = "investment:$id",
        force = force,
        load = { dataSource.getInvestmentById(id)?.let(CachedInvestmentId::fromDomain) },
        map = CachedInvestmentId::toDomain,
        missingMessage = "ไม่พบข้อมูลการลงทุน",
    )

    override suspend fun getLandById(id: String, force: Boolean): AppResult<LandIdData> = cachedSingle(
        key = "land:$id",
        force = force,
        load = { dataSource.getLandById(id)?.let(CachedLandId::fromDomain) },
        map = CachedLandId::toDomain,
        missingMessage = "ไม่พบข้อมูลที่ดิน",
    )

    override suspend fun getLiabilityById(id: String, force: Boolean): AppResult<LiabilityIdData> = cachedSingle(
        key = "liability:$id",
        force = force,
        load = { dataSource.getLiabilityById(id)?.let(CachedLiabilityId::fromDomain) },
        map = CachedLiabilityId::toDomain,
        missingMessage = "ไม่พบข้อมูลหนี้สิน/รายจ่าย",
    )

    override suspend fun getAccounts(force: Boolean): AppResult<List<AccountData>> = cachedList(
        key = "accounts", force = force,
        load = { dataSource.getAccount().map(CachedAccount::fromDomain) }, map = CachedAccount::toDomain,
        publish = { value, freshness, updatedAt -> observedAccounts.value = CachedValue(value, freshness, updatedAt) },
    )

    override suspend fun getCashes(force: Boolean): AppResult<List<GetCashData>> = cachedList(
        key = "cashes", force = force,
        load = { dataSource.getCash().map(CachedCash::fromDomain) }, map = CachedCash::toDomain,
        publish = { value, freshness, updatedAt -> observedCashes.value = CachedValue(value, freshness, updatedAt) },
    )

    override suspend fun getInvestments(force: Boolean): AppResult<List<GetInvestmentData>> = cachedList(
        key = "investments", force = force,
        load = { dataSource.getInvestment().map(CachedInvestment::fromDomain) }, map = CachedInvestment::toDomain,
        publish = { value, freshness, updatedAt -> observedInvestments.value = CachedValue(value, freshness, updatedAt) },
    )

    override suspend fun getInsurances(force: Boolean): AppResult<List<GetInsuranceData>> = cachedList(
        key = "insurances", force = force,
        load = { dataSource.getInsurance().map(CachedInsurance::fromDomain) }, map = CachedInsurance::toDomain,
        publish = { value, freshness, updatedAt -> observedInsurances.value = CachedValue(value, freshness, updatedAt) },
    )

    override suspend fun getBuildings(force: Boolean): AppResult<List<GetBuildingData>> = cachedList(
        key = "buildings", force = force,
        load = { dataSource.getBuilding().map(CachedBuilding::fromDomain) }, map = CachedBuilding::toDomain,
        publish = { value, freshness, updatedAt -> observedBuildings.value = CachedValue(value, freshness, updatedAt) },
    )

    override suspend fun getLands(force: Boolean): AppResult<List<GetLandData>> = cachedList(
        key = "lands", force = force,
        load = { dataSource.getLand().map(CachedLand::fromDomain) }, map = CachedLand::toDomain,
        publish = { value, freshness, updatedAt -> observedLands.value = CachedValue(value, freshness, updatedAt) },
    )

    override suspend fun getLiabilities(force: Boolean): AppResult<List<GetLiabilityData>> = cachedList(
        key = "liabilities", force = force,
        load = { dataSource.getLiability().map(CachedLiability::fromDomain) }, map = CachedLiability::toDomain,
        publish = { value, freshness, updatedAt -> observedLiabilities.value = CachedValue(value, freshness, updatedAt) },
    )

    override suspend fun refresh(force: Boolean): AppResult<Unit> {
        val results = listOf(
            getAccounts(force),
            getCashes(force),
            getInvestments(force),
            getInsurances(force),
            getBuildings(force),
            getLands(force),
            getLiabilities(force),
        )
        return results.firstOrNull { it is AppResult.Failure }?.let { failure ->
            AppResult.Failure((failure as AppResult.Failure).error)
        } ?: AppResult.Success(Unit)
    }

    override suspend fun deleteAsset(id: String, type: String): AppResult<Boolean> =
        runSuspendAppCatching {
            check(dataSource.deleteAsset(id, type)) { "เกิดข้อผิดพลาดในการลบข้อมูล" }
            true
        }.invalidatePortfolioCache(cache)

    private suspend inline fun <reified D, R> cachedList(
        key: String,
        force: Boolean,
        crossinline load: suspend () -> List<D>,
        crossinline map: (D) -> R,
        crossinline publish: (List<R>, CacheFreshness, Long) -> Unit,
    ): AppResult<List<R>> {
        val now = Clock.System.now().toEpochMilliseconds()
        val cached = readCached<List<D>>(key, now)
        if (!force && cached != null && isCacheFresh(cached.updatedAtEpochMillis, now, PORTFOLIO_TTL_MILLIS)) {
            val value = cached.value.map(map)
            publish(value, CacheFreshness.Fresh, cached.updatedAtEpochMillis)
            return AppResult.Success(value)
        }

        cached?.let { publish(it.value.map(map), CacheFreshness.Stale, it.updatedAtEpochMillis) }

        return when (val result = singleFlight.execute(key) {
            runSuspendAppCatching { load() }
        }) {
            is AppResult.Success -> {
                val value = result.value
                writeCached(key, value, now)
                val mapped = value.map(map)
                publish(mapped, CacheFreshness.Fresh, now)
                AppResult.Success(mapped)
            }
            is AppResult.Failure -> {
                cached?.let {
                    val mapped = it.value.map(map)
                    publish(mapped, CacheFreshness.Offline, it.updatedAtEpochMillis)
                    AppResult.Success(mapped)
                }
                    ?: result
            }
        }
    }

    private suspend inline fun <reified D, R> cachedSingle(
        key: String,
        force: Boolean,
        crossinline load: suspend () -> D?,
        crossinline map: (D) -> R,
        missingMessage: String,
    ): AppResult<R> {
        val now = Clock.System.now().toEpochMilliseconds()
        val cached = readCached<D>(key, now)
        if (!force && cached != null && isCacheFresh(cached.updatedAtEpochMillis, now, PORTFOLIO_TTL_MILLIS)) {
            return AppResult.Success(map(cached.value))
        }

        return when (val result = singleFlight.execute(key) {
            runSuspendAppCatching { load() ?: error(missingMessage) }
        }) {
            is AppResult.Success -> {
                writeCached(key, result.value, now)
                AppResult.Success(map(result.value))
            }
            is AppResult.Failure -> cached?.let { AppResult.Success(map(it.value)) } ?: result
        }
    }

    private suspend inline fun <reified T> readCached(
        key: String,
        now: Long,
    ): CachedPayload<T>? = try {
        cache?.read(NAMESPACE, key)?.let { entry ->
            runCatching { json?.decodeFromString<T>(entry.payload) }
                .getOrNull()
                ?.let {
                    CachedPayload(
                        it,
                        sanitizeCacheTimestamp(entry.updatedAtEpochMillis, now, PORTFOLIO_TTL_MILLIS),
                    )
                }
        }
    } catch (error: Throwable) {
        if (error is kotlinx.coroutines.CancellationException) throw error
        null
    }

    private suspend inline fun <reified T> writeCached(key: String, value: T, now: Long) {
        val encoded = json?.let { runCatching { it.encodeToString(value) }.getOrNull() } ?: return
        try {
            cache?.write(NAMESPACE, key, encoded, now)
        } catch (error: Throwable) {
            if (error is kotlinx.coroutines.CancellationException) throw error
            // A cache outage must never turn a successful network mutation into
            // a user-visible failure; the next request can repopulate it.
        }
    }

    private data class CachedPayload<T>(val value: T, val updatedAtEpochMillis: Long)

    private companion object {
        const val NAMESPACE = "portfolio"
        const val PORTFOLIO_TTL_MILLIS = 5 * 60 * 1_000L
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
private data class CachedBuildingId(
    val id: String?,
    val userId: String?,
    val type: String?,
    val name: String?,
    val area: Double?,
    val amount: Money?,
    val description: String?,
    val location: CachedBuildingLocation?,
    val ins: List<CachedBuildingInsurance>?,
    val referenceIds: List<CachedBuildingReference>?,
    val files: List<CachedAssetFile>?,
    val createdAt: String?,
    val updatedAt: String?,
) {
    fun toDomain() = BuildingIdData(
        id = id,
        userId = userId,
        type = type,
        name = name,
        area = area,
        amount = amount,
        description = description,
        location = location?.toDomain(),
        ins = ins?.map(CachedBuildingInsurance::toDomain),
        referenceIds = referenceIds?.map(CachedBuildingReference::toDomain),
        files = files?.map(CachedAssetFile::toDomain),
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

    companion object {
        fun fromDomain(value: BuildingIdData) = CachedBuildingId(
            id = value.id,
            userId = value.userId,
            type = value.type,
            name = value.name,
            area = value.area,
            amount = value.amount,
            description = value.description,
            location = value.location?.let(CachedBuildingLocation::fromDomain),
            ins = value.ins?.map(CachedBuildingInsurance::fromDomain),
            referenceIds = value.referenceIds?.map(CachedBuildingReference::fromDomain),
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
private data class CachedBuildingInsurance(
    val insId: String,
    val insName: String,
) {
    fun toDomain() = BuildingInsurance(insId = insId, insName = insName)

    companion object {
        fun fromDomain(value: BuildingInsurance) = CachedBuildingInsurance(insId = value.insId, insName = value.insName)
    }
}

@Serializable
private data class CachedBuildingReference(
    val refId: String,
    val refName: String,
) {
    fun toDomain() = BuildingReference(refId = refId, refName = refName)

    companion object {
        fun fromDomain(value: BuildingReference) = CachedBuildingReference(refId = value.refId, refName = value.refName)
    }
}

@Serializable
private data class CachedAccount(
    val id: String,
    val userId: String,
    val name: String?,
    val bankName: String?,
    val bankAccount: String?,
    val type: String?,
    val amount: Money?,
    val description: String?,
) {
    fun toDomain() = AccountData(
        id = id,
        userId = userId,
        name = name,
        bankName = bankName,
        bankAccount = bankAccount,
        type = type,
        amount = amount,
        description = description,
    )

    companion object {
        fun fromDomain(value: AccountData) = CachedAccount(
            id = value.id,
            userId = value.userId,
            name = value.name,
            bankName = value.bankName,
            bankAccount = value.bankAccount,
            type = value.type,
            amount = value.amount,
            description = value.description,
        )
    }
}

@Serializable
private data class CachedBankAccount(
    val id: String,
    val userId: String,
    val name: String?,
    val bankName: String?,
    val bankAccount: String?,
    val type: String?,
    val amount: Money?,
    val description: String?,
    val files: List<CachedAssetFile>?,
    val createdAt: String?,
    val updatedAt: String?,
) {
    fun toDomain() = BankAccountData(
        id = id,
        userId = userId,
        name = name,
        bankName = bankName,
        bankAccount = bankAccount,
        type = type,
        amount = amount,
        description = description,
        files = files?.map(CachedAssetFile::toDomain),
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

    companion object {
        fun fromDomain(value: BankAccountData) = CachedBankAccount(
            id = value.id,
            userId = value.userId,
            name = value.name,
            bankName = value.bankName,
            bankAccount = value.bankAccount,
            type = value.type,
            amount = value.amount,
            description = value.description,
            files = value.files?.map(CachedAssetFile::fromDomain),
            createdAt = value.createdAt,
            updatedAt = value.updatedAt,
        )
    }
}

/**
 * The domain contracts intentionally do not carry persistence annotations.
 * These tiny snapshots keep SQL/cache serialization local to the data
 * implementation while transport DTOs remain private to their API modules.
 */
@Serializable
private data class CachedLand(
    val id: String?,
    val userId: String?,
    val name: String?,
    val deedNum: String?,
    val area: Int?,
    val amount: Money?,
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
        fun fromDomain(value: GetLandData) = CachedLand(
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
private data class CachedLandId(
    val id: String,
    val userId: String,
    val name: String?,
    val deedNum: String?,
    val area: Double?,
    val amount: Money?,
    val description: String?,
    val location: CachedLandLocation?,
    val files: List<CachedAssetFile>?,
    val createdAt: String?,
    val updatedAt: String?,
    val ref: List<CachedLandReference>?,
) {
    fun toDomain() = LandIdData(
        id = id,
        userId = userId,
        name = name,
        deedNum = deedNum,
        area = area,
        amount = amount,
        description = description,
        location = location?.toDomain(),
        files = files?.map(CachedAssetFile::toDomain),
        createdAt = createdAt,
        updatedAt = updatedAt,
        ref = ref?.map(CachedLandReference::toDomain),
    )

    companion object {
        fun fromDomain(value: LandIdData) = CachedLandId(
            id = value.id,
            userId = value.userId,
            name = value.name,
            deedNum = value.deedNum,
            area = value.area,
            amount = value.amount,
            description = value.description,
            location = value.location?.let(CachedLandLocation::fromDomain),
            files = value.files?.map(CachedAssetFile::fromDomain),
            createdAt = value.createdAt,
            updatedAt = value.updatedAt,
            ref = value.ref?.map(CachedLandReference::fromDomain),
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
    fun toDomain() = LandLocation(
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
        fun fromDomain(value: LandLocation) = CachedLandLocation(
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
private data class CachedLandReference(
    val refId: String,
    val refName: String,
) {
    fun toDomain() = LandReference(refId = refId, refName = refName)

    companion object {
        fun fromDomain(value: LandReference) = CachedLandReference(refId = value.refId, refName = value.refName)
    }
}

@Serializable
private data class CachedInvestment(
    val id: String?,
    val userId: String?,
    val name: String?,
    val symbol: String?,
    val type: String?,
    val brokerName: String?,
    val quantity: FixedDecimal?,
    val costPerPrice: Money?,
    val amount: Money?,
    val description: String?,
    val createdAt: String?,
    val updatedAt: String?,
) {
    fun toDomain() = GetInvestmentData(
        id = id,
        userId = userId,
        name = name,
        symbol = symbol,
        type = type,
        brokerName = brokerName,
        quantity = quantity,
        costPerPrice = costPerPrice,
        amount = amount,
        description = description,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

    companion object {
        fun fromDomain(value: GetInvestmentData) = CachedInvestment(
            id = value.id,
            userId = value.userId,
            name = value.name,
            symbol = value.symbol,
            type = value.type,
            brokerName = value.brokerName,
            quantity = value.quantity,
            costPerPrice = value.costPerPrice,
            amount = value.amount,
            description = value.description,
            createdAt = value.createdAt,
            updatedAt = value.updatedAt,
        )
    }
}

@Serializable
private data class CachedInvestmentId(
    val id: String,
    val userId: String,
    val name: String?,
    val symbol: String?,
    val type: String?,
    val brokerName: String?,
    val quantity: FixedDecimal?,
    val costPerPrice: Money?,
    val amount: Money?,
    val description: String?,
    val files: List<CachedAssetFile>?,
    val createdAt: String?,
    val updatedAt: String?,
) {
    fun toDomain() = InvestmentIdData(
        id = id,
        userId = userId,
        name = name,
        symbol = symbol,
        type = type,
        brokerName = brokerName,
        quantity = quantity,
        costPerPrice = costPerPrice,
        amount = amount,
        description = description,
        files = files?.map(CachedAssetFile::toDomain),
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

    companion object {
        fun fromDomain(value: InvestmentIdData) = CachedInvestmentId(
            id = value.id,
            userId = value.userId,
            name = value.name,
            symbol = value.symbol,
            type = value.type,
            brokerName = value.brokerName,
            quantity = value.quantity,
            costPerPrice = value.costPerPrice,
            amount = value.amount,
            description = value.description,
            files = value.files?.map(CachedAssetFile::fromDomain),
            createdAt = value.createdAt,
            updatedAt = value.updatedAt,
        )
    }
}

@Serializable
private data class CachedLiability(
    val id: String?,
    val userId: String?,
    val type: String?,
    val name: String?,
    val creditor: String?,
    val principal: Money?,
    val interestRate: FixedDecimal?,
    val description: String?,
    val startedAt: String?,
    val endedAt: String?,
    val createdAt: String?,
    val updatedAt: String?,
) {
    fun toDomain() = GetLiabilityData(
        id = id,
        userId = userId,
        type = type,
        name = name,
        creditor = creditor,
        principal = principal,
        interestRate = interestRate,
        description = description,
        startedAt = startedAt,
        endedAt = endedAt,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

    companion object {
        fun fromDomain(value: GetLiabilityData) = CachedLiability(
            id = value.id,
            userId = value.userId,
            type = value.type,
            name = value.name,
            creditor = value.creditor,
            principal = value.principal,
            interestRate = value.interestRate,
            description = value.description,
            startedAt = value.startedAt,
            endedAt = value.endedAt,
            createdAt = value.createdAt,
            updatedAt = value.updatedAt,
        )
    }
}

@Serializable
private data class CachedLiabilityId(
    val id: String,
    val userId: String,
    val type: String?,
    val name: String?,
    val creditor: String?,
    val principal: Money?,
    val interestRate: FixedDecimal?,
    val description: String?,
    val startedAt: String?,
    val endedAt: String?,
    val files: List<CachedAssetFile>?,
    val createdAt: String?,
    val updatedAt: String?,
) {
    fun toDomain() = LiabilityIdData(
        id = id,
        userId = userId,
        type = type,
        name = name,
        creditor = creditor,
        principal = principal,
        interestRate = interestRate,
        description = description,
        startedAt = startedAt,
        endedAt = endedAt,
        files = files?.map(CachedAssetFile::toDomain),
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

    companion object {
        fun fromDomain(value: LiabilityIdData) = CachedLiabilityId(
            id = value.id,
            userId = value.userId,
            type = value.type,
            name = value.name,
            creditor = value.creditor,
            principal = value.principal,
            interestRate = value.interestRate,
            description = value.description,
            startedAt = value.startedAt,
            endedAt = value.endedAt,
            files = value.files?.map(CachedAssetFile::fromDomain),
            createdAt = value.createdAt,
            updatedAt = value.updatedAt,
        )
    }
}

@Serializable
private data class CachedInsurance(
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
        fun fromDomain(value: GetInsuranceData) = CachedInsurance(
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

@Serializable
private data class CachedInsuranceId(
    val id: String,
    val userId: String,
    val name: String?,
    val policyNumber: String?,
    val type: String?,
    val companyName: String?,
    val coveragePeriod: Int?,
    val coverageAmount: Money?,
    val conDate: String?,
    val expDate: String?,
    val description: String?,
    val files: List<CachedAssetFile>?,
    val createdAt: String?,
    val updatedAt: String?,
) {
    fun toDomain() = InsuranceIdData(
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
        files = files?.map(CachedAssetFile::toDomain),
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

    companion object {
        fun fromDomain(value: InsuranceIdData) = CachedInsuranceId(
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
            files = value.files?.map(CachedAssetFile::fromDomain),
            createdAt = value.createdAt,
            updatedAt = value.updatedAt,
        )
    }
}

@Serializable
private data class CachedCash(
    val id: String?,
    val userId: String?,
    val name: String?,
    val amount: Money?,
    val description: String?,
    val createdAt: String?,
    val updatedAt: String?,
) {
    fun toDomain() = GetCashData(
        id = id,
        userId = userId,
        name = name,
        ammount = amount,
        description = description,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

    companion object {
        fun fromDomain(value: GetCashData) = CachedCash(
            id = value.id,
            userId = value.userId,
            name = value.name,
            amount = value.ammount,
            description = value.description,
            createdAt = value.createdAt,
            updatedAt = value.updatedAt,
        )
    }
}

@Serializable
private data class CachedAssetFile(
    val id: String,
    val url: String,
    val fileType: String,
) {
    fun toDomain() = AssetFile(id = id, url = url, fileType = fileType)

    companion object {
        fun fromDomain(value: AssetFile) = CachedAssetFile(
            id = value.id,
            url = value.url,
            fileType = value.fileType,
        )
    }
}

@Serializable
private data class CachedCashId(
    val id: String,
    val userId: String,
    val name: String?,
    val amount: Money?,
    val description: String?,
    val files: List<CachedAssetFile>?,
    val createdAt: String?,
    val updatedAt: String?,
) {
    fun toDomain() = CashIdData(
        id = id,
        userId = userId,
        name = name,
        amount = amount,
        description = description,
        files = files?.map(CachedAssetFile::toDomain),
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

    companion object {
        fun fromDomain(value: CashIdData) = CachedCashId(
            id = value.id,
            userId = value.userId,
            name = value.name,
            amount = value.amount,
            description = value.description,
            files = value.files?.map(CachedAssetFile::fromDomain),
            createdAt = value.createdAt,
            updatedAt = value.updatedAt,
        )
    }
}

package com.wealthvault.social.data

import com.wealthvault.core.model.FriendData
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.map
import com.wealthvault.core.observability.AppLogger
import com.wealthvault.core.observability.platformLogger
import com.wealthvault.domain.social.AcceptFriendRequest
import com.wealthvault.domain.social.AcceptFriendResult
import com.wealthvault.domain.social.FriendProfile
import com.wealthvault.domain.social.GroupData
import com.wealthvault.domain.social.GroupMember
import com.wealthvault.domain.social.GroupMessage
import com.wealthvault.domain.social.GroupResult
import com.wealthvault.domain.social.GroupSummary
import com.wealthvault.domain.social.MessageItem
import com.wealthvault.domain.social.PendingFriend
import com.wealthvault.domain.social.ShareFriend
import com.wealthvault.domain.social.ShareGroup
import com.wealthvault.domain.social.ShareItems
import com.wealthvault.domain.social.ShareableItem
import com.wealthvault.core.model.BankAccountData
import com.wealthvault.core.model.BuildingIdData
import com.wealthvault.core.model.CashIdData
import com.wealthvault.core.model.InsuranceIdData
import com.wealthvault.core.model.InvestmentIdData
import com.wealthvault.core.model.LandIdData
import com.wealthvault.core.model.LiabilityIdData
import com.wealthvault.core.cache.FeatureCache
import com.wealthvault.core.cache.isCacheFresh
import com.wealthvault.core.cache.sanitizeCacheTimestamp
import com.wealthvault.core.architecture.CacheFreshness
import com.wealthvault.core.architecture.CachedValue
import com.wealthvault.core.concurrency.SingleFlight
import com.wealthvault.domain.social.SocialRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.time.Clock

internal class SocialRepositoryImpl(
    private val dataSource: SocialRemoteDataSource,
    private val logger: AppLogger = platformLogger(),
    private val cache: FeatureCache? = null,
    private val json: Json? = null,
) : SocialRepository {
    private val observedFriends = MutableStateFlow<CachedValue<List<FriendData>>?>(null)
    private val observedGroups = MutableStateFlow<CachedValue<List<GroupSummary>>?>(null)
    private val observedPendingFriends = MutableStateFlow<CachedValue<List<PendingFriend>>?>(null)
    private val refreshes = SingleFlight<String>()

    override fun observeAllFriends(): Flow<CachedValue<List<FriendData>>> =
        observedFriends.filterNotNull().distinctUntilChanged()

    override fun observeAllGroups(): Flow<CachedValue<List<GroupSummary>>> =
        observedGroups.filterNotNull().distinctUntilChanged()

    override fun observePendingFriends(): Flow<CachedValue<List<PendingFriend>>> =
        observedPendingFriends.filterNotNull().distinctUntilChanged()

    override suspend fun refresh(force: Boolean): AppResult<Unit> {
        // Keep the three read models behind their existing single-flight/cache
        // paths.  A partial failure is reported, while stale values remain
        // available through the corresponding observe* flow when present.
        val results = listOf(
            getAllFriends(force),
            getAllGroups(force),
            getPendingFriends(force),
        )
        return results.firstOrNull { it is AppResult.Failure }?.let { failure ->
            AppResult.Failure((failure as AppResult.Failure).error)
        } ?: AppResult.Success(Unit)
    }

    override suspend fun getAllFriends(force: Boolean): AppResult<List<FriendData>> = cached(
        key = FRIENDS_KEY,
        force = force,
        load = { dataSource.getAllFriends() },
        decode = { json?.decodeFromString<List<FriendCacheRecord>>(it)?.map(FriendCacheRecord::toDomain) },
        encode = { json?.encodeToString(it.map(FriendData::toCacheRecord)) },
        publish = { value, freshness, updatedAt ->
            observedFriends.value = CachedValue(value, freshness, updatedAt)
        },
    ).also { result ->
        result.onSuccess { logger.debug("Fetched friends: ${it.size}") }
            .onFailure { logger.warn("Get friends failed", it) }
    }

    override suspend fun getAllGroups(force: Boolean): AppResult<List<GroupSummary>> = cached(
        key = GROUPS_KEY,
        force = force,
        load = { dataSource.getAllGroups() },
        decode = { json?.decodeFromString<List<GroupSummaryCacheRecord>>(it)?.map(GroupSummaryCacheRecord::toDomain) },
        encode = { json?.encodeToString(it.map(GroupSummary::toCacheRecord)) },
        publish = { value, freshness, updatedAt ->
            observedGroups.value = CachedValue(value, freshness, updatedAt)
        },
    ).also { result ->
        result.onSuccess { logger.debug("Fetched groups: ${it.size}") }
            .onFailure { logger.warn("Get groups failed", it) }
    }

    override suspend fun createGroup(
        groupName: String,
        memberIds: List<String>,
        imageBytes: ByteArray?
    ): AppResult<GroupResult> {
        val result = dataSource.createGroup(groupName, memberIds, imageBytes)
        if (result.isSuccess) invalidateReadCache()
        return result.onSuccess {
            logger.info("Create group succeeded")
        }.onFailure { error ->
            logger.warn("Create group failed", error)
        }
    }

    // 🌟 1. เพิ่มฟังก์ชันค้นหาเพื่อน
    override suspend fun searchUser(email: String): AppResult<FriendData?> {
        return dataSource.searchUser(email).onSuccess {
            logger.debug("Search user succeeded")
        }.onFailure { error ->
            logger.warn("Search user failed", error)
        }
    }

    // 🌟 2. เพิ่มฟังก์ชันเพิ่มเพื่อน
    override suspend fun addFriend(targetId: String): AppResult<Boolean> {
        val result = dataSource.addFriend(targetId)
        if (result.isSuccess) invalidateReadCache()
        return result.onSuccess {
            logger.info("Add friend succeeded")
        }.onFailure { error ->
            logger.warn("Add friend failed", error)
        }
    }
    override suspend fun getFriendMessages(friendId: String): AppResult<List<MessageItem>> {
        return dataSource.getFriendMessages(friendId).onSuccess {
            logger.debug("Get friend messages succeeded: ${it.size}")
        }.onFailure { error ->
            logger.warn("Get friend messages failed", error)
        }
    }
    override suspend fun getFriendProfile(friendId: String): AppResult<FriendProfile> {
        return dataSource.getFriendProfile(friendId).onSuccess {
            logger.debug("Get friend profile succeeded")
        }.onFailure { error ->
            logger.warn("Get friend profile failed", error)
        }
    }
    override suspend fun getAccountById(id: String): AppResult<BankAccountData> = dataSource.getAccountById(id)
        .map { it ?: error("ไม่พบข้อมูลบัญชีเงินฝาก") }

    override suspend fun getBuildingById(id: String): AppResult<BuildingIdData> = dataSource.getBuildingById(id)
        .map { it ?: error("ไม่พบข้อมูลอาคาร/สิ่งปลูกสร้าง") }

    override suspend fun getCashById(id: String): AppResult<CashIdData> = dataSource.getCashById(id)
        .map { it ?: error("ไม่พบข้อมูลเงินสด/ทองคำ") }

    override suspend fun getInsuranceById(id: String): AppResult<InsuranceIdData> = dataSource.getInsuranceById(id)
        .map { it ?: error("ไม่พบข้อมูลประกัน") }

    override suspend fun getInvestmentById(id: String): AppResult<InvestmentIdData> = dataSource.getInvestmentById(id)
        .map { it ?: error("ไม่พบข้อมูลการลงทุน") }

    override suspend fun getLandById(id: String): AppResult<LandIdData> = dataSource.getLandById(id)
        .map { it ?: error("ไม่พบข้อมูลที่ดิน") }

    override suspend fun getLiabilityById(id: String): AppResult<LiabilityIdData> = dataSource.getLiabilityById(id)
        .map { it ?: error("ไม่พบข้อมูลหนี้สิน/รายจ่าย") }


    override suspend fun getGroupMessages(groupId: String): AppResult<List<GroupMessage>> {
        return dataSource.getGroupMessages(groupId).onSuccess {
            logger.debug("Get group messages succeeded: ${it.size}")
        }.onFailure { error ->
            logger.warn("Get group messages failed", error)
        }
    }

    override suspend fun getShareFriendItems(friendId: String): AppResult<List<ShareFriend>> {
        return dataSource.getShareFriendItems(friendId).onSuccess {
            logger.debug("Get shared friend items succeeded: ${it.size}")
        }.onFailure { error ->
            logger.warn("Get shared friend items failed", error)
        }
    }
    override suspend fun getShareGroupItems(groupId: String): AppResult<List<ShareGroup>> {
        return dataSource.getShareGroupItems(groupId).onSuccess {
            logger.debug("Get shared group items succeeded: ${it.size}")
        }.onFailure { error ->
            logger.warn("Get shared group items failed", error)
        }
    }

    override suspend fun grantAccess(groupId: String, targetId: String, itemIds: List<String>): AppResult<Boolean> {
        val result = dataSource.grantAccess(groupId, targetId, itemIds).onSuccess {
            logger.info("Grant access succeeded")
        }.onFailure { error ->
            logger.warn("Grant access failed", error)
        }
        return invalidateAfterSuccess(result)
    }

    override suspend fun getGroupDetail(groupId: String): AppResult<GroupData> {
        return dataSource.getGroupDetail(groupId)
            .onSuccess { data ->
                logger.debug("Get group detail succeeded: ${data.memberCount} members")
            }
            .onFailure { error ->
                logger.warn("Get group detail failed", error)
            }
    }
    override suspend fun getGroupMembers(groupId: String): AppResult<List<GroupMember>> {
        return dataSource.getGroupMembers(groupId)
    }
    // 🌟 1. อัปเดตข้อมูลกลุ่ม (ชื่อ, รูป)
    override suspend fun updateGroup(groupId: String, groupName: String, profileImage: ByteArray?): AppResult<GroupResult> {
        val result = dataSource.updateGroup(groupId, groupName, profileImage)
            .onSuccess {
                logger.info("Update group succeeded")
            }
            .onFailure { error ->
                logger.warn("Update group failed", error)
            }
        return invalidateAfterSuccess(result)
    }

    // 🌟 2. เพิ่มสมาชิกเข้ากลุ่ม
    override suspend fun addGroupMember(groupId: String, targetId: String): AppResult<Boolean> {
        val result = dataSource.addGroupMember(groupId, targetId)
            .onSuccess {
                logger.info("Add group member succeeded")
            }
            .onFailure { error ->
                logger.warn("Add group member failed", error)
            }
        return invalidateAfterSuccess(result)
    }

    // 🌟 3. ลบสมาชิกออกจากกลุ่ม
    override suspend fun removeGroupMember(groupId: String, targetId: String): AppResult<Boolean> {
        val result = dataSource.removeGroupMember(groupId, targetId)
            .onSuccess {
                logger.info("Remove group member succeeded")
            }
            .onFailure { error ->
                logger.warn("Remove group member failed", error)
            }
        return invalidateAfterSuccess(result)
    }
    override suspend fun leaveGroup(groupId: String): AppResult<Boolean> {
        val result = dataSource.leaveGroup(groupId)
            .onSuccess { logger.info("Leave group succeeded") }
            .onFailure { logger.warn("Leave group failed", it) }
        return invalidateAfterSuccess(result)
    }

    override suspend fun removeFriend(targetId: String): AppResult<Boolean> {
        val result = dataSource.removeFriend(targetId)
            .onSuccess { logger.info("Remove friend succeeded") }
            .onFailure { logger.warn("Remove friend failed", it) }
        return invalidateAfterSuccess(result)
    }
    override suspend fun getItemsToShare(targetId: String, isGroup: Boolean): AppResult<List<ShareableItem>> {
        return dataSource.getItemsToShare(targetId, isGroup)
            .onSuccess { logger.debug("Get shareable items succeeded: ${it.size}") }
            .onFailure { logger.warn("Get shareable items failed", it) }
    }
    override suspend fun submitShareItems(request: ShareItems): AppResult<Boolean> {
        val result = dataSource.submitShareItems(request)
            .onSuccess { logger.info("Submit shared items succeeded") }
            .onFailure { logger.warn("Submit shared items failed", it) }
        return invalidateAfterSuccess(result)
    }
    override suspend fun unShareAsset(sharedItemId: String, isGroup: Boolean): AppResult<Boolean> {
        val result = if (isGroup) {
            dataSource.unShareGroupItem(sharedItemId)
        } else {
            dataSource.unShareFriendItem(sharedItemId)
        }
        return invalidateAfterSuccess(result)
    }
    override suspend fun deleteGroup(groupId: String): AppResult<Boolean> {
        return invalidateAfterSuccess(dataSource.deleteGroup(groupId))
    }

    override suspend fun acceptFriend(request: AcceptFriendRequest): AppResult<AcceptFriendResult> {
        return invalidateAfterSuccess(dataSource.acceptFriend(request))
    }
    override suspend fun getPendingFriends(force: Boolean): AppResult<List<PendingFriend>> {
        return cached(
            key = PENDING_FRIENDS_KEY,
            force = force,
            load = { dataSource.getPendingFriends() },
            decode = { json?.decodeFromString<List<PendingFriendCacheRecord>>(it)?.map(PendingFriendCacheRecord::toDomain) },
            encode = { json?.encodeToString(it.map(PendingFriend::toCacheRecord)) },
            publish = { value, freshness, updatedAt ->
                observedPendingFriends.value = CachedValue(value, freshness, updatedAt)
            },
        )
            .onSuccess { logger.debug("Get pending friends succeeded: ${it.size}") }
            .onFailure { logger.warn("Get pending friends failed", it) }
    }

    private suspend inline fun <reified T> cached(
        key: String,
        force: Boolean,
        crossinline load: suspend () -> AppResult<T>,
        crossinline decode: (String) -> T?,
        crossinline encode: (T) -> String?,
        crossinline publish: (value: T, freshness: CacheFreshness, updatedAtEpochMillis: Long) -> Unit,
    ): AppResult<T> = refreshes.execute(key) {
        cachedInternal(key, force, load, decode, encode, publish)
    }

    private suspend inline fun <reified T> cachedInternal(
        key: String,
        force: Boolean,
        crossinline load: suspend () -> AppResult<T>,
        crossinline decode: (String) -> T?,
        crossinline encode: (T) -> String?,
        crossinline publish: (value: T, freshness: CacheFreshness, updatedAtEpochMillis: Long) -> Unit,
    ): AppResult<T> {
        val now = Clock.System.now().toEpochMilliseconds()
        val cached = try {
            cache?.read(NAMESPACE, key)?.let { entry ->
                runCatching { decode(entry.payload) }.getOrNull()
                    ?.let {
                        CachedSnapshot(
                            it,
                            sanitizeCacheTimestamp(entry.updatedAtEpochMillis, now, SOCIAL_TTL_MILLIS),
                        )
                    }
            }
        } catch (error: Throwable) {
            if (error is kotlinx.coroutines.CancellationException) throw error
            logger.warn("Social cache read failed", error)
            null
        }
        if (!force && cached != null && isCacheFresh(cached.updatedAtEpochMillis, now, SOCIAL_TTL_MILLIS)) {
            publish(cached.value, CacheFreshness.Fresh, cached.updatedAtEpochMillis)
            return AppResult.Success(cached.value)
        }

        // Keep stale content visible while the refresh is in flight. This is the
        // stale-while-revalidate contract used by the other bounded contexts.
        cached?.let { publish(it.value, CacheFreshness.Stale, it.updatedAtEpochMillis) }

        val loaded = load()
        return when (loaded) {
            is AppResult.Success -> {
                val value = loaded.value
                val encoded = runCatching { encode(value) }.getOrNull()
                if (encoded != null) {
                    try {
                        cache?.write(NAMESPACE, key, encoded, now)
                    } catch (error: Throwable) {
                        if (error is kotlinx.coroutines.CancellationException) throw error
                        logger.warn("Social cache write failed", error)
                    }
                }
                publish(value, CacheFreshness.Fresh, now)
                AppResult.Success(value)
            }
            is AppResult.Failure -> cached?.let {
                publish(it.value, CacheFreshness.Offline, it.updatedAtEpochMillis)
                AppResult.Success(it.value)
            } ?: loaded
        }
    }

    private suspend fun invalidateReadCache() {
        try {
            cache?.clearNamespace(NAMESPACE)
        } catch (error: Throwable) {
            if (error is kotlinx.coroutines.CancellationException) throw error
            logger.warn("Social cache clear failed", error)
        } finally {
            // Observers must not continue to expose a snapshot after a
            // successful mutation, even when the persistence layer is
            // temporarily unavailable. The next read can repopulate it.
            observedFriends.value = null
            observedGroups.value = null
            observedPendingFriends.value = null
        }
    }

    private suspend fun <T> invalidateAfterSuccess(result: AppResult<T>): AppResult<T> {
        if (result.isSuccess) invalidateReadCache()
        return result
    }

    private data class CachedSnapshot<T>(val value: T, val updatedAtEpochMillis: Long)

    private companion object {
        const val NAMESPACE = "social"
        const val FRIENDS_KEY = "friends"
        const val GROUPS_KEY = "groups"
        const val PENDING_FRIENDS_KEY = "pending-friends"
        const val SOCIAL_TTL_MILLIS = 5 * 60 * 1_000L
    }


}

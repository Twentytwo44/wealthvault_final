package com.wealthvault.financiallist.data.group

import com.wealthvault.group_api.model.GetAllGroupData

class GroupRepositoryImpl(
    private val networkDataSource: GroupNetworkDataSource,
) {
    suspend fun getAllGroup(): Result<List<GetAllGroupData>> {
        return networkDataSource.getAllGroup()
    }
}

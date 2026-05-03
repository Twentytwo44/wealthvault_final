package com.wealthvault.introduction.data

import com.wealthvault.`user-api`.model.UpdateUserData
import com.wealthvault.`user-api`.model.UpdateUserDataRequest

class IntroRepositoryImpl(
    private val networkDataSource: IntroNetworkDataSource,

) {
    suspend fun updateUser(request: UpdateUserDataRequest): Result<UpdateUserData> {
        return networkDataSource.updateUser(request)
    }



}

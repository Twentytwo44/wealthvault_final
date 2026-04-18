package com.wealthvault.financiallist.data.group

import com.wealthvault.group_api.getgrouplist.GetAllGroupApi
import com.wealthvault.group_api.model.GetAllGroupData


class GroupNetworkDataSource(
    private val groupApi: GetAllGroupApi,
) {
    // เปลี่ยนจาก GetGroupResponse เป็น List<GetAllGroupData>
    suspend fun getAllGroup(): Result<List<GetAllGroupData>> {
        return runCatching {
            val result = groupApi.getAllGroup()
            println("result: $result")
            // ส่ง List กลับไป ถ้า null ก็โยน Error
            result.data ?: throw IllegalArgumentException(result.error)
        }
    }
}

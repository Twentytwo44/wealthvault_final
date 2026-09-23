package com.wealthvault.group_api.di


import com.wealthvault.core.KoinConst
import com.wealthvault.group_api.addmember.AddMemberApi
import com.wealthvault.group_api.addmember.AddMemberApiImpl
import com.wealthvault.group_api.creategroup.CreateGroupApi
import com.wealthvault.group_api.creategroup.CreateGroupApiImpl
import com.wealthvault.group_api.deletegroup.DeleteGroupApi
import com.wealthvault.group_api.deletegroup.DeleteGroupApiImpl
import com.wealthvault.group_api.getgroupdetail.GetGroupApi
import com.wealthvault.group_api.getgroupdetail.GetGroupApiImpl
import com.wealthvault.group_api.getgrouplist.GetAllGroupApi
import com.wealthvault.group_api.getgrouplist.GetAllGroupApiImpl
import com.wealthvault.group_api.getmember.GetGroupMemberApi
import com.wealthvault.group_api.getmember.GetGroupMemberApiImpl
import com.wealthvault.group_api.grantaccess.GrantAccessApi
import com.wealthvault.group_api.grantaccess.GrantAccessApiImpl
import com.wealthvault.group_api.groupmsg.GetGroupMsgApi
import com.wealthvault.group_api.groupmsg.GetGroupMsgApiImpl
import com.wealthvault.group_api.leavegroup.LeaveGroupApi
import com.wealthvault.group_api.leavegroup.LeaveGroupApiImpl
import com.wealthvault.group_api.removemember.RemoveMemberApi
import com.wealthvault.group_api.removemember.RemoveMemberApiImpl
import com.wealthvault.group_api.updategroup.UpdateGroupApi
import com.wealthvault.group_api.updategroup.UpdateGroupApiImpl
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module

object GroupApiModule {
    val allModules = module {
        single<Json>(named(KoinConst.KotlinSerialization.GLOBAL)) {
            Json {
                ignoreUnknownKeys = true
            }
        }

        single<AddMemberApi> { AddMemberApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<CreateGroupApi> { CreateGroupApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<GetAllGroupApi> { GetAllGroupApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<GetGroupApi> { GetGroupApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<GetGroupMemberApi> { GetGroupMemberApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<GetGroupMsgApi> { GetGroupMsgApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<UpdateGroupApi> { UpdateGroupApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<GrantAccessApi> { GrantAccessApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<RemoveMemberApi> { RemoveMemberApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<LeaveGroupApi> { LeaveGroupApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }

        single<DeleteGroupApi> { DeleteGroupApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
    }

    fun single(definition: Any) {}


}

package com.wealthvault.data.social.group.transport.di


import com.wealthvault.core.KoinConst
import com.wealthvault.data.social.group.transport.addmember.AddMemberApi
import com.wealthvault.data.social.group.transport.addmember.AddMemberApiImpl
import com.wealthvault.data.social.group.transport.creategroup.CreateGroupApi
import com.wealthvault.data.social.group.transport.creategroup.CreateGroupApiImpl
import com.wealthvault.data.social.group.transport.deletegroup.DeleteGroupApi
import com.wealthvault.data.social.group.transport.deletegroup.DeleteGroupApiImpl
import com.wealthvault.data.social.group.transport.getgroupdetail.GetGroupApi
import com.wealthvault.data.social.group.transport.getgroupdetail.GetGroupApiImpl
import com.wealthvault.data.social.group.transport.getgrouplist.GetAllGroupApi
import com.wealthvault.data.social.group.transport.getgrouplist.GetAllGroupApiImpl
import com.wealthvault.data.social.group.transport.getmember.GetGroupMemberApi
import com.wealthvault.data.social.group.transport.getmember.GetGroupMemberApiImpl
import com.wealthvault.data.social.group.transport.grantaccess.GrantAccessApi
import com.wealthvault.data.social.group.transport.grantaccess.GrantAccessApiImpl
import com.wealthvault.data.social.group.transport.groupmsg.GetGroupMsgApi
import com.wealthvault.data.social.group.transport.groupmsg.GetGroupMsgApiImpl
import com.wealthvault.data.social.group.transport.leavegroup.LeaveGroupApi
import com.wealthvault.data.social.group.transport.leavegroup.LeaveGroupApiImpl
import com.wealthvault.data.social.group.transport.removemember.RemoveMemberApi
import com.wealthvault.data.social.group.transport.removemember.RemoveMemberApiImpl
import com.wealthvault.data.social.group.transport.updategroup.UpdateGroupApi
import com.wealthvault.data.social.group.transport.updategroup.UpdateGroupApiImpl
import io.ktor.client.HttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module

object GroupApiModule {
    val allModules = module {
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

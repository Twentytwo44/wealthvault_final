package com.example.account_api.deleteaccount

import com.example.account_api.model.DeleteAccountResponse
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.Path

interface DeleteAccountApi {
    @DELETE("ic_nav_asset/account/{id}")
    suspend fun deleteAccount(@Path("id") id: String): DeleteAccountResponse
}

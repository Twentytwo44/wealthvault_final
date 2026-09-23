package com.wealthvault.google_auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.wealthvault.core.observability.platformLogger


class GoogleAuthAndroid(
    private val context: Context
) : GoogleAuth {

    override suspend fun signIn(): GoogleUser? {

        return try {

            val googleIdOption = GetGoogleIdOption.Builder()
                .setServerClientId(
                    "130348752829-b63e0fvmhv4ma7n1u4ib6ugj1gl9d858.apps.googleusercontent.com"
                )
                .setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val credentialManager = CredentialManager.create(context)

            val result = credentialManager.getCredential(
                request = request,
                context = context
            )

            val credential = result.credential

            if (
                credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {

                val googleCredential =
                    GoogleIdTokenCredential.createFrom(credential.data)

                return GoogleUser(
                    idToken = googleCredential.idToken,
                    accessToken = null,
                    email = googleCredential.id,
                    displayName = googleCredential.displayName,
                    photoUrl = googleCredential.profilePictureUri?.toString(),
                    userId = googleCredential.id
                )
            }

            null

        } catch (e: Exception) {
            platformLogger().warn("Google credential request failed", e)

            null
        }
    }
}

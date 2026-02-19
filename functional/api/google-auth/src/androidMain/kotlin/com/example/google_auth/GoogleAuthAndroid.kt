package com.example.google_auth

import android.content.Context
import android.util.Base64
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential


class GoogleAuthAndroid(
    private val context: Context
) : GoogleAuth {

    override suspend fun signIn(): GoogleUser? {

        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId("130348752829-b63e0fvmhv4ma7n1u4ib6ugj1gl9d858.apps.googleusercontent.com")
            .setFilterByAuthorizedAccounts(false)
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
        println("credential: $credential")
        if (credential is CustomCredential) {

            // ดู type ก่อน
            println("TYPE: ${credential.type}")

            // ดึงข้อมูลทั้งหมด (Bundle)
            val bundle = credential.data

            for (key in bundle.keySet()) {
                println("KEY: $key")
                println("VALUE: ${bundle.get(key)}")
            }
        }
        if (credential is GoogleIdTokenCredential) {
            return GoogleUser(
                idToken = credential.idToken,
                accessToken = null,
                email = null,
                displayName = credential.displayName,
                photoUrl = credential.profilePictureUri?.toString(),
                userId = credential.id
            )
        }
        val googleCredential =
            GoogleIdTokenCredential.createFrom(credential.data)

        println("TOKEN LENGTH: ${googleCredential.idToken?.length}")
        println("TOKEN: ${googleCredential.idToken}")

        val json = decodeJwt(googleCredential.idToken)
        println(json)

        return null
    }
    fun decodeJwt(token: String): String {
        val parts = token.split(".")





        val header = String(Base64.decode(parts[0], Base64.URL_SAFE))
        val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
        val signature = parts[2]

        println("HEADER:\n$header")
        println("PAYLOAD:\n$payload")
        println("SIGNATURE:\n$signature")

        return ""
    }

}

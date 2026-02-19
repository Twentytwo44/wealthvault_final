@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package com.example.google_auth

import cocoapods.GoogleSignIn.GIDConfiguration
import cocoapods.GoogleSignIn.GIDSignIn
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.UIKit.UIViewController
import kotlin.coroutines.resume

class GoogleAuthIOS(
    private val controller: UIViewController
) : GoogleAuth {

    override suspend fun signIn(): GoogleUser? =
        suspendCancellableCoroutine { continuation ->

            GIDSignIn.sharedInstance.configuration =
                GIDConfiguration(
                    clientID = "130348752829-7jmmd7hsc8tcngdmjcvj51r5af1g4np8.apps.googleusercontent.com"
                )

            GIDSignIn.sharedInstance.signInWithPresentingViewController(
                controller
            ) { result, error ->

                if (result != null) {
                    continuation.resume(
                        GoogleUser(
                            idToken = result.user.idToken?.tokenString ?: "",
                            accessToken = result.user.accessToken.tokenString,
                            email = result.user.profile?.email,
                            displayName = result.user.profile?.name,
                            photoUrl = result.user.profile?.imageURLWithDimension(300u)?.absoluteString,
                            userId = result.user.userID ?: ""
                        )
                    )
                } else {
                    continuation.resume(null)
                }
            }
        }
}

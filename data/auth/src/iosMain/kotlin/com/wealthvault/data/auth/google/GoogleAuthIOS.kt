@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package com.wealthvault.data.auth.google

import cocoapods.GoogleSignIn.GIDConfiguration
import cocoapods.GoogleSignIn.GIDSignIn
import com.wealthvault.domain.auth.GoogleIdentity
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.UIKit.UIViewController
import kotlin.coroutines.resume

internal class GoogleAuthIOS(
    private val controller: UIViewController
) : GoogleAuth {

    override suspend fun signIn(): GoogleIdentity? =
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
                        GoogleIdentity(
                            idToken = result.user.idToken?.tokenString ?: "",
                        )
                    )
                } else {
                    continuation.resume(null)
                }
            }
        }
}

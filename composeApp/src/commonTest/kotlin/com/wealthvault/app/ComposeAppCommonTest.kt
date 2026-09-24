package com.wealthvault.app

import kotlin.test.Test
import kotlin.test.assertEquals
import com.wealthvault.app.navigation.AuthenticatedRoute
import com.wealthvault.app.navigation.authenticatedRoute
import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.navigation.SharedScreen
import com.wealthvault.domain.profile.UserData

class ComposeAppCommonTest {

    @Test
    fun typedNavigationDestinationsRemainDistinct() {
        val destinations = listOf(
            SharedScreen.Login,
            SharedScreen.Register,
            SharedScreen.Main,
            SharedScreen.DashboardTab,
            SharedScreen.ProfileTab,
            SharedScreen.AssetTab,
            SharedScreen.DebtTab,
            SharedScreen.SocialTab,
            SharedScreen.Notification,
        )

        assertEquals(destinations.size, destinations.distinct().size)
        assertEquals(SharedScreen.Login, destinations.first())
        assertEquals(SharedScreen.Notification, destinations.last())
    }

    @Test
    fun authenticatedRoutingKeepsProfileAndTransientNetworkBranchesExplicit() {
        assertEquals(
            AuthenticatedRoute.Intro,
            authenticatedRoute(AppResult.Success(UserData(birthday = null))),
        )
        assertEquals(
            AuthenticatedRoute.Intro,
            authenticatedRoute(AppResult.Success(UserData(birthday = "1970-01-01"))),
        )
        assertEquals(
            AuthenticatedRoute.Main,
            authenticatedRoute(AppResult.Success(UserData(birthday = "1990-01-01"))),
        )
        assertEquals(
            AuthenticatedRoute.Login,
            authenticatedRoute(AppResult.Failure(AppError.Unauthorized)),
        )
        assertEquals(
            AuthenticatedRoute.Main,
            authenticatedRoute(AppResult.Failure(AppError.Network(IllegalStateException("offline")))),
        )
    }
}

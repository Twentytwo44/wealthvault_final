package com.wealthvault.push.di

import org.koin.core.module.Module

/**
 * Platform-specific push registration is exposed through one common Koin
 * contract so the composition root can assemble the same graph on Android
 * and iOS.
 */
expect val pushNotificationModule: Module

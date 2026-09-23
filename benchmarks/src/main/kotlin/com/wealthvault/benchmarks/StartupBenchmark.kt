package com.wealthvault.benchmarks

import android.content.ComponentName
import android.content.Intent
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.MemoryUsageMetric
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalMetricApi::class)
class StartupBenchmark {
    private companion object {
        // Keep this aligned with androidApp.applicationId. The Kotlin/Android
        // namespace is intentionally different from the installed package so
        // existing installs and deep links remain compatible.
        const val APPLICATION_ID = "com.wealthvault.wealthvault_final"
    }

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun coldStartup() = benchmarkRule.measureRepeated(
        packageName = APPLICATION_ID,
        metrics = listOf(
            StartupTimingMetric(),
            FrameTimingMetric(),
            MemoryUsageMetric(MemoryUsageMetric.Mode.Max),
        ),
        iterations = 5,
        startupMode = StartupMode.COLD,
        setupBlock = { pressHome() },
    ) {
        startActivityAndWait()
    }

    @Test
    fun warmStartup() = benchmarkRule.measureRepeated(
        packageName = APPLICATION_ID,
        metrics = listOf(
            StartupTimingMetric(),
            FrameTimingMetric(),
            MemoryUsageMetric(MemoryUsageMetric.Mode.Max),
        ),
        iterations = 5,
        startupMode = StartupMode.WARM,
        setupBlock = { pressHome() },
    ) {
        startActivityAndWait()
    }

    @Test
    fun dashboardScrolling() = benchmarkRule.measureRepeated(
        packageName = APPLICATION_ID,
        metrics = listOf(
            FrameTimingMetric(),
            MemoryUsageMetric(MemoryUsageMetric.Mode.Max),
        ),
        iterations = 5,
        startupMode = StartupMode.COLD,
        setupBlock = { pressHome() },
    ) {
        val dashboardIntent = Intent().setComponent(
            ComponentName(APPLICATION_ID, "com.wealthvault.app.BenchmarkDashboardActivity"),
        )
        startActivityAndWait(dashboardIntent)
        device.waitForIdle()

        val centerX = device.displayWidth / 2
        val bottomY = (device.displayHeight * 0.86f).toInt()
        val topY = (device.displayHeight * 0.20f).toInt()
        repeat(10) {
            device.swipe(centerX, bottomY, centerX, topY, 18)
        }
    }
}

@RunWith(AndroidJUnit4::class)
class BaselineProfileBenchmark {
    private companion object {
        const val APPLICATION_ID = "com.wealthvault.wealthvault_final"
    }

    @get:Rule
    val profileRule = BaselineProfileRule()

    @Test
    fun startupAndPrimaryNavigationProfile() = profileRule.collect(
        packageName = APPLICATION_ID,
        maxIterations = 5,
        stableIterations = 2,
    ) {
        pressHome()
        startActivityAndWait()
    }
}

package com.wealthvault.app

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

/**
 * Deterministic dashboard content for startup and scroll tests.
 *
 * This activity exists only in the benchmark variant. Keeping the data local
 * makes the measurement independent from authentication, network availability,
 * and backend data shape. It intentionally uses platform views so the Android
 * benchmark target does not add a feature-to-launcher dependency.
 */
class BenchmarkDashboardActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val density = resources.displayMetrics.density
        val scroll = ScrollView(this)
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16, density), dp(16, density), dp(16, density), dp(16, density))
        }
        repeat(240) { index ->
            content.addView(TextView(this).apply {
                text = "Benchmark asset $index    ฿${100_000 + index}"
                textSize = 16f
                setTextColor(Color.DKGRAY)
                gravity = Gravity.CENTER_VERTICAL
                setPadding(0, dp(8, density), 0, dp(8, density))
                contentDescription = "benchmark-asset-$index"
            })
        }
        scroll.addView(content)
        setContentView(scroll)
    }

    private fun dp(value: Int, density: Float): Int = (value * density).toInt()
}

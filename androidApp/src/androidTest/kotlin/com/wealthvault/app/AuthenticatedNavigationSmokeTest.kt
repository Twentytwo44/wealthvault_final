package com.wealthvault.app

import android.os.SystemClock
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.wealthvault.domain.auth.SessionTokenStore
import com.wealthvault.domain.auth.SessionTokens
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext

/**
 * Verifies the process-level session coordinator, not just that the activity
 * can be created. A seeded session must reach the authenticated shell and a
 * subsequent clear must route back to login without recreating the process.
 *
 * The test uses a deterministic in-app token and never calls the backend. The
 * dashboard may show its loading state when the emulator is offline; the
 * authenticated shell is still identifiable by its notification action.
 */
@RunWith(AndroidJUnit4::class)
class AuthenticatedNavigationSmokeTest {
    private lateinit var sessionStore: SessionTokenStore
    private lateinit var device: UiDevice

    @Before
    fun setUp() {
        sessionStore = GlobalContext.get().get()
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        runBlocking {
            sessionStore.saveTokens(
                SessionTokens(
                    accessToken = "instrumentation-access",
                    refreshToken = "instrumentation-refresh",
                ),
            )
        }
    }

    @After
    fun tearDown() {
        runBlocking { sessionStore.clearTokens() }
    }

    @Test
    fun authenticatedSessionReachesMainShellAndLogoutReturnsToLogin() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            assertTrue(
                "Authenticated shell did not appear",
                device.wait(Until.hasObject(By.desc("Notifications")), 8_000),
            )

            device.findObject(By.desc("Notifications")).click()
            assertTrue(
                "Notification destination did not render",
                device.wait(Until.hasObject(By.text("การแจ้งเตือน")), 4_000),
            )
            device.findObject(By.desc("Back")).click()
            assertTrue(device.wait(Until.hasObject(By.desc("Notifications")), 4_000))

            assertBottomTabOpens("ทรัพย์สิน", "เพิ่มทรัพย์สิน")
            device.findObject(By.desc("เพิ่มทรัพย์สิน")).click()
            assertTrue(device.wait(Until.hasObject(By.text("ประเภท")), 4_000))
            device.findObject(By.text("เงินสด ทองคำ")).click()
            device.findObject(By.text("ต่อไป")).click()
            assertTrue(device.wait(Until.hasObject(By.text("ข้อมูลเงินสด ทองคำ")), 4_000))
            device.pressBack()
            device.pressBack()
            assertTrue(device.wait(Until.hasObject(By.desc("เพิ่มทรัพย์สิน")), 4_000))
            assertBottomTabOpens("หนี้สิน", "เพิ่มหนี้สิน")
            device.findObject(By.desc("เพิ่มหนี้สิน")).click()
            assertTrue(device.wait(Until.hasObject(By.text("ประเภท")), 4_000))
            device.findObject(By.text("หนี้สิน")).click()
            device.findObject(By.text("ต่อไป")).click()
            assertTrue(device.wait(Until.hasObject(By.text("ข้อมูลหนี้สิน")), 4_000))
            device.pressBack()
            device.pressBack()
            assertTrue(device.wait(Until.hasObject(By.desc("เพิ่มหนี้สิน")), 4_000))
            assertBottomTabOpens("โซเชียล", "Add Friend")
            assertBottomTabOpens("โปรไฟล์", "Settings")
            val dashboardTab = device.findObject(By.text("หน้าหลัก"))
            assertNotNull("Dashboard tab did not become selectable again", dashboardTab)
            dashboardTab.click()
            assertTrue(device.wait(Until.hasObject(By.desc("Notifications")), 4_000))

            runBlocking { sessionStore.clearTokens() }
            waitForLoginShell()

            assertNotNull(device.findObject(By.text("เข้าสู่ระบบ")))
            device.findObject(By.text("สร้างบัญชี?")).click()
            assertTrue(device.wait(Until.hasObject(By.text("สร้างบัญชี")), 4_000))
            device.pressBack()
            assertTrue(device.wait(Until.hasObject(By.text("ลืมรหัสผ่าน")), 4_000))
            device.findObject(By.text("ลืมรหัสผ่าน")).click()
            assertTrue(device.wait(Until.hasObject(By.text("ลืมรหัสผ่าน?")), 4_000))
            device.pressBack()
            assertTrue(device.wait(Until.hasObject(By.text("เข้าสู่ระบบ")), 4_000))
            assertTrue(scenario.state.isAtLeast(androidx.lifecycle.Lifecycle.State.RESUMED))
        }
    }

    private fun waitForLoginShell() {
        val deadline = SystemClock.uptimeMillis() + 8_000
        while (SystemClock.uptimeMillis() < deadline) {
            if (device.findObject(By.text("เข้าสู่ระบบ")) != null) return
            SystemClock.sleep(100)
        }
    }

    private fun assertBottomTabOpens(label: String, expectedContentDescription: String) {
        val tab = device.findObject(By.text(label))
        assertNotNull("Bottom tab '$label' is missing", tab)
        tab.click()
        assertTrue(
            "Tab '$label' did not render its destination",
            device.wait(Until.hasObject(By.desc(expectedContentDescription)), 4_000),
        )
    }
}

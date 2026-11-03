package com.arnafoundation.app

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test for ARNA GRAM UNNAYA FOUNDATION.
 *
 * This test runs on an Android device or emulator
 * and verifies that the installed application package
 * matches the expected ARNA application ID.
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry
            .getInstrumentation()
            .targetContext

        assertEquals(
            BuildConfig.APPLICATION_ID,
            appContext.packageName
        )
    }
}
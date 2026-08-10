package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Robolectric tests — run on JVM with a simulated Android environment.
 * Used for tests that need a Context but not a real device/emulator.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `app name string resource is correct`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        assertEquals("JO Social Hub", context.getString(R.string.app_name))
    }

    @Test
    fun `application context is not null`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        assertNotNull(context)
    }

    @Test
    fun `application package name matches build config`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // namespace is com.example as declared in build.gradle.kts
        assertEquals("com.example", context.packageName)
    }
}

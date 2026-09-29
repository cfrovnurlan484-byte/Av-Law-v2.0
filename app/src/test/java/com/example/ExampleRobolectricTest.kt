package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.LegalDatabaseSeeder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Av-Law", appName)
  }

  @Test
  fun `verify legal database seeder loads azerbaijani legal content`() {
    val sources = LegalDatabaseSeeder.getInitialLegalSources()
    assertTrue(sources.isNotEmpty())
    assertTrue(sources.any { it.codeCategory == "Mülki Məcəllə" })
    assertTrue(sources.any { it.codeCategory == "Cinayət Məcəlləsi" })
    assertTrue(sources.any { it.codeCategory == "AR Konstitusiyası" })

    val profile = LegalDatabaseSeeder.getInitialProfile()
    assertEquals("Əli Məmmədov", profile.fullName)
    assertTrue(profile.totalPoints > 0)
  }
}

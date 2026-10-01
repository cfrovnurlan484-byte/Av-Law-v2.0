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
    assertEquals("Hüquqşünas", profile.fullName)
    assertEquals(0, profile.totalPoints)
    assertEquals(0, profile.theoryChecksCompleted)
  }

  @Test
  fun `verify CaseStudy Room entity fields and initial case studies`() {
    val caseStudy = com.example.data.local.model.CaseStudy(
      id = 1L,
      title = "Mülki Hüquq Kazusu",
      category = "Mülki Hüquq",
      description = "Mənzil alqı-satqı mübahisəsi və etibarsızlıq",
      status = "ACTIVE"
    )
    assertEquals("Mülki Hüquq Kazusu", caseStudy.title)
    assertEquals("Mülki Hüquq", caseStudy.category)
    assertEquals("Mənzil alqı-satqı mübahisəsi və etibarsızlıq", caseStudy.description)
    assertEquals("ACTIVE", caseStudy.status)

    val seededCases = LegalDatabaseSeeder.getInitialCaseStudies()
    assertTrue(seededCases.isNotEmpty())
    assertTrue(seededCases.all { it.title.isNotBlank() })
    assertTrue(seededCases.all { it.category.isNotBlank() })
    assertTrue(seededCases.all { it.description.isNotBlank() })
    assertTrue(seededCases.all { it.status.isNotBlank() })
  }
}

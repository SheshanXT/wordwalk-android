package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.WordWalkDatabase
import com.example.data.local.entities.JournalEntryEntity
import com.example.data.local.entities.UserPreferencesEntity
import com.example.data.plan.PlanGenerator
import com.example.data.repository.WordWalkRepository
import com.example.localization.LocaleStrings
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class WordWalkTest {

    private lateinit var db: WordWalkDatabase
    private lateinit var repository: WordWalkRepository

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, WordWalkDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = WordWalkRepository(db)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun testPlanGenerationAndCalculatedDates() {
        val startMillis = 1700000000000L // arbitrary fixed date
        val planWithSessions = PlanGenerator.generatePlan(
            bibleExperience = "beginner",
            spiritualGoal = "habit",
            minutesPerSession = 10,
            studyDaysPerWeek = 5,
            planStyle = "john",
            startDateMillis = startMillis
        )

        val plan = planWithSessions.plan
        val sessions = planWithSessions.sessions

        assertTrue("Plan should have sessions", sessions.isNotEmpty())
        assertEquals(sessions.size, plan.totalSessions)
        assertTrue(plan.titleEn.contains("John"))
        assertTrue(plan.titleSi.contains("යොහන්"))

        // Verify first session is John 1:1-18
        val firstSession = sessions.first()
        assertEquals("John 1:1-18", firstSession.bookReferenceEn)
        assertEquals("යොහන් 1:1-18", firstSession.bookReferenceSi)
        assertFalse(firstSession.isCatchUpGraceDay)

        // Verify Grace & Catch-up days are injected
        val graceDays = sessions.filter { it.isCatchUpGraceDay }
        assertTrue("Grace days should be included in curriculum", graceDays.isNotEmpty())
        assertTrue(graceDays.first().bookReferenceEn.contains("Grace"))
        assertTrue(graceDays.first().bookReferenceSi.contains("කරුණාවේ"))

        // Verify estimated completion date is strictly in the future and mathematically calculated
        assertTrue(plan.estimatedCompletionDateMillis > startMillis)
    }

    @Test
    fun testOnboardingResumePersistence() = runBlocking {
        // Step 1: save progress at step 3
        repository.saveOnboardingProgress(
            step = 3,
            language = LocaleStrings.LANG_SI,
            experience = "beginner",
            goals = "peace",
            minutes = 15
        )

        val savedPrefs = repository.userPreferences.first()
        assertNotNull(savedPrefs)
        assertEquals(3, savedPrefs?.questionnaireStep)
        assertEquals(LocaleStrings.LANG_SI, savedPrefs?.preferredLanguage)
        assertEquals("beginner", savedPrefs?.bibleExperience)
        assertEquals("peace", savedPrefs?.spiritualGoal)
        assertEquals(15, savedPrefs?.minutesPerSession)
        assertFalse(savedPrefs?.onboardingCompleted ?: true)

        // Step 2: Complete onboarding and verify plan was generated & persisted
        val plan = repository.completeOnboardingAndGeneratePlan(
            language = LocaleStrings.LANG_SI,
            experience = "beginner",
            goals = "peace",
            minutes = 15,
            days = 6,
            reminderTime = "07:00 AM",
            challenge = "time",
            style = "john"
        )

        assertNotNull(plan)
        val activePlanFromDb = repository.activePlan.first()
        assertNotNull(activePlanFromDb)
        assertEquals(plan.id, activePlanFromDb?.id)

        val sessionsFromDb = repository.getSessionsForPlan(plan.id).first()
        assertTrue(sessionsFromDb.isNotEmpty())
    }

    @Test
    fun testCompleteSessionAndJournalPersistence() = runBlocking {
        val plan = repository.completeOnboardingAndGeneratePlan(
            language = LocaleStrings.LANG_EN,
            experience = "beginner",
            goals = "habit",
            minutes = 10,
            days = 5,
            reminderTime = "07:00 AM",
            challenge = "time",
            style = "john"
        )

        val sessions = repository.getSessionsForPlan(plan.id).first()
        val firstSession = sessions.first()
        assertFalse(firstSession.isCompleted)

        // Complete the first session with user journal notes
        repository.completeStudySession(
            session = firstSession,
            journalLearned = "Light shines in the darkness and cannot be defeated.",
            journalSurrender = "Letting go of comparison today.",
            journalPrayer = "Lord, grant me peaceful focus today.",
            journalReflection = "A great start to the morning."
        )

        // Verify session is now marked completed
        val updatedSession = repository.getSessionByNumber(plan.id, firstSession.sessionNumber).first()
        assertTrue(updatedSession?.isCompleted == true)
        assertNotNull(updatedSession?.completedAtMillis)

        // Verify journal entry persisted
        val journal = repository.getJournalForSession(firstSession.id).first()
        assertNotNull(journal)
        assertEquals("Light shines in the darkness and cannot be defeated.", journal?.whatILearned)
        assertEquals("Lord, grant me peaceful focus today.", journal?.prayerGratitude)

        // Verify streak updated
        val prefs = repository.userPreferences.first()
        assertEquals(1, prefs?.currentStreak)
        assertEquals(1, prefs?.totalSessionsFinished)
    }

    @Test
    fun testGraceDayNonShamingRecovery() = runBlocking {
        repository.completeOnboardingAndGeneratePlan(
            language = LocaleStrings.LANG_EN,
            experience = "beginner",
            goals = "habit",
            minutes = 10,
            days = 5,
            reminderTime = "07:00 AM",
            challenge = "time",
            style = "john"
        )

        // Take a grace day
        repository.takeGraceCatchUpDay()

        val prefs = repository.userPreferences.first()
        assertEquals(1, prefs?.graceDaysUsed)
        assertTrue(prefs?.lastCompletedDateMillis ?: 0L > 0L)
    }

    @Test
    fun testBilingualStringCompleteness() {
        val testKeys = listOf(
            "app_title", "nav_home", "nav_study", "nav_plan", "nav_progress", "nav_settings",
            "btn_start_walk", "dash_greeting_morning", "study_complete_dialog_title",
            "settings_reminder_toggle", "plan_grace_note"
        )

        for (k in testKeys) {
            val en = LocaleStrings.get(k, LocaleStrings.LANG_EN)
            val si = LocaleStrings.get(k, LocaleStrings.LANG_SI)
            assertNotNull("English string missing for $k", en)
            assertNotNull("Sinhala string missing for $k", si)
            assertTrue("English string shouldn't be empty for $k", en.isNotBlank())
            assertTrue("Sinhala string shouldn't be empty for $k", si.isNotBlank())
            assertTrue("Sinhala string shouldn't equal English fallback for $k", en != si)
        }
    }
}

package com.example.data.repository

import android.content.Context
import com.example.data.local.WordWalkDatabase
import com.example.data.local.entities.JournalEntryEntity
import com.example.data.local.entities.PlanEntity
import com.example.data.local.entities.StudySessionEntity
import com.example.data.local.entities.UserPreferencesEntity
import com.example.data.plan.PlanGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WordWalkRepository(private val database: WordWalkDatabase) {

    private val userPrefsDao = database.userPreferencesDao()
    private val planDao = database.planDao()
    private val sessionDao = database.studySessionDao()
    private val journalDao = database.journalEntryDao()

    val userPreferences: Flow<UserPreferencesEntity?> = userPrefsDao.getUserPreferences()
    val activePlan: Flow<PlanEntity?> = planDao.getActivePlan()
    val allJournals: Flow<List<JournalEntryEntity>> = journalDao.getAllJournals()

    fun getSessionsForPlan(planId: String): Flow<List<StudySessionEntity>> {
        return sessionDao.getSessionsForPlan(planId)
    }

    fun getSessionByNumber(planId: String, sessionNumber: Int): Flow<StudySessionEntity?> {
        return sessionDao.getSessionByNumber(planId, sessionNumber)
    }

    fun getJournalForSession(sessionId: Long): Flow<JournalEntryEntity?> {
        return journalDao.getJournalForSession(sessionId)
    }

    suspend fun saveOnboardingProgress(
        step: Int,
        language: String? = null,
        experience: String? = null,
        goals: String? = null,
        minutes: Int? = null,
        days: Int? = null,
        reminderTime: String? = null,
        challenge: String? = null,
        style: String? = null
    ) = withContext(Dispatchers.IO) {
        val current = userPrefsDao.getUserPreferencesSync() ?: UserPreferencesEntity()
        val updated = current.copy(
            questionnaireStep = step,
            preferredLanguage = language ?: current.preferredLanguage,
            bibleExperience = experience ?: current.bibleExperience,
            spiritualGoal = goals ?: current.spiritualGoal,
            minutesPerSession = minutes ?: current.minutesPerSession,
            studyDaysPerWeek = days ?: current.studyDaysPerWeek,
            reminderTime = reminderTime ?: current.reminderTime,
            biggestChallenge = challenge ?: current.biggestChallenge,
            desiredPlanStyle = style ?: current.desiredPlanStyle,
            updatedAt = System.currentTimeMillis()
        )
        userPrefsDao.insertOrUpdate(updated)
    }

    suspend fun completeOnboardingAndGeneratePlan(
        language: String,
        experience: String,
        goals: String,
        minutes: Int,
        days: Int,
        reminderTime: String,
        challenge: String,
        style: String
    ): PlanEntity = withContext(Dispatchers.IO) {
        // Parse reminder hour/min
        val (hour, minute) = parseReminderTime(reminderTime)

        // 1. Generate plan & sessions
        val (plan, sessions) = PlanGenerator.generatePlan(
            bibleExperience = experience,
            spiritualGoal = goals,
            minutesPerSession = minutes,
            studyDaysPerWeek = days,
            planStyle = style
        )

        // 2. Persist in database
        planDao.deleteAllPlans()
        sessionDao.deleteAllSessions()

        planDao.insertPlan(plan)
        sessionDao.insertSessions(sessions)

        val updatedPrefs = UserPreferencesEntity(
            id = 1,
            preferredLanguage = language,
            bibleExperience = experience,
            spiritualGoal = goals,
            minutesPerSession = minutes,
            studyDaysPerWeek = days,
            reminderTime = reminderTime,
            reminderHour = hour,
            reminderMinute = minute,
            reminderEnabled = true,
            biggestChallenge = challenge,
            desiredPlanStyle = style,
            onboardingCompleted = true,
            questionnaireStep = 8,
            currentStreak = 0,
            lastCompletedDateMillis = 0L,
            totalSessionsFinished = 0,
            graceDaysUsed = 0,
            updatedAt = System.currentTimeMillis()
        )
        userPrefsDao.insertOrUpdate(updatedPrefs)

        plan
    }

    suspend fun completeStudySession(
        session: StudySessionEntity,
        journalLearned: String,
        journalSurrender: String,
        journalPrayer: String,
        journalReflection: String
    ) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()

        // 1. Mark session complete
        val updatedSession = session.copy(
            isCompleted = true,
            completedAtMillis = now
        )
        sessionDao.updateSession(updatedSession)

        // 2. Save journal entry
        val existingJournal = journalDao.getJournalForSessionSync(session.id)
        val journalEntity = (existingJournal ?: JournalEntryEntity(
            sessionId = session.id,
            sessionNumber = session.sessionNumber,
            scriptureReference = session.bookReferenceEn
        )).copy(
            dateMillis = now,
            whatILearned = journalLearned,
            whatSurrendered = journalSurrender,
            prayerGratitude = journalPrayer,
            personalReflection = journalReflection
        )
        journalDao.insertOrUpdateJournal(journalEntity)

        // 3. Update active plan current session index
        val plan = planDao.getPlanById(session.planId)
        if (plan != null) {
            val nextIndex = (session.sessionNumber + 1).coerceAtMost(plan.totalSessions)
            val isPlanCompleted = session.sessionNumber >= plan.totalSessions
            val updatedPlan = plan.copy(
                currentSessionIndex = nextIndex,
                status = if (isPlanCompleted) "COMPLETED" else plan.status
            )
            planDao.updatePlan(updatedPlan)
        }

        // 4. Update streak and stats
        val prefs = userPrefsDao.getUserPreferencesSync() ?: UserPreferencesEntity()
        val isConsecutiveDay = isNextDay(prefs.lastCompletedDateMillis, now)
        val isSameDay = isSameCalendarDay(prefs.lastCompletedDateMillis, now)

        val newStreak = when {
            isSameDay -> prefs.currentStreak.coerceAtLeast(1)
            isConsecutiveDay -> prefs.currentStreak + 1
            else -> 1 // Non-punitive restart: starts immediately at 1
        }

        val updatedPrefs = prefs.copy(
            currentStreak = newStreak,
            lastCompletedDateMillis = now,
            totalSessionsFinished = prefs.totalSessionsFinished + 1,
            updatedAt = now
        )
        userPrefsDao.insertOrUpdate(updatedPrefs)
    }

    suspend fun takeGraceCatchUpDay() = withContext(Dispatchers.IO) {
        val prefs = userPrefsDao.getUserPreferencesSync() ?: return@withContext
        // Grace day preserves current streak and increments grace count!
        val updated = prefs.copy(
            graceDaysUsed = prefs.graceDaysUsed + 1,
            lastCompletedDateMillis = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        userPrefsDao.insertOrUpdate(updated)
    }

    suspend fun setLanguage(lang: String) = withContext(Dispatchers.IO) {
        val current = userPrefsDao.getUserPreferencesSync() ?: UserPreferencesEntity()
        userPrefsDao.insertOrUpdate(current.copy(preferredLanguage = lang, updatedAt = System.currentTimeMillis()))
    }

    suspend fun updateReminderSettings(enabled: Boolean, timeLabel: String, hour: Int, minute: Int) = withContext(Dispatchers.IO) {
        val current = userPrefsDao.getUserPreferencesSync() ?: UserPreferencesEntity()
        userPrefsDao.insertOrUpdate(
            current.copy(
                reminderEnabled = enabled,
                reminderTime = timeLabel,
                reminderHour = hour,
                reminderMinute = minute,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun updatePlanPaceAndDays(minutes: Int, daysPerWeek: Int) = withContext(Dispatchers.IO) {
        val active = planDao.getActivePlan().firstOrNull() ?: return@withContext
        val newCompletionDate = PlanGenerator.calculateEstimatedCompletionDate(
            startDateMillis = active.startDateMillis,
            totalSessions = active.totalSessions,
            studyDaysPerWeek = daysPerWeek
        )
        val updatedPlan = active.copy(
            minutesPerSession = minutes,
            studyDaysPerWeek = daysPerWeek,
            estimatedCompletionDateMillis = newCompletionDate
        )
        planDao.updatePlan(updatedPlan)

        val currentPrefs = userPrefsDao.getUserPreferencesSync() ?: UserPreferencesEntity()
        userPrefsDao.insertOrUpdate(
            currentPrefs.copy(
                minutesPerSession = minutes,
                studyDaysPerWeek = daysPerWeek,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun setPlanStatus(newStatus: String) = withContext(Dispatchers.IO) {
        val active = planDao.getActivePlan().firstOrNull() ?: return@withContext
        planDao.updatePlan(active.copy(status = newStatus))
    }

    suspend fun resetAllData() = withContext(Dispatchers.IO) {
        journalDao.deleteAllJournals()
        sessionDao.deleteAllSessions()
        planDao.deleteAllPlans()
        userPrefsDao.deleteAll()
    }

    suspend fun exportDataAsJson(): String = withContext(Dispatchers.IO) {
        val prefs = userPrefsDao.getUserPreferencesSync()
        val plan = planDao.getActivePlan().firstOrNull()
        val journals = journalDao.getAllJournals().firstOrNull() ?: emptyList()

        val root = JSONObject()
        root.put("app", "WordWalk")
        root.put("exportedAt", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()))

        val userObj = JSONObject()
        prefs?.let {
            userObj.put("language", it.preferredLanguage)
            userObj.put("streak", it.currentStreak)
            userObj.put("totalSessionsFinished", it.totalSessionsFinished)
            userObj.put("graceDaysUsed", it.graceDaysUsed)
            userObj.put("minutesPerSession", it.minutesPerSession)
            userObj.put("studyDaysPerWeek", it.studyDaysPerWeek)
        }
        root.put("userPreferences", userObj)

        val planObj = JSONObject()
        plan?.let {
            planObj.put("id", it.id)
            planObj.put("title", it.titleEn)
            planObj.put("status", it.status)
            planObj.put("currentSessionIndex", it.currentSessionIndex)
            planObj.put("totalSessions", it.totalSessions)
        }
        root.put("activePlan", planObj)

        val journalArray = JSONArray()
        for (j in journals) {
            val jObj = JSONObject()
            jObj.put("sessionNumber", j.sessionNumber)
            jObj.put("scriptureReference", j.scriptureReference)
            jObj.put("date", SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(j.dateMillis)))
            jObj.put("whatILearned", j.whatILearned)
            jObj.put("whatSurrendered", j.whatSurrendered)
            jObj.put("prayerGratitude", j.prayerGratitude)
            jObj.put("personalReflection", j.personalReflection)
            journalArray.put(jObj)
        }
        root.put("journalEntries", journalArray)

        root.toString(2)
    }

    private fun parseReminderTime(timeStr: String): Pair<Int, Int> {
        return when {
            timeStr.contains("07:00") -> Pair(7, 0)
            timeStr.contains("12:30") -> Pair(12, 30)
            timeStr.contains("08:30") -> Pair(20, 30)
            else -> Pair(8, 0)
        }
    }

    private fun isSameCalendarDay(millis1: Long, millis2: Long): Boolean {
        if (millis1 <= 0L || millis2 <= 0L) return false
        val fmt = SimpleDateFormat("yyyyMMdd", Locale.US)
        return fmt.format(Date(millis1)) == fmt.format(Date(millis2))
    }

    private fun isNextDay(prevMillis: Long, nowMillis: Long): Boolean {
        if (prevMillis <= 0L) return false
        val diff = nowMillis - prevMillis
        val oneDay = 24 * 60 * 60 * 1000L
        return diff in (12 * 60 * 60 * 1000L)..(48 * 60 * 60 * 1000L)
    }

    companion object {
        @Volatile
        private var INSTANCE: WordWalkRepository? = null

        fun getInstance(context: Context): WordWalkRepository {
            return INSTANCE ?: synchronized(this) {
                val db = WordWalkDatabase.getDatabase(context)
                val repo = WordWalkRepository(db)
                INSTANCE = repo
                repo
            }
        }
    }
}

package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_preferences")
data class UserPreferencesEntity(
    @PrimaryKey val id: Int = 1,
    val preferredLanguage: String = "en",
    val bibleExperience: String = "beginner",
    val spiritualGoal: String = "habit",
    val minutesPerSession: Int = 10,
    val studyDaysPerWeek: Int = 5,
    val reminderTime: String = "07:00 AM",
    val reminderHour: Int = 7,
    val reminderMinute: Int = 0,
    val reminderEnabled: Boolean = true,
    val biggestChallenge: String = "time",
    val desiredPlanStyle: String = "john",
    val onboardingCompleted: Boolean = false,
    val questionnaireStep: Int = 0,
    val currentStreak: Int = 0,
    val lastCompletedDateMillis: Long = 0L,
    val totalSessionsFinished: Int = 0,
    val graceDaysUsed: Int = 0,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "plans")
data class PlanEntity(
    @PrimaryKey val id: String,
    val titleEn: String,
    val titleSi: String,
    val descriptionEn: String,
    val descriptionSi: String,
    val style: String,
    val totalSessions: Int,
    val currentSessionIndex: Int = 1,
    val status: String = "ACTIVE", // ACTIVE, PAUSED, COMPLETED
    val startDateMillis: Long,
    val estimatedCompletionDateMillis: Long,
    val minutesPerSession: Int,
    val studyDaysPerWeek: Int
)

@Entity(tableName = "study_sessions")
data class StudySessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val planId: String,
    val sessionNumber: Int,
    val bookReferenceEn: String,
    val bookReferenceSi: String,
    val passageTitleEn: String,
    val passageTitleSi: String,
    val passageExcerptEn: String,
    val passageExcerptSi: String,
    val contextNoteEn: String,
    val contextNoteSi: String,
    val learningNoteEn: String,
    val learningNoteSi: String,
    val reflectionPromptsEn: String,
    val reflectionPromptsSi: String,
    val thoughtQuestionsEn: String,
    val thoughtQuestionsSi: String,
    val practicalApplicationEn: String,
    val practicalApplicationSi: String,
    val isCatchUpGraceDay: Boolean = false,
    val isCompleted: Boolean = false,
    val completedAtMillis: Long? = null,
    val estimatedMinutes: Int = 10
)

@Entity(tableName = "journal_entries")
data class JournalEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val sessionNumber: Int,
    val scriptureReference: String,
    val dateMillis: Long = System.currentTimeMillis(),
    val whatILearned: String = "",
    val whatSurrendered: String = "",
    val prayerGratitude: String = "",
    val personalReflection: String = ""
)

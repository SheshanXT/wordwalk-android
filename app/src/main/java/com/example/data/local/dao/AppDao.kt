package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.JournalEntryEntity
import com.example.data.local.entities.PlanEntity
import com.example.data.local.entities.StudySessionEntity
import com.example.data.local.entities.UserPreferencesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserPreferencesDao {
    @Query("SELECT * FROM user_preferences WHERE id = 1 LIMIT 1")
    fun getUserPreferences(): Flow<UserPreferencesEntity?>

    @Query("SELECT * FROM user_preferences WHERE id = 1 LIMIT 1")
    suspend fun getUserPreferencesSync(): UserPreferencesEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(prefs: UserPreferencesEntity)

    @Query("DELETE FROM user_preferences")
    suspend fun deleteAll()
}

@Dao
interface PlanDao {
    @Query("SELECT * FROM plans WHERE status != 'COMPLETED' ORDER BY startDateMillis DESC LIMIT 1")
    fun getActivePlan(): Flow<PlanEntity?>

    @Query("SELECT * FROM plans WHERE id = :id LIMIT 1")
    suspend fun getPlanById(id: String): PlanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: PlanEntity)

    @Update
    suspend fun updatePlan(plan: PlanEntity)

    @Query("DELETE FROM plans")
    suspend fun deleteAllPlans()
}

@Dao
interface StudySessionDao {
    @Query("SELECT * FROM study_sessions WHERE planId = :planId ORDER BY sessionNumber ASC")
    fun getSessionsForPlan(planId: String): Flow<List<StudySessionEntity>>

    @Query("SELECT * FROM study_sessions WHERE planId = :planId ORDER BY sessionNumber ASC")
    suspend fun getSessionsForPlanSync(planId: String): List<StudySessionEntity>

    @Query("SELECT * FROM study_sessions WHERE planId = :planId AND sessionNumber = :sessionNumber LIMIT 1")
    fun getSessionByNumber(planId: String, sessionNumber: Int): Flow<StudySessionEntity?>

    @Query("SELECT * FROM study_sessions WHERE id = :id LIMIT 1")
    suspend fun getSessionById(id: Long): StudySessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSessions(sessions: List<StudySessionEntity>)

    @Update
    suspend fun updateSession(session: StudySessionEntity)

    @Query("SELECT COUNT(*) FROM study_sessions WHERE planId = :planId AND isCompleted = 1")
    fun getCompletedSessionsCount(planId: String): Flow<Int>

    @Query("DELETE FROM study_sessions")
    suspend fun deleteAllSessions()
}

@Dao
interface JournalEntryDao {
    @Query("SELECT * FROM journal_entries ORDER BY dateMillis DESC")
    fun getAllJournals(): Flow<List<JournalEntryEntity>>

    @Query("SELECT * FROM journal_entries WHERE sessionId = :sessionId LIMIT 1")
    fun getJournalForSession(sessionId: Long): Flow<JournalEntryEntity?>

    @Query("SELECT * FROM journal_entries WHERE sessionId = :sessionId LIMIT 1")
    suspend fun getJournalForSessionSync(sessionId: Long): JournalEntryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateJournal(entry: JournalEntryEntity)

    @Query("DELETE FROM journal_entries")
    suspend fun deleteAllJournals()
}

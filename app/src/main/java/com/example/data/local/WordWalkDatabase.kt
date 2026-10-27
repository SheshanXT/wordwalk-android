package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.JournalEntryDao
import com.example.data.local.dao.PlanDao
import com.example.data.local.dao.StudySessionDao
import com.example.data.local.dao.UserPreferencesDao
import com.example.data.local.entities.JournalEntryEntity
import com.example.data.local.entities.PlanEntity
import com.example.data.local.entities.StudySessionEntity
import com.example.data.local.entities.UserPreferencesEntity

@Database(
    entities = [
        UserPreferencesEntity::class,
        PlanEntity::class,
        StudySessionEntity::class,
        JournalEntryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class WordWalkDatabase : RoomDatabase() {
    abstract fun userPreferencesDao(): UserPreferencesDao
    abstract fun planDao(): PlanDao
    abstract fun studySessionDao(): StudySessionDao
    abstract fun journalEntryDao(): JournalEntryDao

    companion object {
        @Volatile
        private var INSTANCE: WordWalkDatabase? = null

        fun getDatabase(context: Context): WordWalkDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WordWalkDatabase::class.java,
                    "wordwalk_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

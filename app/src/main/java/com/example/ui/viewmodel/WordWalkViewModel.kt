package com.example.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.entities.JournalEntryEntity
import com.example.data.local.entities.PlanEntity
import com.example.data.local.entities.StudySessionEntity
import com.example.data.local.entities.UserPreferencesEntity
import com.example.data.repository.WordWalkRepository
import com.example.notification.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class WordWalkUiState(
    val preferences: UserPreferencesEntity = UserPreferencesEntity(),
    val activePlan: PlanEntity? = null,
    val sessions: List<StudySessionEntity> = emptyList(),
    val currentSession: StudySessionEntity? = null,
    val currentJournal: JournalEntryEntity? = null,
    val allJournals: List<JournalEntryEntity> = emptyList(),
    val isLoading: Boolean = true,
    val notificationPermissionGranted: Boolean = true,
    val exportedJson: String? = null,
    val showCompletionCelebration: Boolean = false
)

class WordWalkViewModel(
    private val repository: WordWalkRepository
) : ViewModel() {

    private val _currentSelectedSessionId = MutableStateFlow<Long?>(null)
    private val _journalLearned = MutableStateFlow("")
    private val _journalSurrendered = MutableStateFlow("")
    private val _journalPrayer = MutableStateFlow("")
    private val _journalReflection = MutableStateFlow("")
    private val _showCompletionCelebration = MutableStateFlow(false)
    private val _exportedJson = MutableStateFlow<String?>(null)

    val journalLearned: StateFlow<String> = _journalLearned.asStateFlow()
    val journalSurrendered: StateFlow<String> = _journalSurrendered.asStateFlow()
    val journalPrayer: StateFlow<String> = _journalPrayer.asStateFlow()
    val journalReflection: StateFlow<String> = _journalReflection.asStateFlow()
    val showCompletionCelebration: StateFlow<Boolean> = _showCompletionCelebration.asStateFlow()
    val exportedJson: StateFlow<String?> = _exportedJson.asStateFlow()

    private val _sessionsFlow = MutableStateFlow<List<StudySessionEntity>>(emptyList())

    val uiState: StateFlow<WordWalkUiState> = combine(
        repository.userPreferences,
        repository.activePlan,
        _sessionsFlow,
        _currentSelectedSessionId,
        repository.allJournals
    ) { prefs, plan, sessions, selectedSessionId, journals ->
        val safePrefs = prefs ?: UserPreferencesEntity()

        // Find current active session
        val targetSession = if (selectedSessionId != null) {
            sessions.find { it.id == selectedSessionId }
        } else {
            // Find first uncompleted session or last session if all done
            sessions.firstOrNull { !it.isCompleted } ?: sessions.lastOrNull()
        }

        WordWalkUiState(
            preferences = safePrefs,
            activePlan = plan,
            sessions = sessions,
            currentSession = targetSession,
            allJournals = journals,
            isLoading = false,
            exportedJson = _exportedJson.value,
            showCompletionCelebration = _showCompletionCelebration.value
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WordWalkUiState()
    )

    init {
        // Observe plan to load sessions
        viewModelScope.launch {
            repository.activePlan.collect { plan ->
                if (plan != null) {
                    repository.getSessionsForPlan(plan.id).collect { sessionList ->
                        _sessionsFlow.value = sessionList
                    }
                } else {
                    _sessionsFlow.value = emptyList()
                }
            }
        }
    }

    fun selectSession(sessionId: Long) {
        _currentSelectedSessionId.value = sessionId
        // Load existing journal for this session
        viewModelScope.launch {
            val journal = repository.getJournalForSession(sessionId).firstOrNull()
            if (journal != null) {
                _journalLearned.value = journal.whatILearned
                _journalSurrendered.value = journal.whatSurrendered
                _journalPrayer.value = journal.prayerGratitude
                _journalReflection.value = journal.personalReflection
            } else {
                _journalLearned.value = ""
                _journalSurrendered.value = ""
                _journalPrayer.value = ""
                _journalReflection.value = ""
            }
        }
    }

    fun updateJournalLearned(text: String) { _journalLearned.value = text }
    fun updateJournalSurrendered(text: String) { _journalSurrendered.value = text }
    fun updateJournalPrayer(text: String) { _journalPrayer.value = text }
    fun updateJournalReflection(text: String) { _journalReflection.value = text }

    fun dismissCelebration() {
        _showCompletionCelebration.value = false
    }

    fun saveOnboardingProgress(
        step: Int,
        language: String? = null,
        experience: String? = null,
        goals: String? = null,
        minutes: Int? = null,
        days: Int? = null,
        reminderTime: String? = null,
        challenge: String? = null,
        style: String? = null
    ) {
        viewModelScope.launch {
            repository.saveOnboardingProgress(
                step = step,
                language = language,
                experience = experience,
                goals = goals,
                minutes = minutes,
                days = days,
                reminderTime = reminderTime,
                challenge = challenge,
                style = style
            )
        }
    }

    fun completeOnboardingAndGeneratePlan(
        context: Context,
        language: String,
        experience: String,
        goals: String,
        minutes: Int,
        days: Int,
        reminderTime: String,
        challenge: String,
        style: String
    ) {
        viewModelScope.launch {
            repository.completeOnboardingAndGeneratePlan(
                language = language,
                experience = experience,
                goals = goals,
                minutes = minutes,
                days = days,
                reminderTime = reminderTime,
                challenge = challenge,
                style = style
            )

            // Schedule initial notification
            val (hour, min) = when {
                reminderTime.contains("07:00") -> Pair(7, 0)
                reminderTime.contains("12:30") -> Pair(12, 30)
                reminderTime.contains("08:30") -> Pair(20, 30)
                else -> Pair(8, 0)
            }
            NotificationHelper.scheduleDailyReminder(context, hour, min)
        }
    }

    fun completeCurrentSession() {
        val session = uiState.value.currentSession ?: return
        viewModelScope.launch {
            repository.completeStudySession(
                session = session,
                journalLearned = _journalLearned.value,
                journalSurrender = _journalSurrendered.value,
                journalPrayer = _journalPrayer.value,
                journalReflection = _journalReflection.value
            )
            _showCompletionCelebration.value = true
        }
    }

    fun takeGraceCatchUpDay() {
        viewModelScope.launch {
            repository.takeGraceCatchUpDay()
        }
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch {
            repository.setLanguage(lang)
        }
    }

    fun updateReminderToggle(enabled: Boolean, context: Context) {
        val prefs = uiState.value.preferences
        viewModelScope.launch {
            repository.updateReminderSettings(
                enabled = enabled,
                timeLabel = prefs.reminderTime,
                hour = prefs.reminderHour,
                minute = prefs.reminderMinute
            )
            if (enabled) {
                NotificationHelper.scheduleDailyReminder(context, prefs.reminderHour, prefs.reminderMinute)
            } else {
                NotificationHelper.cancelReminder(context)
            }
        }
    }

    fun updateReminderTime(label: String, hour: Int, minute: Int, context: Context) {
        viewModelScope.launch {
            repository.updateReminderSettings(
                enabled = true,
                timeLabel = label,
                hour = hour,
                minute = minute
            )
            NotificationHelper.scheduleDailyReminder(context, hour, minute)
        }
    }

    fun updatePlanPaceAndDays(minutes: Int, daysPerWeek: Int) {
        viewModelScope.launch {
            repository.updatePlanPaceAndDays(minutes, daysPerWeek)
        }
    }

    fun togglePlanStatus() {
        val currentPlan = uiState.value.activePlan ?: return
        val newStatus = if (currentPlan.status == "ACTIVE") "PAUSED" else "ACTIVE"
        viewModelScope.launch {
            repository.setPlanStatus(newStatus)
        }
    }

    fun exportData() {
        viewModelScope.launch {
            val json = repository.exportDataAsJson()
            _exportedJson.value = json
        }
    }

    fun clearExportedJson() {
        _exportedJson.value = null
    }

    fun resetAllData() {
        viewModelScope.launch {
            repository.resetAllData()
            _currentSelectedSessionId.value = null
            _journalLearned.value = ""
            _journalSurrendered.value = ""
            _journalPrayer.value = ""
            _journalReflection.value = ""
        }
    }

    fun triggerTestNotification(context: Context) {
        NotificationHelper.showNotification(context)
    }

    class Factory(private val repository: WordWalkRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WordWalkViewModel::class.java)) {
                return WordWalkViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

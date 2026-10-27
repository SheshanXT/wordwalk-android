package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.UserPreferencesEntity
import com.example.localization.LocaleStrings
import com.example.ui.components.SelectableOptionCard
import com.example.ui.components.WordWalkTopBar
import com.example.ui.viewmodel.WordWalkViewModel

@Composable
fun OnboardingScreen(
    preferences: UserPreferencesEntity,
    viewModel: WordWalkViewModel,
    onOnboardingComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentStep by remember { mutableIntStateOf(preferences.questionnaireStep.coerceIn(0, 8)) }
    var selectedLang by remember { mutableStateOf(preferences.preferredLanguage) }
    var selectedExperience by remember { mutableStateOf(preferences.bibleExperience) }
    var selectedGoal by remember { mutableStateOf(preferences.spiritualGoal) }
    var selectedMinutes by remember { mutableIntStateOf(preferences.minutesPerSession) }
    var selectedDays by remember { mutableIntStateOf(preferences.studyDaysPerWeek) }
    var selectedReminderTime by remember { mutableStateOf(preferences.reminderTime) }
    var selectedChallenge by remember { mutableStateOf(preferences.biggestChallenge) }
    var selectedStyle by remember { mutableStateOf(preferences.desiredPlanStyle) }
    var isGenerating by remember { mutableStateOf(false) }

    fun S(key: String): String = LocaleStrings.get(key, selectedLang)

    Scaffold(
        topBar = {
            WordWalkTopBar(
                title = S("app_title"),
                currentLanguage = selectedLang,
                onToggleLanguage = {
                    val nextLang = if (selectedLang == LocaleStrings.LANG_EN) LocaleStrings.LANG_SI else LocaleStrings.LANG_EN
                    selectedLang = nextLang
                    viewModel.setLanguage(nextLang)
                }
            )
        },
        bottomBar = {
            if (currentStep > 0 && !isGenerating) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                if (currentStep > 1) {
                                    currentStep--
                                    viewModel.saveOnboardingProgress(step = currentStep)
                                } else {
                                    currentStep = 0
                                }
                            },
                            modifier = Modifier.testTag("onboarding_back_btn")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(S("btn_back"))
                        }

                        Button(
                            onClick = {
                                if (currentStep < 8) {
                                    currentStep++
                                    viewModel.saveOnboardingProgress(
                                        step = currentStep,
                                        language = selectedLang,
                                        experience = selectedExperience,
                                        goals = selectedGoal,
                                        minutes = selectedMinutes,
                                        days = selectedDays,
                                        reminderTime = selectedReminderTime,
                                        challenge = selectedChallenge,
                                        style = selectedStyle
                                    )
                                } else {
                                    // Step 8 complete: generate plan!
                                    isGenerating = true
                                    viewModel.completeOnboardingAndGeneratePlan(
                                        context = context,
                                        language = selectedLang,
                                        experience = selectedExperience,
                                        goals = selectedGoal,
                                        minutes = selectedMinutes,
                                        days = selectedDays,
                                        reminderTime = selectedReminderTime,
                                        challenge = selectedChallenge,
                                        style = selectedStyle
                                    )
                                    onOnboardingComplete()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.testTag("onboarding_next_btn")
                        ) {
                            Text(if (currentStep == 8) S("btn_start_walk") else S("btn_next"))
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                if (currentStep == 8) Icons.Default.Check else Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            if (currentStep > 0 && !isGenerating) {
                // Progress Bar
                Column(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = String.format(S("onboarding_step_of"), currentStep, 8),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${(currentStep * 100) / 8}%",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { currentStep.toFloat() / 8f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }

            AnimatedContent(
                targetState = currentStep,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "onboarding_step_anim"
            ) { step ->
                when (step) {
                    0 -> WelcomeView(
                        lang = selectedLang,
                        hasSavedProgress = preferences.questionnaireStep > 0,
                        onStart = {
                            currentStep = 1
                            viewModel.saveOnboardingProgress(step = 1)
                        },
                        onResume = {
                            currentStep = preferences.questionnaireStep.coerceIn(1, 8)
                        }
                    )
                    1 -> LanguageSelectionView(
                        lang = selectedLang,
                        selectedLang = selectedLang,
                        onSelectLang = {
                            selectedLang = it
                            viewModel.setLanguage(it)
                        }
                    )
                    2 -> ExperienceSelectionView(
                        lang = selectedLang,
                        selected = selectedExperience,
                        onSelect = { selectedExperience = it }
                    )
                    3 -> GoalSelectionView(
                        lang = selectedLang,
                        selected = selectedGoal,
                        onSelect = { selectedGoal = it }
                    )
                    4 -> TimeSelectionView(
                        lang = selectedLang,
                        selectedMinutes = selectedMinutes,
                        onSelect = { selectedMinutes = it }
                    )
                    5 -> DaysSelectionView(
                        lang = selectedLang,
                        selectedDays = selectedDays,
                        onSelect = { selectedDays = it }
                    )
                    6 -> ReminderSelectionView(
                        lang = selectedLang,
                        selectedReminder = selectedReminderTime,
                        onSelect = { selectedReminderTime = it }
                    )
                    7 -> ChallengeSelectionView(
                        lang = selectedLang,
                        selected = selectedChallenge,
                        onSelect = { selectedChallenge = it }
                    )
                    8 -> PlanStyleSelectionView(
                        lang = selectedLang,
                        selected = selectedStyle,
                        onSelect = { selectedStyle = it }
                    )
                }
            }

            if (isGenerating) {
                Spacer(modifier = Modifier.height(32.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = S("plan_creating"),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun WelcomeView(
    lang: String,
    hasSavedProgress: Boolean,
    onStart: () -> Unit,
    onResume: () -> Unit
) {
    fun S(key: String): String = LocaleStrings.get(key, lang)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(96.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Spa,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(52.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = S("onboarding_welcome_title"),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = S("app_subtitle"),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.tertiary,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = S("onboarding_welcome_desc"),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (hasSavedProgress) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.tertiaryContainer,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = S("onboarding_resume_notice"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.padding(14.dp),
                    textAlign = TextAlign.Center
                )
            }

            Button(
                onClick = onResume,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("resume_onboarding_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(S("onboarding_btn_resume"), style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("restart_onboarding_btn")
            ) {
                Text(S("onboarding_btn_start"), style = MaterialTheme.typography.titleMedium)
            }
        } else {
            Button(
                onClick = onStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("start_onboarding_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(S("onboarding_btn_start"), style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
            }
        }
    }
}

@Composable
private fun LanguageSelectionView(
    lang: String,
    selectedLang: String,
    onSelectLang: (String) -> Unit
) {
    fun S(key: String): String = LocaleStrings.get(key, lang)

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = S("q_lang_title"),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = S("q_lang_desc"),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 6.dp, bottom = 20.dp)
        )

        SelectableOptionCard(
            title = "English",
            description = "English translations, notes, and prayer journal",
            isSelected = selectedLang == LocaleStrings.LANG_EN,
            onSelect = { onSelectLang(LocaleStrings.LANG_EN) },
            testTag = "lang_en_card"
        )
        Spacer(modifier = Modifier.height(12.dp))
        SelectableOptionCard(
            title = "සිංහල (Sinhala)",
            description = "සිංහල ලියවිලි යොමු, පැහැදිලි කිරීම් සහ යාච්ඤා සටහන්",
            isSelected = selectedLang == LocaleStrings.LANG_SI,
            onSelect = { onSelectLang(LocaleStrings.LANG_SI) },
            testTag = "lang_si_card"
        )
    }
}

@Composable
private fun ExperienceSelectionView(
    lang: String,
    selected: String,
    onSelect: (String) -> Unit
) {
    fun S(key: String): String = LocaleStrings.get(key, lang)

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = S("q_exp_title"),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = S("q_exp_desc"),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 6.dp, bottom = 20.dp)
        )

        val options = listOf(
            Triple("beginner", S("exp_beginner"), S("exp_beginner_desc")),
            Triple("explored", S("exp_explored"), S("exp_explored_desc")),
            Triple("returning", S("exp_returning"), S("exp_returning_desc")),
            Triple("consistent", S("exp_consistent"), S("exp_consistent_desc"))
        )

        options.forEach { (key, title, desc) ->
            SelectableOptionCard(
                title = title,
                description = desc,
                isSelected = selected == key,
                onSelect = { onSelect(key) },
                testTag = "exp_${key}_card"
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun GoalSelectionView(
    lang: String,
    selected: String,
    onSelect: (String) -> Unit
) {
    fun S(key: String): String = LocaleStrings.get(key, lang)

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = S("q_goals_title"),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = S("q_goals_desc"),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 6.dp, bottom = 20.dp)
        )

        val goals = listOf(
            Pair("habit", S("goal_habit")),
            Pair("jesus", S("goal_jesus")),
            Pair("peace", S("goal_peace")),
            Pair("apply", S("goal_apply")),
            Pair("overcome", S("goal_overcome"))
        )

        goals.forEach { (key, title) ->
            SelectableOptionCard(
                title = title,
                isSelected = selected == key,
                onSelect = { onSelect(key) },
                testTag = "goal_${key}_card"
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun TimeSelectionView(
    lang: String,
    selectedMinutes: Int,
    onSelect: (Int) -> Unit
) {
    fun S(key: String): String = LocaleStrings.get(key, lang)

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = S("q_time_title"),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = S("q_time_desc"),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 6.dp, bottom = 20.dp)
        )

        val times = listOf(
            Pair(5, S("time_5min")),
            Pair(10, S("time_10min")),
            Pair(15, S("time_15min")),
            Pair(20, S("time_20min"))
        )

        times.forEach { (mins, title) ->
            SelectableOptionCard(
                title = title,
                isSelected = selectedMinutes == mins,
                onSelect = { onSelect(mins) },
                testTag = "time_${mins}min_card"
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun DaysSelectionView(
    lang: String,
    selectedDays: Int,
    onSelect: (Int) -> Unit
) {
    fun S(key: String): String = LocaleStrings.get(key, lang)

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = S("q_days_title"),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = S("q_days_desc"),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 6.dp, bottom = 20.dp)
        )

        val days = listOf(
            Pair(5, S("days_5")),
            Pair(6, S("days_6")),
            Pair(7, S("days_7"))
        )

        days.forEach { (d, title) ->
            SelectableOptionCard(
                title = title,
                isSelected = selectedDays == d,
                onSelect = { onSelect(d) },
                testTag = "days_${d}_card"
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun ReminderSelectionView(
    lang: String,
    selectedReminder: String,
    onSelect: (String) -> Unit
) {
    fun S(key: String): String = LocaleStrings.get(key, lang)

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = S("q_reminder_title"),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = S("q_reminder_desc"),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 6.dp, bottom = 20.dp)
        )

        val reminders = listOf(
            Pair("07:00 AM", S("rem_morning")),
            Pair("12:30 PM", S("rem_midday")),
            Pair("08:30 PM", S("rem_evening"))
        )

        reminders.forEach { (timeStr, title) ->
            SelectableOptionCard(
                title = title,
                isSelected = selectedReminder.contains(timeStr),
                onSelect = { onSelect(timeStr) },
                testTag = "rem_${timeStr}_card"
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun ChallengeSelectionView(
    lang: String,
    selected: String,
    onSelect: (String) -> Unit
) {
    fun S(key: String): String = LocaleStrings.get(key, lang)

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = S("q_challenge_title"),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = S("q_challenge_desc"),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 6.dp, bottom = 20.dp)
        )

        val challenges = listOf(
            Pair("time", S("chal_time")),
            Pair("context", S("chal_context")),
            Pair("consistency", S("chal_consistency")),
            Pair("start", S("chal_start"))
        )

        challenges.forEach { (key, title) ->
            SelectableOptionCard(
                title = title,
                isSelected = selected == key,
                onSelect = { onSelect(key) },
                testTag = "chal_${key}_card"
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun PlanStyleSelectionView(
    lang: String,
    selected: String,
    onSelect: (String) -> Unit
) {
    fun S(key: String): String = LocaleStrings.get(key, lang)

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = S("q_style_title"),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = S("q_style_desc"),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 6.dp, bottom = 20.dp)
        )

        val styles = listOf(
            Triple("john", S("style_john"), S("style_john_desc")),
            Triple("wisdom", S("style_wisdom"), S("style_wisdom_desc")),
            Triple("foundations", S("style_foundations"), S("style_foundations_desc"))
        )

        styles.forEach { (key, title, desc) ->
            SelectableOptionCard(
                title = title,
                description = desc,
                isSelected = selected == key,
                onSelect = { onSelect(key) },
                testTag = "style_${key}_card"
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

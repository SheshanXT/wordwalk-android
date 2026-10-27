package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.repository.WordWalkRepository
import com.example.localization.LocaleStrings
import com.example.notification.NotificationHelper
import com.example.ui.screens.DailyStudyScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PlanScreen
import com.example.ui.screens.ProgressScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.WordWalkTheme
import com.example.ui.viewmodel.WordWalkViewModel

import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {

    private val repository by lazy {
        WordWalkRepository.getInstance(applicationContext)
    }

    private val viewModel: WordWalkViewModel by viewModels {
        WordWalkViewModel.Factory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Create notification channel on launch
        NotificationHelper.createNotificationChannel(this)

        setContent {
            WordWalkTheme {
                WordWalkApp(viewModel = viewModel)
            }
        }
    }
}

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Dashboard : Screen("dashboard")
    object DailyStudy : Screen("daily_study")
    object Plan : Screen("plan")
    object Progress : Screen("progress")
    object Settings : Screen("settings")
}

@Composable
fun WordWalkApp(viewModel: WordWalkViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    val preferredLanguage = uiState.preferences.preferredLanguage
    fun S(key: String): String = LocaleStrings.get(key, preferredLanguage)

    // Determine start destination based on onboarding completion
    val startDestination = if (uiState.preferences.onboardingCompleted) {
        Screen.Dashboard.route
    } else {
        Screen.Onboarding.route
    }

    val showBottomBar = currentRoute in listOf(
        Screen.Dashboard.route,
        Screen.Plan.route,
        Screen.Progress.route,
        Screen.Settings.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp
                ) {
                    val items = listOf(
                        Triple(Screen.Dashboard.route, S("nav_home"), Icons.Filled.Home to Icons.Outlined.Home),
                        Triple(Screen.Plan.route, S("nav_plan"), Icons.Filled.MenuBook to Icons.Outlined.MenuBook),
                        Triple(Screen.Progress.route, S("nav_progress"), Icons.Filled.TrendingUp to Icons.Outlined.TrendingUp),
                        Triple(Screen.Settings.route, S("nav_settings"), Icons.Filled.Settings to Icons.Outlined.Settings)
                    )

                    items.forEach { (route, label, icons) ->
                        val selected = currentRoute == route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (currentRoute != route) {
                                    navController.navigate(route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) icons.first else icons.second,
                                    contentDescription = label
                                )
                            },
                            label = { Text(text = label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                            ),
                            modifier = Modifier.testTag("nav_${route}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    preferences = uiState.preferences,
                    viewModel = viewModel,
                    onOnboardingComplete = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    preferences = uiState.preferences,
                    activePlan = uiState.activePlan,
                    currentSession = uiState.currentSession,
                    onStartStudy = { sessionId ->
                        viewModel.selectSession(sessionId)
                        navController.navigate(Screen.DailyStudy.route)
                    },
                    onTakeGraceDay = {
                        viewModel.takeGraceCatchUpDay()
                    },
                    onSwitchLanguage = {
                        val next = if (preferredLanguage == LocaleStrings.LANG_EN) LocaleStrings.LANG_SI else LocaleStrings.LANG_EN
                        viewModel.setLanguage(next)
                    },
                    onNavigateToPlan = {
                        navController.navigate(Screen.Plan.route)
                    }
                )
            }

            composable(Screen.DailyStudy.route) {
                BackHandler {
                    navController.popBackStack()
                }

                DailyStudyScreen(
                    session = uiState.currentSession,
                    viewModel = viewModel,
                    preferredLanguage = preferredLanguage,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.Plan.route) {
                BackHandler {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                    }
                }

                PlanScreen(
                    activePlan = uiState.activePlan,
                    sessions = uiState.sessions,
                    viewModel = viewModel,
                    preferredLanguage = preferredLanguage,
                    onSelectSession = { sessionId ->
                        viewModel.selectSession(sessionId)
                        navController.navigate(Screen.DailyStudy.route)
                    },
                    onSwitchLanguage = {
                        val next = if (preferredLanguage == LocaleStrings.LANG_EN) LocaleStrings.LANG_SI else LocaleStrings.LANG_EN
                        viewModel.setLanguage(next)
                    }
                )
            }

            composable(Screen.Progress.route) {
                BackHandler {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                    }
                }

                ProgressScreen(
                    preferences = uiState.preferences,
                    journals = uiState.allJournals,
                    viewModel = viewModel,
                    preferredLanguage = preferredLanguage,
                    onSwitchLanguage = {
                        val next = if (preferredLanguage == LocaleStrings.LANG_EN) LocaleStrings.LANG_SI else LocaleStrings.LANG_EN
                        viewModel.setLanguage(next)
                    }
                )
            }

            composable(Screen.Settings.route) {
                BackHandler {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                    }
                }

                SettingsScreen(
                    preferences = uiState.preferences,
                    viewModel = viewModel,
                    preferredLanguage = preferredLanguage,
                    onResetToOnboarding = {
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onSwitchLanguage = {
                        val next = if (preferredLanguage == LocaleStrings.LANG_EN) LocaleStrings.LANG_SI else LocaleStrings.LANG_EN
                        viewModel.setLanguage(next)
                    }
                )
            }
        }
    }
}

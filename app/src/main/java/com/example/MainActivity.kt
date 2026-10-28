package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

enum class AppScreen {
    HOME,
    GAME_SETTINGS,
    SAV_EDITOR,
    GYRO_BOOST,
    PERFORMANCE_ENGINE,
    REFRESH_RATE,
    GAME_SESSION,
    NETWORK_QUALITY,
    COMMAND_TEST_CENTER,
    POST_MATCH_REPORT,
    TOUCH_RESPONSE_LAB,
    SETTINGS
}

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FrameForgeTheme {
                MainAppNavHost(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppNavHost(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var currentScreen by remember { mutableStateOf(AppScreen.HOME) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearNotification()
        }
    }

    val showBottomBar = currentScreen in listOf(
        AppScreen.HOME,
        AppScreen.GAME_SETTINGS,
        AppScreen.PERFORMANCE_ENGINE,
        AppScreen.SAV_EDITOR,
        AppScreen.SETTINGS
    )

    // Back button handling
    if (currentScreen != AppScreen.HOME) {
        BackHandler {
            currentScreen = AppScreen.HOME
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = GamingDarkBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = GamingSurface,
                    contentColor = TextPrimary,
                    tonalElevation = 8.dp,
                    windowInsets = WindowInsets.navigationBars
                ) {
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.HOME,
                        onClick = { currentScreen = AppScreen.HOME },
                        icon = {
                            Icon(
                                if (currentScreen == AppScreen.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = "Home"
                            )
                        },
                        label = { Text("Home", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonGreenActive,
                            selectedTextColor = NeonGreenActive,
                            indicatorColor = GamingSurfaceVariant,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_home")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.GAME_SETTINGS,
                        onClick = { currentScreen = AppScreen.GAME_SETTINGS },
                        icon = {
                            Icon(
                                if (currentScreen == AppScreen.GAME_SETTINGS) Icons.Filled.Tune else Icons.Outlined.Tune,
                                contentDescription = "Graphics"
                            )
                        },
                        label = { Text("Graphics", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonGreenActive,
                            selectedTextColor = NeonGreenActive,
                            indicatorColor = GamingSurfaceVariant,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_graphics")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.PERFORMANCE_ENGINE,
                        onClick = { currentScreen = AppScreen.PERFORMANCE_ENGINE },
                        icon = {
                            Icon(
                                if (currentScreen == AppScreen.PERFORMANCE_ENGINE) Icons.Filled.RocketLaunch else Icons.Outlined.RocketLaunch,
                                contentDescription = "Game Opt"
                            )
                        },
                        label = { Text("Game Opt", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonGreenActive,
                            selectedTextColor = NeonGreenActive,
                            indicatorColor = GamingSurfaceVariant,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_game_opt")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.SAV_EDITOR,
                        onClick = { currentScreen = AppScreen.SAV_EDITOR },
                        icon = {
                            Icon(
                                if (currentScreen == AppScreen.SAV_EDITOR) Icons.Filled.EditNote else Icons.Outlined.EditNote,
                                contentDescription = "SAV Editor"
                            )
                        },
                        label = { Text("SAV Editor", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonGreenActive,
                            selectedTextColor = NeonGreenActive,
                            indicatorColor = GamingSurfaceVariant,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_sav_editor")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.SETTINGS,
                        onClick = { currentScreen = AppScreen.SETTINGS },
                        icon = {
                            Icon(
                                if (currentScreen == AppScreen.SETTINGS) Icons.Filled.Settings else Icons.Outlined.Settings,
                                contentDescription = "Settings"
                            )
                        },
                        label = { Text("Settings", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonGreenActive,
                            selectedTextColor = NeonGreenActive,
                            indicatorColor = GamingSurfaceVariant,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_settings")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.HOME -> {
                    HomeScreen(
                        uiState = uiState,
                        onNavigateToGameSettings = { currentScreen = AppScreen.GAME_SETTINGS },
                        onNavigateToDiagnostics = { currentScreen = AppScreen.PERFORMANCE_ENGINE },
                        onNavigateToCommandTestCenter = { currentScreen = AppScreen.COMMAND_TEST_CENTER },
                        onNavigateToSavEditor = { currentScreen = AppScreen.SAV_EDITOR },
                        onNavigateToGyroBoost = { currentScreen = AppScreen.GYRO_BOOST },
                        onNavigateToPerformanceEngine = { currentScreen = AppScreen.PERFORMANCE_ENGINE },
                        onNavigateToGameSession = { currentScreen = AppScreen.GAME_SESSION },
                        onStartGameSession = { viewModel.startGameSession() },
                        onStopGameSession = {
                            viewModel.stopGameSession()
                            viewModel.endCurrentGameSession()
                            currentScreen = AppScreen.POST_MATCH_REPORT
                        },
                        onSelectGame = { viewModel.selectGame(it) },
                        onNavigateToNetworkQuality = { currentScreen = AppScreen.NETWORK_QUALITY },
                        onNavigateToTouchLab = { currentScreen = AppScreen.TOUCH_RESPONSE_LAB },
                        onEvaluate8v8Readiness = { viewModel.evaluate8v8MatchReadiness() },
                        onLaunchZeroOverheadGame = { game, profile ->
                            viewModel.launchGameSession(game, profile)
                        },
                        onSelectWhitelistModel = { viewModel.selectWhitelistDevice(it) }
                    )
                }

                AppScreen.GAME_SETTINGS -> {
                    GameSettingsScreen(
                        uiState = uiState,
                        onBack = { currentScreen = AppScreen.HOME },
                        onUpdateProfile = { viewModel.updateGraphicsProfile(it) },
                        onApply = { viewModel.applyGraphicsProfile() },
                        onSaveProfile = { viewModel.saveGraphicsProfile() },
                        onResetProfile = { viewModel.resetGraphicsProfile() }
                    )
                }

                AppScreen.SAV_EDITOR -> {
                    SavEditorScreen(
                        uiState = uiState,
                        onBack = { currentScreen = AppScreen.HOME },
                        onUpdateGuiGraphics = { viewModel.updateSavGuiGraphics(it) },
                        onUpdateGuiFrameRate = { viewModel.updateSavGuiFrameRate(it) },
                        onUpdateGuiManualFps = { viewModel.updateSavGuiManualFps(it) },
                        onUpdateGuiStyle = { viewModel.updateSavGuiStyle(it) },
                        onUpdateGuiBrightness = { viewModel.updateSavGuiBrightness(it) },
                        onUpdateProposedValue = { keyId, value -> viewModel.updateSavProposedValue(keyId, value) },
                        onApplySavChanges = { viewModel.applySavChanges() },
                        onExportSavJson = { viewModel.exportSavJson() },
                        onAddCustomKey = { key, value -> viewModel.addSavCustomKey(key, value) },
                        onApplyQuickPreset = { preset -> viewModel.applySavPreset(preset) }
                    )
                }

                AppScreen.GYRO_BOOST -> {
                    GyroBoostScreen(
                        uiState = uiState,
                        onBack = { currentScreen = AppScreen.HOME },
                        onToggleGyroBoost = { viewModel.toggleGyroBoost(it) },
                        onSetGyroMode = { viewModel.setGyroMode(it) },
                        onUpdateSensitivity = { x, y -> viewModel.updateGyroSensitivity(x, y) }
                    )
                }

                AppScreen.PERFORMANCE_ENGINE -> {
                    PerformanceEngineScreen(
                        uiState = uiState,
                        onBack = { currentScreen = AppScreen.HOME },
                        onSelectMode = { viewModel.setPerformanceMode(it) },
                        onToggleExternalCooler = { viewModel.toggleExternalCooler(it) },
                        onApplyOptimizations = { viewModel.applyRawOptimizations() },
                        onRestoreSettings = { viewModel.restoreAllSettings() },
                        onSelectWhitelistDevice = { viewModel.selectWhitelistDevice(it) }
                    )
                }

                AppScreen.REFRESH_RATE -> {
                    RefreshRateScreen(
                        uiState = uiState,
                        supportedModes = viewModel.getSupportedDisplayModes(),
                        onBack = { currentScreen = AppScreen.HOME },
                        onSetRefreshRateMode = { viewModel.setDisplayRefreshPreset(it) }
                    )
                }

                AppScreen.GAME_SESSION -> {
                    GameSessionScreen(
                        uiState = uiState,
                        onBack = { currentScreen = AppScreen.HOME },
                        onEndSession = {
                            viewModel.stopGameSession()
                            viewModel.endCurrentGameSession()
                            currentScreen = AppScreen.POST_MATCH_REPORT
                        }
                    )
                }

                AppScreen.NETWORK_QUALITY -> {
                    NetworkQualityScreen(
                        report = uiState.networkReport,
                        onBack = { currentScreen = AppScreen.HOME },
                        onRunDiagnostics = { viewModel.runNetworkDiagnostics() }
                    )
                }

                AppScreen.COMMAND_TEST_CENTER -> {
                    CommandTestCenterScreen(
                        uiState = uiState,
                        onBack = { currentScreen = AppScreen.HOME },
                        onRunSingleTest = { viewModel.runSingleCommandTest(it) },
                        onRunAllTests = { viewModel.runAllCommandCategoryTests() },
                        getSanitizedReport = { viewModel.getSanitizedDiagnosticReport() }
                    )
                }

                AppScreen.TOUCH_RESPONSE_LAB -> {
                    TouchResponseLabScreen(
                        touchMetrics = uiState.touchMetrics,
                        onBack = { currentScreen = AppScreen.HOME },
                        onRecordTouchEvent = { _, _ -> },
                        onResetMetrics = { viewModel.resetTouchMetrics() }
                    )
                }

                AppScreen.POST_MATCH_REPORT -> {
                    uiState.sessionState.latestPostMatchReport?.let { report ->
                        PostMatchReportScreen(
                            report = report,
                            onRollbackSettings = {
                                viewModel.rollbackLastSession()
                                currentScreen = AppScreen.HOME
                            },
                            onDismiss = {
                                viewModel.dismissPostMatchReport()
                                currentScreen = AppScreen.HOME
                            }
                        )
                    } ?: run {
                        currentScreen = AppScreen.HOME
                    }
                }

                AppScreen.SETTINGS -> {
                    SettingsScreen(
                        uiState = uiState,
                        onBack = { currentScreen = AppScreen.HOME },
                        onRestoreSettings = { viewModel.restoreAllSettings() },
                        onSetAnimationScale = { viewModel.setAnimationScale(it) },
                        onRestoreAnimationScales = { viewModel.restoreAnimationScales() }
                    )
                }
            }
        }
    }
}

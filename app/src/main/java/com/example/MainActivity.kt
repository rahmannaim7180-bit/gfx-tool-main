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
    PERFORMANCE_DIAGNOSTICS,
    COMMAND_TEST_CENTER,
    GAME_SETTINGS,
    SAV_EDITOR,
    GYRO_BOOST,
    PERFORMANCE_ENGINE,
    REFRESH_RATE,
    FRAME_PACING,
    GAME_SESSION,
    NETWORK_QUALITY,
    COMPATIBILITY_DATABASE,
    DEVICE_CAPABILITIES,
    TOUCH_RESPONSE_LAB,
    GAMEPLAY_RESPONSIVENESS,
    POST_MATCH_REPORT,
    SELF_AUDIT,
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
        AppScreen.PERFORMANCE_DIAGNOSTICS,
        AppScreen.COMMAND_TEST_CENTER,
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
                            selectedIconColor = ElectricCyan,
                            selectedTextColor = ElectricCyan,
                            indicatorColor = ElectricCyanContainer,
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
                            selectedIconColor = ElectricCyan,
                            selectedTextColor = ElectricCyan,
                            indicatorColor = ElectricCyanContainer,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_graphics")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.PERFORMANCE_DIAGNOSTICS,
                        onClick = { currentScreen = AppScreen.PERFORMANCE_DIAGNOSTICS },
                        icon = {
                            Icon(
                                if (currentScreen == AppScreen.PERFORMANCE_DIAGNOSTICS) Icons.Filled.Troubleshoot else Icons.Outlined.Troubleshoot,
                                contentDescription = "Diagnostics"
                            )
                        },
                        label = { Text("Diagnostics", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ElectricCyan,
                            selectedTextColor = ElectricCyan,
                            indicatorColor = ElectricCyanContainer,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_diagnostics")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.COMMAND_TEST_CENTER,
                        onClick = { currentScreen = AppScreen.COMMAND_TEST_CENTER },
                        icon = {
                            Icon(
                                if (currentScreen == AppScreen.COMMAND_TEST_CENTER) Icons.Filled.Terminal else Icons.Outlined.Terminal,
                                contentDescription = "Audit"
                            )
                        },
                        label = { Text("Command Ctr", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ElectricCyan,
                            selectedTextColor = ElectricCyan,
                            indicatorColor = ElectricCyanContainer,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_command_center")
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
                            selectedIconColor = ElectricCyan,
                            selectedTextColor = ElectricCyan,
                            indicatorColor = ElectricCyanContainer,
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
                        onNavigateToDiagnostics = { currentScreen = AppScreen.PERFORMANCE_DIAGNOSTICS },
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
                        onNavigateToCompatibility = { currentScreen = AppScreen.COMPATIBILITY_DATABASE },
                        onNavigateToDeviceCapabilities = { currentScreen = AppScreen.DEVICE_CAPABILITIES },
                        onNavigateToFramePacing = {
                            viewModel.evaluateFramePacing()
                            currentScreen = AppScreen.FRAME_PACING
                        },
                        onNavigateToTouchLab = { currentScreen = AppScreen.TOUCH_RESPONSE_LAB },
                        onNavigateToGameplayLab = { currentScreen = AppScreen.GAMEPLAY_RESPONSIVENESS },
                        onNavigateToSelfAudit = { currentScreen = AppScreen.SELF_AUDIT },
                        onEvaluate8v8Readiness = { viewModel.evaluate8v8MatchReadiness() },
                        onLaunchZeroOverheadGame = { game, profile ->
                            viewModel.launchGameSession(game, profile)
                        }
                    )
                }

                AppScreen.PERFORMANCE_DIAGNOSTICS -> {
                    PerformanceDiagnosticsScreen(
                        uiState = uiState,
                        onBack = { currentScreen = AppScreen.HOME },
                        onRefresh = { viewModel.refreshDiagnostics() },
                        onActionClick = { actionId ->
                            when (actionId) {
                                "nav_sav_editor" -> currentScreen = AppScreen.SAV_EDITOR
                                "force_144hz_shizuku" -> viewModel.setDisplayRefreshPreset("144 Hz")
                                "force_120hz_display" -> viewModel.setDisplayRefreshPreset("120 Hz")
                                "set_sustained_mode" -> viewModel.setPerformanceMode(com.example.data.model.PerformanceMode.SUSTAINED_COMPETITIVE)
                                "apply_adpf_cpu_hints" -> viewModel.applyRawOptimizations()
                                "nav_gyro_assistant" -> currentScreen = AppScreen.GYRO_BOOST
                                "trim_shader_caches" -> viewModel.executeAdbCommand(
                                    com.example.data.model.AdbCommand.PREDEFINED_COMMANDS.first { it.id == "clear_game_cache" }
                                )
                                "nav_config_inspector" -> currentScreen = AppScreen.SAV_EDITOR
                                "nav_command_test_center" -> currentScreen = AppScreen.COMMAND_TEST_CENTER
                                "switch_to_720p" -> {
                                    viewModel.updateGraphicsProfile(
                                        uiState.activeProfile.copy(resolution = com.example.data.model.ResolutionPreset.RES_1280x720)
                                    )
                                    viewModel.applyGraphicsProfile()
                                }
                            }
                        }
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
                        onExportSavJson = { viewModel.exportSavJson() }
                    )
                }

                AppScreen.GYRO_BOOST -> {
                    GyroResponseLabScreen(
                        specs = uiState.gyroSpecs,
                        telemetry = uiState.gyroTelemetry,
                        onBack = { currentScreen = AppScreen.HOME },
                        onStartTesting = { viewModel.startGyroLab() },
                        onStopTesting = { viewModel.stopGyroLab() },
                        onSelectProfile = { viewModel.selectGyroProfile(it) },
                        onStartStationaryCalibration = { viewModel.startStationaryGyroCalibration() },
                        onStartRotationTest = { viewModel.startRotationGyroTest() },
                        onResetCalibration = { viewModel.resetGyroCalibration() }
                    )
                }

                AppScreen.PERFORMANCE_ENGINE -> {
                    PerformanceEngineScreen(
                        uiState = uiState,
                        onBack = { currentScreen = AppScreen.HOME },
                        onSelectMode = { viewModel.setPerformanceMode(it) },
                        onToggleExternalCooler = { viewModel.toggleExternalCooler(it) },
                        onApplyOptimizations = { viewModel.applyRawOptimizations() },
                        onRestoreSettings = { viewModel.restoreAllSettings() }
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

                AppScreen.FRAME_PACING -> {
                    FramePacingScreen(
                        uiState = uiState,
                        onBack = { currentScreen = AppScreen.HOME }
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

                AppScreen.COMPATIBILITY_DATABASE -> {
                    CompatibilityDatabaseScreen(
                        onBack = { currentScreen = AppScreen.HOME },
                        onCompareWithDevice = { _ ->
                            currentScreen = AppScreen.DEVICE_CAPABILITIES
                        }
                    )
                }

                AppScreen.DEVICE_CAPABILITIES -> {
                    DeviceCapabilitiesScreen(
                        deviceSummary = uiState.deviceSummary,
                        capabilities = uiState.capabilityRegistry,
                        onBack = { currentScreen = AppScreen.HOME }
                    )
                }

                AppScreen.TOUCH_RESPONSE_LAB -> {
                    TouchResponseLabScreen(
                        metrics = uiState.touchMetrics,
                        onRecordTouchEvent = { viewModel.recordTouchEvent(it) },
                        onReset = { viewModel.resetTouchMetrics() },
                        onBack = { currentScreen = AppScreen.HOME }
                    )
                }

                AppScreen.GAMEPLAY_RESPONSIVENESS -> {
                    GameplayResponsivenessScreen(
                        onBack = { currentScreen = AppScreen.HOME }
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

                AppScreen.SELF_AUDIT -> {
                    SelfAuditScreen(
                        onBack = { currentScreen = AppScreen.HOME }
                    )
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

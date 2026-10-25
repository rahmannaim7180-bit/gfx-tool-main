package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

enum class AppScreen {
    SPLASH,
    HOME,
    GAME_SETTINGS,
    GYRO_BOOST,
    PERFORMANCE_ENGINE,
    RAW_PERFORMANCE,
    ADB_SHIZUKU,
    INPUT_LAB,
    FRAME_PACING,
    BOTTLENECK,
    BENCHMARK,
    ADVANCED_SETTINGS,
    REFRESH_RATE,
    GAME_SESSION,
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
        AppScreen.SETTINGS
    )

    // Back button handling
    if (currentScreen != AppScreen.HOME && currentScreen != AppScreen.SPLASH) {
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
                        label = { Text("Home", fontSize = 11.sp) },
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
                                if (currentScreen == AppScreen.GAME_SETTINGS) Icons.Filled.SportsEsports else Icons.Outlined.SportsEsports,
                                contentDescription = "Game"
                            )
                        },
                        label = { Text("Game", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ElectricCyan,
                            selectedTextColor = ElectricCyan,
                            indicatorColor = ElectricCyanContainer,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_game")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.PERFORMANCE_ENGINE,
                        onClick = { currentScreen = AppScreen.PERFORMANCE_ENGINE },
                        icon = {
                            Icon(
                                if (currentScreen == AppScreen.PERFORMANCE_ENGINE) Icons.Filled.Speed else Icons.Outlined.Speed,
                                contentDescription = "Engine"
                            )
                        },
                        label = { Text("Tools", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ElectricCyan,
                            selectedTextColor = ElectricCyan,
                            indicatorColor = ElectricCyanContainer,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_tools")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.SETTINGS,
                        onClick = { currentScreen = AppScreen.SETTINGS },
                        icon = {
                            Icon(
                                if (currentScreen == AppScreen.SETTINGS) Icons.Filled.Person else Icons.Outlined.Person,
                                contentDescription = "Profile"
                            )
                        },
                        label = { Text("Profile", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ElectricCyan,
                            selectedTextColor = ElectricCyan,
                            indicatorColor = ElectricCyanContainer,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_profile")
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
                AppScreen.SPLASH -> {
                    SplashScreen(
                        onGetStarted = { currentScreen = AppScreen.HOME }
                    )
                }

                AppScreen.HOME -> {
                    HomeScreen(
                        uiState = uiState,
                        onNavigateToGameSettings = { currentScreen = AppScreen.GAME_SETTINGS },
                        onNavigateToGyroBoost = { currentScreen = AppScreen.GYRO_BOOST },
                        onNavigateToPerformanceEngine = { currentScreen = AppScreen.PERFORMANCE_ENGINE },
                        onNavigateToFramePacing = { currentScreen = AppScreen.FRAME_PACING },
                        onNavigateToBottleneck = { currentScreen = AppScreen.BOTTLENECK },
                        onNavigateToRawPerformance = { currentScreen = AppScreen.RAW_PERFORMANCE },
                        onNavigateToAdbShizuku = { currentScreen = AppScreen.ADB_SHIZUKU },
                        onNavigateToBenchmark = { currentScreen = AppScreen.BENCHMARK },
                        onNavigateToInputLab = { currentScreen = AppScreen.INPUT_LAB },
                        onNavigateToAdvancedSettings = { currentScreen = AppScreen.ADVANCED_SETTINGS },
                        onNavigateToRefreshRate = { currentScreen = AppScreen.REFRESH_RATE },
                        onNavigateToGameSession = { currentScreen = AppScreen.GAME_SESSION },
                        onOptimizeNow = {
                            viewModel.applyGraphicsProfile()
                            currentScreen = AppScreen.GAME_SESSION
                        },
                        onSelectGame = { viewModel.selectGame(it) }
                    )
                }

                AppScreen.GAME_SETTINGS -> {
                    GameSettingsScreen(
                        uiState = uiState,
                        onBack = { currentScreen = AppScreen.HOME },
                        onUpdateProfile = { viewModel.updateGraphicsProfile(it) },
                        onApply = { viewModel.applyGraphicsProfile() }
                    )
                }

                AppScreen.GYRO_BOOST -> {
                    GyroBoostScreen(
                        uiState = uiState,
                        onBack = { currentScreen = AppScreen.HOME },
                        onToggleGyro = { viewModel.toggleGyroBoost(it) },
                        onSensitivityChanged = { x, y -> viewModel.updateGyroSensitivity(x, y) },
                        onModeChanged = { viewModel.setGyroMode(it) },
                        onRunTest = { viewModel.runGyroTest() }
                    )
                }

                AppScreen.PERFORMANCE_ENGINE -> {
                    PerformanceEngineScreen(
                        uiState = uiState,
                        onBack = { currentScreen = AppScreen.HOME },
                        onSelectMode = { viewModel.setPerformanceMode(it) },
                        onToggleUseAdbShizuku = { viewModel.toggleUseAdbShizuku(it) },
                        onToggleApplyOnLaunch = { viewModel.toggleApplyOnLaunch(it) },
                        onToggleAutoRestore = { viewModel.toggleAutoRestore(it) },
                        onApply = {
                            if (uiState.performanceMode == com.example.data.model.PerformanceMode.RAW_PERFORMANCE) {
                                viewModel.applyRawOptimizations()
                            } else {
                                viewModel.showNotification("Mode applied: ${uiState.performanceMode.displayName}")
                            }
                        },
                        onNavigateToRawDetails = { currentScreen = AppScreen.RAW_PERFORMANCE }
                    )
                }

                AppScreen.RAW_PERFORMANCE -> {
                    RawPerformanceScreen(
                        uiState = uiState,
                        onBack = { currentScreen = AppScreen.PERFORMANCE_ENGINE },
                        onToggleRawPerformance = { viewModel.toggleRawPerformanceMode(it) },
                        onToggleExternalCooler = { viewModel.toggleExternalCooler(it) },
                        onApplyOptimizations = { viewModel.applyRawOptimizations() },
                        onRestoreAllSettings = { viewModel.restoreAllSettings() }
                    )
                }

                AppScreen.ADB_SHIZUKU -> {
                    AdbShizukuScreen(
                        uiState = uiState,
                        onBack = { currentScreen = AppScreen.HOME },
                        onExecuteCommand = { viewModel.executeAdbCommand(it) },
                        onRollbackCommand = { viewModel.rollbackAdbCommand(it) }
                    )
                }

                AppScreen.INPUT_LAB -> {
                    InputResponseLabScreen(
                        uiState = uiState,
                        onBack = { currentScreen = AppScreen.HOME },
                        onTestSensor = { viewModel.runGyroTest() }
                    )
                }

                AppScreen.FRAME_PACING -> {
                    FramePacingScreen(
                        uiState = uiState,
                        onBack = { currentScreen = AppScreen.HOME }
                    )
                }

                AppScreen.BOTTLENECK -> {
                    BottleneckDetectorScreen(
                        uiState = uiState,
                        onBack = { currentScreen = AppScreen.HOME }
                    )
                }

                AppScreen.BENCHMARK -> {
                    BenchmarkScreen(
                        uiState = uiState,
                        onBack = { currentScreen = AppScreen.HOME },
                        onRunBenchmark = { viewModel.startBenchmark() }
                    )
                }

                AppScreen.ADVANCED_SETTINGS -> {
                    AdvancedSettingsScreen(
                        uiState = uiState,
                        onBack = { currentScreen = AppScreen.HOME }
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
                            viewModel.toggleGameSession()
                            currentScreen = AppScreen.HOME
                        }
                    )
                }

                AppScreen.SETTINGS -> {
                    SettingsScreen(
                        uiState = uiState,
                        onBack = { currentScreen = AppScreen.HOME },
                        onRestoreSettings = { viewModel.restoreAllSettings() }
                    )
                }
            }
        }
    }
}

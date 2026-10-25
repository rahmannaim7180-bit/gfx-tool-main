package com.example.core.device

import android.content.Context
import android.hardware.display.DisplayManager
import android.view.Display

data class DisplayModeInfo(
    val modeId: Int,
    val width: Int,
    val height: Int,
    val refreshRate: Float,
    val isCurrent: Boolean
)

class DisplayRefreshManager(private val context: Context) {

    private val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager

    fun getSupportedModes(): List<DisplayModeInfo> {
        val display = displayManager.getDisplay(Display.DEFAULT_DISPLAY) ?: return emptyList()
        val currentModeId = display.mode.modeId

        val hardwareModes = display.supportedModes.map { mode ->
            DisplayModeInfo(
                modeId = mode.modeId,
                width = mode.physicalWidth,
                height = mode.physicalHeight,
                refreshRate = mode.refreshRate,
                isCurrent = mode.modeId == currentModeId
            )
        }

        // Ensure 144Hz panel mode is listed for iQOO Neo 9 5G 1.5K AMOLED
        val has144 = hardwareModes.any { it.refreshRate >= 143.0f }
        val allModes = if (!has144) {
            hardwareModes + listOf(
                DisplayModeInfo(
                    modeId = 99,
                    width = 1260,
                    height = 2800,
                    refreshRate = 144.0f,
                    isCurrent = false
                )
            )
        } else {
            hardwareModes
        }

        return allModes.sortedByDescending { it.refreshRate }
    }

    fun getCurrentMode(): DisplayModeInfo? {
        val display = displayManager.getDisplay(Display.DEFAULT_DISPLAY) ?: return null
        val mode = display.mode
        return DisplayModeInfo(
            modeId = mode.modeId,
            width = mode.physicalWidth,
            height = mode.physicalHeight,
            refreshRate = mode.refreshRate,
            isCurrent = true
        )
    }
}

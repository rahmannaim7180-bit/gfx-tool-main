package com.example.core.device

import android.content.Context
import android.hardware.display.DisplayManager
import android.view.Display

data class DisplayModeInfo(
    val modeId: Int,
    val width: Int,
    val height: Int,
    val refreshRate: Float,
    val isCurrent: Boolean = false
)

class DisplayRefreshManager(private val context: Context) {
    private val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as? DisplayManager

    fun getCurrentMode(): DisplayModeInfo {
        val display = displayManager?.getDisplay(Display.DEFAULT_DISPLAY)
        val mode = display?.mode
        return DisplayModeInfo(
            modeId = mode?.modeId ?: 0,
            width = mode?.physicalWidth ?: 1080,
            height = mode?.physicalHeight ?: 2400,
            refreshRate = mode?.refreshRate ?: 60f,
            isCurrent = true
        )
    }

    fun getSupportedModes(): List<DisplayModeInfo> {
        val display = displayManager?.getDisplay(Display.DEFAULT_DISPLAY)
        val currentModeId = display?.mode?.modeId ?: -1
        return display?.supportedModes?.map { m ->
            DisplayModeInfo(
                modeId = m.modeId,
                width = m.physicalWidth,
                height = m.physicalHeight,
                refreshRate = m.refreshRate,
                isCurrent = m.modeId == currentModeId
            )
        } ?: listOf(getCurrentMode())
    }
}

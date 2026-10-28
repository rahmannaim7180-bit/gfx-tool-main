package com.example.data.local.entity

data class ProfileEntity(
    val id: String,
    val name: String,
    val gamePackage: String,
    val profileType: String,
    val graphicsQuality: String,
    val frameRateTarget: String,
    val style: String,
    val resolution: String,
    val renderScalePercent: Int,
    val shadows: String,
    val antiAliasing: String,
    val textureQuality: String,
    val effectsQuality: String,
    val audioQuality: String,
    val gyroBoostEnabled: Boolean = false,
    val gyroXSensitivity: Int = 100,
    val gyroYSensitivity: Int = 100,
    val performanceMode: String = "BALANCED",
    val verificationState: String = "UNVERIFIED",
    val lastAppliedResult: String = "",
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

package com.example.core.profile

import com.example.data.local.dao.ProfileDao
import com.example.data.local.entity.ProfileEntity
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class ProfileRepository(private val profileDao: ProfileDao) {

    fun getProfilesForGame(pkg: String): Flow<List<GraphicsProfile>> {
        return profileDao.getProfilesForGame(pkg).map { list ->
            if (list.isEmpty()) {
                listOf(getDefaultProfile(pkg))
            } else {
                list.map { it.toModel() }
            }
        }
    }

    suspend fun saveProfile(profile: GraphicsProfile) {
        profileDao.saveProfile(profile.toEntity())
    }

    suspend fun duplicateProfile(profile: GraphicsProfile): GraphicsProfile {
        val newId = "profile_${UUID.randomUUID().toString().take(8)}"
        val duplicated = profile.copy(
            id = newId,
            name = "${profile.name} (Copy)",
            stateBadge = ProfileStateBadge.SAVED_PREFERENCE
        )
        profileDao.saveProfile(duplicated.toEntity())
        return duplicated
    }

    suspend fun deleteProfile(id: String) {
        profileDao.deleteProfile(id)
    }

    suspend fun resetProfileToDefaults(profile: GraphicsProfile): GraphicsProfile {
        val reset = getDefaultProfile(profile.gamePackage).copy(id = profile.id, name = profile.name)
        profileDao.saveProfile(reset.toEntity())
        return reset
    }

    fun exportProfileJson(profile: GraphicsProfile): String {
        return """
        {
            "id": "${profile.id}",
            "name": "${profile.name}",
            "gamePackage": "${profile.gamePackage}",
            "quality": "${profile.quality.name}",
            "frameRate": "${profile.frameRate.name}",
            "style": "${profile.style.name}",
            "resolution": "${profile.resolution.name}",
            "renderScalePercent": ${profile.renderScalePercent},
            "shadows": "${profile.shadows.name}",
            "antiAliasing": "${profile.antiAliasing.name}",
            "textureQuality": "${profile.textureQuality.name}",
            "effectsQuality": "${profile.effectsQuality.name}",
            "audioQuality": "${profile.audioQuality.name}"
        }
        """.trimIndent()
    }

    fun getDefaultProfile(gamePackage: String): GraphicsProfile {
        return GraphicsProfile(
            id = "default_${gamePackage.replace('.', '_')}",
            name = "Stable Competitive 90",
            gamePackage = gamePackage,
            type = ProfileType.SMOOTH_COMPETITIVE,
            quality = GraphicsQuality.SMOOTH,
            frameRate = FrameRateTarget.FPS_90,
            style = GraphicsStyle.CLASSIC,
            resolution = ResolutionPreset.AUTO,
            renderScalePercent = 100,
            shadows = ShadowQuality.DISABLED,
            antiAliasing = AntiAliasingMode.DISABLED,
            textureQuality = TextureQuality.LOW,
            effectsQuality = EffectsQuality.LOW,
            audioQuality = AudioQuality.HIGH,
            stateBadge = ProfileStateBadge.SAVED_PREFERENCE,
            statusSummary = "Default competitive profile: Smooth, 90 FPS target"
        )
    }

    private fun ProfileEntity.toModel(): GraphicsProfile {
        return GraphicsProfile(
            id = this.id,
            name = this.name,
            gamePackage = this.gamePackage,
            type = runCatching { ProfileType.valueOf(this.profileType) }.getOrDefault(ProfileType.SMOOTH_COMPETITIVE),
            quality = runCatching { GraphicsQuality.valueOf(this.graphicsQuality) }.getOrDefault(GraphicsQuality.SMOOTH),
            frameRate = runCatching { FrameRateTarget.valueOf(this.frameRateTarget) }.getOrDefault(FrameRateTarget.FPS_90),
            style = runCatching { GraphicsStyle.valueOf(this.style) }.getOrDefault(GraphicsStyle.CLASSIC),
            resolution = runCatching { ResolutionPreset.valueOf(this.resolution) }.getOrDefault(ResolutionPreset.AUTO),
            renderScalePercent = this.renderScalePercent,
            shadows = runCatching { ShadowQuality.valueOf(this.shadows) }.getOrDefault(ShadowQuality.DISABLED),
            antiAliasing = runCatching { AntiAliasingMode.valueOf(this.antiAliasing) }.getOrDefault(AntiAliasingMode.DISABLED),
            textureQuality = runCatching { TextureQuality.valueOf(this.textureQuality) }.getOrDefault(TextureQuality.LOW),
            effectsQuality = runCatching { EffectsQuality.valueOf(this.effectsQuality) }.getOrDefault(EffectsQuality.LOW),
            audioQuality = runCatching { AudioQuality.valueOf(this.audioQuality) }.getOrDefault(AudioQuality.HIGH),
            stateBadge = runCatching { ProfileStateBadge.valueOf(this.verificationState) }.getOrDefault(ProfileStateBadge.SAVED_PREFERENCE),
            isApplied = this.verificationState == ProfileStateBadge.APPLIED.name || this.verificationState == ProfileStateBadge.VERIFIED_ACTIVE.name,
            statusSummary = this.lastAppliedResult
        )
    }

    private fun GraphicsProfile.toEntity(): ProfileEntity {
        return ProfileEntity(
            id = this.id,
            gamePackage = this.gamePackage,
            name = this.name,
            profileType = this.type.name,
            graphicsQuality = this.quality.name,
            frameRateTarget = this.frameRate.name,
            style = this.style.name,
            resolution = this.resolution.name,
            renderScalePercent = this.renderScalePercent,
            shadows = this.shadows.name,
            antiAliasing = this.antiAliasing.name,
            textureQuality = this.textureQuality.name,
            effectsQuality = this.effectsQuality.name,
            audioQuality = this.audioQuality.name,
            verificationState = this.stateBadge.name,
            lastAppliedResult = this.statusSummary,
            updatedAt = System.currentTimeMillis()
        )
    }
}

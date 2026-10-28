package com.example.data.local.dao

import com.example.data.local.entity.ProfileEntity
import kotlinx.coroutines.flow.Flow

interface ProfileDao {
    fun getAllProfiles(): Flow<List<ProfileEntity>>
    fun getProfilesForGame(gamePackage: String): Flow<List<ProfileEntity>>
    suspend fun getProfileById(id: String): ProfileEntity?
    suspend fun getDefaultProfileForGame(gamePackage: String): ProfileEntity?
    suspend fun saveProfile(profile: ProfileEntity)
    suspend fun updateProfile(profile: ProfileEntity)
    suspend fun deleteProfile(id: String)
    suspend fun deleteAllForGame(gamePackage: String)
}

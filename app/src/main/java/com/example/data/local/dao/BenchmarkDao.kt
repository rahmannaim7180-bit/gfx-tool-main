package com.example.data.local.dao

import com.example.data.local.entity.BenchmarkEntity
import kotlinx.coroutines.flow.Flow

interface BenchmarkDao {
    fun getBenchmarksForGame(gamePackage: String): Flow<List<BenchmarkEntity>>
    suspend fun insertBenchmark(benchmark: BenchmarkEntity)
    suspend fun clearBenchmarks(gamePackage: String)
}

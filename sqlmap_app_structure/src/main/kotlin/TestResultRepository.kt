package com.sqlmap.app.data.repository

import com.sqlmap.app.data.database.TestResultDao
import com.sqlmap.app.data.database.TestResultEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TestResultRepository @Inject constructor(
    private val testResultDao: TestResultDao
) {
    
    suspend fun saveTestResult(
        targetUrl: String,
        parameter: String,
        injectionType: String,
        isVulnerable: Boolean,
        dbms: String?,
        dbName: String?,
        tableName: String?,
        responseTime: Long,
        payload: String
    ): Long {
        val result = TestResultEntity(
            targetUrl = targetUrl,
            parameter = parameter,
            injectionType = injectionType,
            isVulnerable = isVulnerable,
            dbms = dbms,
            dbName = dbName,
            tableName = tableName,
            responseTime = responseTime,
            payload = payload
        )
        return testResultDao.insertResult(result)
    }
    
    suspend fun updateTestResult(result: TestResultEntity) {
        testResultDao.updateResult(result)
    }
    
    suspend fun deleteTestResult(result: TestResultEntity) {
        testResultDao.deleteResult(result)
    }
    
    suspend fun getTestResultById(id: Int): TestResultEntity? {
        return testResultDao.getResultById(id)
    }
    
    fun getAllTestResults(): Flow<List<TestResultEntity>> {
        return testResultDao.getAllResults()
    }
    
    fun getTestResultsByUrl(url: String): Flow<List<TestResultEntity>> {
        return testResultDao.getResultsByUrl(url)
    }
    
    fun getVulnerableResults(): Flow<List<TestResultEntity>> {
        return testResultDao.getVulnerableResults()
    }
    
    suspend fun deleteOldResults(beforeTime: Long) {
        testResultDao.deleteOldResults(beforeTime)
    }
    
    suspend fun getResultCount(): Int {
        return testResultDao.getResultCount()
    }
    
    suspend fun clearAllResults() {
        // Tüm sonuçları silmek için
        testResultDao.deleteOldResults(System.currentTimeMillis() + 1000)
    }
}

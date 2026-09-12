package com.sqlmap.app.wafw00f.core

import com.sqlmap.app.network.http.HttpClient
import com.sqlmap.app.utils.helpers.LoggingHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import java.util.Base64
import java.util.regex.Pattern

data class WAFDetectionResult(
    val wafDetected: Boolean,
    val wafName: String? = null,
    val confidence: Int = 0,
    val signatures: List<String> = emptyList(),
    val bypassStrategies: List<String> = emptyList()
)

class WAFDetector(
    private val httpClient: HttpClient,
    private val logger: LoggingHelper
) {
    
    private val wafSignatures = mapOf(
        "Cloudflare" to listOf(
            "cf-ray", "cf-request-id", "cf-mitigated", "Server: cloudflare"
        ),
        "Akamai" to listOf(
            "AkamaiGHost", "X-Akamai", "akamai.platform"
        ),
        "ModSecurity" to listOf(
            "ModSecurity", "NAXSI", "mod_security"
        ),
        "AWS WAF" to listOf(
            "x-amz-", "X-AMZ-", "aws"
        ),
        "Palo Alto" to listOf(
            "PaloAltoNetworks", "Prisma"
        ),
        "Fortinet" to listOf(
            "Fortinet", "FortiGate"
        ),
        "Imperva" to listOf(
            "Imperva", "X-Forwarded-By"
        ),
        "F5 BIG-IP" to listOf(
            "BigIP", "F5", "BIGip"
        )
    )
    
    private val bypassStrategies = mapOf(
        "Cloudflare" to listOf(
            "User-Agent rotation",
            "IP rotation",
            "Delay between requests",
            "TLS fingerprint spoofing"
        ),
        "Akamai" to listOf(
            "Residential proxies",
            "Request rate limiting",
            "Header manipulation"
        ),
        "ModSecurity" to listOf(
            "Tamper scripts",
            "Payload encoding",
            "Comment injection"
        ),
        "AWS WAF" to listOf(
            "Geo-IP rotation",
            "Rate-based rule bypass",
            "String matching evasion"
        )
    )
    
    suspend fun detectWAF(targetUrl: String): WAFDetectionResult = withContext(Dispatchers.IO) {
        return@withContext try {
            logger.log("Başlangıç: WAF tespiti - $targetUrl")
            
            val detectedSignatures = mutableListOf<String>()
            var detectedWAF: String? = null
            var maxConfidence = 0
            
            // HTTP isteği gönder
            val response = httpClient.sendRequest(targetUrl, "GET")
            
            // Headers analiz et
            val headers = response.headers
            for ((wafName, signatures) in wafSignatures) {
                for (signature in signatures) {
                    for ((headerName, headerValue) in headers) {
                        if (headerName.contains(signature, ignoreCase = true) ||
                            headerValue.contains(signature, ignoreCase = true)) {
                            detectedSignatures.add("$headerName: $headerValue")
                            if (maxConfidence < 80) {
                                detectedWAF = wafName
                                maxConfidence = 80
                            }
                        }
                    }
                }
            }
            
            // Response body analiz et
            val body = response.body
            for ((wafName, signatures) in wafSignatures) {
                for (signature in signatures) {
                    if (body.contains(signature, ignoreCase = true)) {
                        detectedSignatures.add("Body pattern: $signature")
                        if (maxConfidence < 60) {
                            detectedWAF = wafName
                            maxConfidence = 60
                        }
                    }
                }
            }
            
            // Test payload gönder (probe)
            val testPayload = "'; DROP TABLE users--"
            val probeResponse = httpClient.sendRequest(
                targetUrl + if (targetUrl.contains("?")) "&" else "?",
                "GET",
                testPayload
            )
            
            // Anormal response kontrolü
            if (probeResponse.statusCode == 403 || probeResponse.statusCode == 406) {
                detectedSignatures.add("Probe blocked with ${probeResponse.statusCode}")
                if (detectedWAF == null) {
                    detectedWAF = "Unknown WAF"
                    maxConfidence = 70
                }
            }
            
            val bypasses = if (detectedWAF != null) {
                bypassStrategies[detectedWAF] ?: emptyList()
            } else {
                emptyList()
            }
            
            logger.log("WAF Tespiti Tamamlandı: ${detectedWAF ?: "WAF Yok"} (Güven: %$maxConfidence)")
            
            WAFDetectionResult(
                wafDetected = detectedWAF != null,
                wafName = detectedWAF,
                confidence = maxConfidence,
                signatures = detectedSignatures,
                bypassStrategies = bypasses
            )
            
        } catch (e: Exception) {
            logger.log("WAF Tespiti Hatası: ${e.message}")
            WAFDetectionResult(
                wafDetected = false,
                signatures = listOf("Error: ${e.message}")
            )
        }
    }
    
    suspend fun testWAFBypass(
        targetUrl: String,
        wafName: String,
        strategy: String
    ): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            logger.log("Bypass testi: $strategy for $wafName")
            
            val payload = when (strategy) {
                "User-Agent rotation" -> {
                    // User-Agent'i değiştir ve yeniden dene
                    val ua = listOf(
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64)",
                        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)",
                        "Mozilla/5.0 (X11; Linux x86_64)"
                    ).random()
                    httpClient.sendRequest(targetUrl, "GET", userAgent = ua)
                    true
                }
                "Payload encoding" -> {
                    val encoded = Base64.getEncoder().encodeToString(
                        "'; DROP TABLE users--".toByteArray()
                    )
                    httpClient.sendRequest(targetUrl, "GET", encoded)
                    true
                }
                else -> false
            }
            
            payload
        } catch (e: Exception) {
            logger.log("Bypass hatası: ${e.message}")
            false
        }
    }
}

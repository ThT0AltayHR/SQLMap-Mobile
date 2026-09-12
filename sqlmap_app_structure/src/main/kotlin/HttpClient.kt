package com.sqlmap.app.network.http

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

data class HttpResponse(
    val statusCode: Int,
    val headers: Map<String, String>,
    val body: String,
    val requestTime: Long = 0
)

class HttpClient {
    
    private val client: OkHttpClient
    
    init {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)
            .build()
    }
    
    fun sendRequest(
        url: String,
        method: String = "GET",
        body: String = "",
        headers: Map<String, String> = emptyMap(),
        userAgent: String = "Mozilla/5.0 (Linux; Android 14) SQLMap~"
    ): HttpResponse {
        return try {
            val startTime = System.currentTimeMillis()
            
            val requestBuilder = Request.Builder()
                .url(url)
                .addHeader("User-Agent", userAgent)
            
            // Add custom headers
            for ((key, value) in headers) {
                requestBuilder.addHeader(key, value)
            }
            
            // Set method and body
            when (method.uppercase()) {
                "GET" -> requestBuilder.get()
                "POST" -> {
                    val requestBody = okhttp3.RequestBody.create(
                        okhttp3.MediaType.parse("application/x-www-form-urlencoded"),
                        body
                    )
                    requestBuilder.post(requestBody)
                }
                "PUT" -> {
                    val requestBody = okhttp3.RequestBody.create(
                        okhttp3.MediaType.parse("application/json"),
                        body
                    )
                    requestBuilder.put(requestBody)
                }
                "DELETE" -> requestBuilder.delete()
                "HEAD" -> requestBuilder.head()
            }
            
            val request = requestBuilder.build()
            val response = client.newCall(request).execute()
            
            val elapsed = System.currentTimeMillis() - startTime
            
            val responseHeaders = mutableMapOf<String, String>()
            for (name in response.headers().names()) {
                response.headers()[name]?.let { value ->
                    responseHeaders[name] = value
                }
            }
            
            HttpResponse(
                statusCode = response.code(),
                headers = responseHeaders,
                body = response.body()?.string() ?: "",
                requestTime = elapsed
            )
            
        } catch (e: Exception) {
            HttpResponse(
                statusCode = 0,
                headers = emptyMap(),
                body = "Error: ${e.message}",
                requestTime = 0
            )
        }
    }
    
    fun sendMultipartRequest(
        url: String,
        params: Map<String, String>,
        files: Map<String, String> = emptyMap()
    ): HttpResponse {
        return try {
            val startTime = System.currentTimeMillis()
            
            val multipartBody = okhttp3.MultipartBody.Builder()
                .setType(okhttp3.MultipartBody.FORM)
            
            for ((key, value) in params) {
                multipartBody.addFormDataPart(key, value)
            }
            
            for ((key, filePath) in files) {
                val file = java.io.File(filePath)
                multipartBody.addFormDataPart(
                    key,
                    file.name,
                    okhttp3.RequestBody.create(
                        okhttp3.MediaType.parse("application/octet-stream"),
                        file
                    )
                )
            }
            
            val request = Request.Builder()
                .url(url)
                .post(multipartBody.build())
                .addHeader("User-Agent", "SQLMap~ v1.0")
                .build()
            
            val response = client.newCall(request).execute()
            val elapsed = System.currentTimeMillis() - startTime
            
            val responseHeaders = mutableMapOf<String, String>()
            for (name in response.headers().names()) {
                response.headers()[name]?.let { value ->
                    responseHeaders[name] = value
                }
            }
            
            HttpResponse(
                statusCode = response.code(),
                headers = responseHeaders,
                body = response.body()?.string() ?: "",
                requestTime = elapsed
            )
            
        } catch (e: Exception) {
            HttpResponse(
                statusCode = 0,
                headers = emptyMap(),
                body = "Error: ${e.message}"
            )
        }
    }
}

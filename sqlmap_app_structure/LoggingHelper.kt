package com.sqlmap.app.utils.helpers

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class LoggingHelper(private val context: Context) {
    
    private val logsDir = File(context.getExternalFilesDir(null), "logs")
    private val currentLogFile: File
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)
    private val logs = mutableListOf<String>()
    
    init {
        if (!logsDir.exists()) {
            logsDir.mkdirs()
        }
        
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        currentLogFile = File(logsDir, "sqlmap_$timestamp.log")
        
        Timber.plant(Timber.DebugTree())
    }
    
    fun log(message: String, level: LogLevel = LogLevel.INFO) {
        val timestamp = dateFormat.format(Date())
        val logEntry = "[$timestamp] [${level.name}] $message"
        
        logs.add(logEntry)
        
        // Log to file
        CoroutineScope(Dispatchers.IO).launch {
            try {
                currentLogFile.appendText("$logEntry\n")
            } catch (e: Exception) {
                Timber.e("Failed to write to log file: ${e.message}")
            }
        }
        
        // Log to Timber
        when (level) {
            LogLevel.DEBUG -> Timber.d(message)
            LogLevel.INFO -> Timber.i(message)
            LogLevel.WARNING -> Timber.w(message)
            LogLevel.ERROR -> Timber.e(message)
        }
    }
    
    fun logWAFDetection(wafName: String?, confidence: Int, signatures: List<String>) {
        log("=== WAF DETECTION STARTED ===")
        if (wafName != null) {
            log("✓ WAF DETECTED: $wafName (Confidence: $confidence%)")
            log("Signatures found:")
            signatures.forEach { sig ->
                log("  • $sig")
            }
        } else {
            log("✗ No WAF detected")
        }
        log("=== WAF DETECTION COMPLETED ===")
    }
    
    fun logInjectionTest(
        parameter: String,
        technique: String,
        vulnerable: Boolean,
        responseTime: Long
    ) {
        log("--- Injection Test: $parameter ---")
        log("Technique: $technique")
        log("Vulnerable: ${if (vulnerable) "YES ✓" else "NO ✗"}")
        log("Response Time: ${responseTime}ms")
        log("---")
    }
    
    fun logDatabaseEnum(
        databases: List<String>,
        tables: Map<String, List<String>>
    ) {
        log("=== DATABASE ENUMERATION ===")
        log("Databases found: ${databases.size}")
        databases.forEach { db ->
            log("  ├─ $db")
            tables[db]?.forEach { table ->
                log("  │  └─ $table")
            }
        }
        log("=== END DATABASE ENUMERATION ===")
    }
    
    fun logDumpProgress(progress: Int, table: String, recordCount: Int) {
        log("[DUMP PROGRESS] Table: $table - $progress% - Records: $recordCount")
    }
    
    fun logError(exception: Throwable, context: String = "") {
        log("ERROR in $context: ${exception.message}", LogLevel.ERROR)
        exception.stackTrace.forEach { element ->
            log("  at $element", LogLevel.ERROR)
        }
    }
    
    fun getLogs(): List<String> = logs.toList()
    
    fun clearLogs() {
        logs.clear()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                currentLogFile.delete()
            } catch (e: Exception) {
                Timber.e("Failed to clear log file: ${e.message}")
            }
        }
    }
    
    fun exportLogs(): File? {
        return try {
            val exportFile = File(context.getExternalFilesDir(null), "sqlmap_export_${System.currentTimeMillis()}.txt")
            exportFile.writeText(logs.joinToString("\n"))
            exportFile
        } catch (e: Exception) {
            Timber.e("Failed to export logs: ${e.message}")
            null
        }
    }
    
    fun getRecentLogs(count: Int = 50): List<String> {
        return logs.takeLast(count)
    }
    
    fun searchLogs(keyword: String): List<String> {
        return logs.filter { it.contains(keyword, ignoreCase = true) }
    }
}

enum class LogLevel {
    DEBUG, INFO, WARNING, ERROR
}

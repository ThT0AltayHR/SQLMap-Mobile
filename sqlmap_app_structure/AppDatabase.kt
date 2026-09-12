package com.sqlmap.app.data.database

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Update
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.flow.Flow

// ───────────────────────────────────────────────
// Entities
// ───────────────────────────────────────────────

@Entity(tableName = "test_results")
data class TestResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val targetUrl: String,
    val parameter: String,
    val injectionType: String,
    val isVulnerable: Boolean,
    val dbms: String?,
    val dbName: String?,
    val tableName: String?,
    val responseTime: Long,
    val payload: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "config")
data class ConfigEntity(
    @PrimaryKey
    val key: String,
    val value: String
)

// ───────────────────────────────────────────────
// DAOs
// ───────────────────────────────────────────────

@Dao
interface TestResultDao {
    @Insert
    suspend fun insertResult(result: TestResultEntity): Long

    @Update
    suspend fun updateResult(result: TestResultEntity)

    @Delete
    suspend fun deleteResult(result: TestResultEntity)

    @Query("SELECT * FROM test_results WHERE id = :id")
    suspend fun getResultById(id: Int): TestResultEntity?

    @Query("SELECT * FROM test_results ORDER BY timestamp DESC")
    fun getAllResults(): Flow<List<TestResultEntity>>

    @Query("SELECT * FROM test_results WHERE targetUrl = :url ORDER BY timestamp DESC")
    fun getResultsByUrl(url: String): Flow<List<TestResultEntity>>

    @Query("SELECT * FROM test_results WHERE isVulnerable = 1 ORDER BY timestamp DESC")
    fun getVulnerableResults(): Flow<List<TestResultEntity>>

    @Query("DELETE FROM test_results WHERE timestamp < :beforeTime")
    suspend fun deleteOldResults(beforeTime: Long)

    @Query("SELECT COUNT(*) FROM test_results")
    suspend fun getResultCount(): Int
}

@Dao
interface ConfigDao {
    @Insert
    suspend fun insertConfig(config: ConfigEntity)

    @Update
    suspend fun updateConfig(config: ConfigEntity)

    @Delete
    suspend fun deleteConfig(config: ConfigEntity)

    @Query("SELECT * FROM config WHERE `key` = :key")
    suspend fun getConfig(key: String): ConfigEntity?

    @Query("SELECT * FROM config")
    suspend fun getAllConfigs(): List<ConfigEntity>

    @Query("DELETE FROM config WHERE `key` = :key")
    suspend fun deleteByKey(key: String)
}

// ───────────────────────────────────────────────
// Type Converters
// ───────────────────────────────────────────────

class Converters {
    private val moshi = Moshi.Builder().build()
    private val listType = Types.newParameterizedType(List::class.java, String::class.java)
    private val adapter = moshi.adapter<List<String>>(listType)

    @TypeConverter
    fun fromStringList(value: List<String>?): String? = value?.let { adapter.toJson(it) }

    @TypeConverter
    fun toStringList(value: String?): List<String>? = value?.let { adapter.fromJson(it) }
}

// ───────────────────────────────────────────────
// Database
// ───────────────────────────────────────────────

@Database(
    entities = [TestResultEntity::class, ConfigEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun testResultDao(): TestResultDao
    abstract fun configDao(): ConfigDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sqlmap_app.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

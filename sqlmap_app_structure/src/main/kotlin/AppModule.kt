package com.sqlmap.app.di

import android.content.Context
import com.sqlmap.app.data.database.AppDatabase
import com.sqlmap.app.data.repository.TestResultRepository
import com.sqlmap.app.network.http.HttpClient
import com.sqlmap.app.sqlmap.manager.DatabaseEnumManager
import com.sqlmap.app.sqlmap.manager.DumpManager
import com.sqlmap.app.sqlmap.manager.DumpResultManager
import com.sqlmap.app.sqlmap.manager.InjectionManager
import com.sqlmap.app.utils.helpers.FileHelper
import com.sqlmap.app.utils.helpers.LoggingHelper
import com.sqlmap.app.utils.helpers.NotificationHelper
import com.sqlmap.app.wafw00f.core.WAFDetector
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClient()
    }
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }
    
    @Provides
    @Singleton
    fun provideFileHelper(@ApplicationContext context: Context): FileHelper {
        return FileHelper(context)
    }
    
    @Provides
    @Singleton
    fun provideLoggingHelper(@ApplicationContext context: Context): LoggingHelper {
        return LoggingHelper(context)
    }
    
    @Provides
    @Singleton
    fun provideNotificationHelper(@ApplicationContext context: Context): NotificationHelper {
        return NotificationHelper(context)
    }
    
    @Provides
    @Singleton
    fun provideWAFDetector(
        httpClient: HttpClient,
        loggingHelper: LoggingHelper
    ): WAFDetector {
        return WAFDetector(httpClient, loggingHelper)
    }
    
    @Provides
    @Singleton
    fun provideInjectionManager(
        httpClient: HttpClient,
        loggingHelper: LoggingHelper
    ): InjectionManager {
        return InjectionManager(httpClient, loggingHelper)
    }
    
    @Provides
    @Singleton
    fun provideDatabaseEnumManager(
        httpClient: HttpClient,
        loggingHelper: LoggingHelper
    ): DatabaseEnumManager {
        return DatabaseEnumManager(httpClient, loggingHelper)
    }
    
    @Provides
    @Singleton
    fun provideDumpManager(
        httpClient: HttpClient,
        fileHelper: FileHelper,
        loggingHelper: LoggingHelper
    ): DumpManager {
        return DumpManager(httpClient, fileHelper, loggingHelper)
    }
    
    @Provides
    @Singleton
    fun provideDumpResultManager(
        @ApplicationContext context: Context,
        loggingHelper: LoggingHelper
    ): DumpResultManager {
        return DumpResultManager(context, loggingHelper)
    }

    @Provides
    @Singleton
    fun provideTestResultRepository(
        database: AppDatabase
    ): TestResultRepository {
        return TestResultRepository(database.testResultDao())
    }
}

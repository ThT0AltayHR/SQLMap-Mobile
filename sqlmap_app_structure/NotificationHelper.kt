package com.sqlmap.app.utils.helpers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.sqlmap.app.MainActivity

class NotificationHelper(private val context: Context) {
    
    companion object {
        const val CHANNEL_ID = "sqlmap_notifications"
        const val CHANNEL_NAME = "SQLMap Notifications"
        const val NOTIFICATION_ID_WAF = 1001
        const val NOTIFICATION_ID_INJECTION = 1002
        const val NOTIFICATION_ID_DUMP = 1003
        const val NOTIFICATION_ID_PROGRESS = 1004
    }
    
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    init {
        createNotificationChannels()
    }
    
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Normal notifications channel
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for SQLMap test results"
                enableVibration(true)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(channel)
            
            // High priority channel for critical alerts
            val criticalChannel = NotificationChannel(
                "sqlmap_critical",
                "Critical Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Critical SQLMap alerts"
                enableVibration(true)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(criticalChannel)
        }
    }
    
    fun showWAFDetectedNotification(wafName: String, language: String = "en") {
        val title = when (language) {
            "tr" -> "WAF Tespit Edildi!"
            "ar" -> "تم اكتشاف WAF!"
            else -> "WAF Detected!"
        }
        
        val message = when (language) {
            "tr" -> "Güvenlik duvarı bulundu: $wafName"
            "ar" -> "تم اكتشاف جدار الحماية: $wafName"
            else -> "Web Application Firewall: $wafName"
        }
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 250, 500))
            .setLights(0xFFFF6B35.toInt(), 1000, 3000)
            .setContentIntent(getPendingIntent())
            .build()
        
        notificationManager.notify(NOTIFICATION_ID_WAF, notification)
    }
    
    fun showInjectionStartedNotification(language: String = "en") {
        val title = when (language) {
            "tr" -> "Enjeksiyon Başladı!"
            "ar" -> "بدأ الحقن!"
            else -> "Injection Started!"
        }
        
        val message = when (language) {
            "tr" -> "SQL enjeksiyon testi başlatıldı"
            "ar" -> "بدأ اختبار الحقن"
            else -> "SQL injection testing started"
        }
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(false)
            .setVibrate(longArrayOf(0, 300, 200, 300))
            .setContentIntent(getPendingIntent())
            .build()
        
        notificationManager.notify(NOTIFICATION_ID_INJECTION, notification)
    }
    
    fun showInjectionResultNotification(
        isVulnerable: Boolean,
        technique: String,
        language: String = "en"
    ) {
        val (title, message) = if (isVulnerable) {
            when (language) {
                "tr" -> Pair("Zafiyet Bulundu!", "Teknik: $technique - Sistem savunmasız!")
                "ar" -> Pair("تم العثور على ثغرة!", "التقنية: $technique - النظام عرضة!")
                else -> Pair("Vulnerability Found!", "Technique: $technique - Target is vulnerable!")
            }
        } else {
            when (language) {
                "tr" -> Pair("Zafiyet Bulunamadı", "Hiçbir enjeksiyon tekniği başarılı olmadı")
                "ar" -> Pair("لم يتم العثور على ثغرة", "لم تنجح أي تقنية حقن")
                else -> Pair("No Vulnerability", "No injection technique successful")
            }
        }
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 250, 500))
            .setLights(if (isVulnerable) 0xFFFF5252.toInt() else 0xFF4CAF50.toInt(), 1000, 3000)
            .setContentIntent(getPendingIntent())
            .build()
        
        notificationManager.notify(NOTIFICATION_ID_INJECTION, notification)
    }
    
    fun showDumpProgressNotification(progress: Int, table: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Database Dumping")
            .setContentText("Table: $table - $progress%")
            .setProgress(100, progress, false)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID_DUMP, notification)
    }
    
    fun showDumpCompleteNotification(fileSize: String, language: String = "en") {
        val title = when (language) {
            "tr" -> "Dump Tamamlandı"
            "ar" -> "تم الانتهاء من التفريغ"
            else -> "Database Dump Complete"
        }
        
        val message = when (language) {
            "tr" -> "Veritabanı başarıyla dökülmüştür. Boyut: $fileSize"
            "ar" -> "تم تفريغ قاعدة البيانات بنجاح. الحجم: $fileSize"
            else -> "Database successfully dumped. Size: $fileSize"
        }
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 300, 200, 300, 200, 300))
            .setLights(0xFF4CAF50.toInt(), 1000, 3000)
            .setContentIntent(getPendingIntent())
            .build()
        
        notificationManager.notify(NOTIFICATION_ID_DUMP, notification)
    }
    
    fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }
    
    private fun getPendingIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}

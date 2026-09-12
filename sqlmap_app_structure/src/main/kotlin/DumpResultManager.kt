package com.sqlmap.app.sqlmap.manager

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.FileProvider
import com.sqlmap.app.utils.helpers.LoggingHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileInputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DumpResultManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val logger: LoggingHelper
) {

    /**
     * Dump edilen tüm .db dosyalarını tek bir ZIP arşivine paketler.
     * Dönen File kullanıcıya dosya kartı olarak sunulur.
     */
    fun packageDumpAsZip(
        dumpFiles: List<File>,
        databaseName: String
    ): File? {
        return try {
            val timestamp = System.currentTimeMillis()
            val zipName = "sqlmap_dump_${databaseName}_$timestamp.zip"
            val outputDir = File(context.getExternalFilesDir(null), "results")
            if (!outputDir.exists()) outputDir.mkdirs()

            val zipFile = File(outputDir, zipName)

            ZipOutputStream(zipFile.outputStream().buffered()).use { zos ->
                for (file in dumpFiles) {
                    if (!file.exists()) continue
                    val entry = ZipEntry(file.name)
                    zos.putNextEntry(entry)
                    FileInputStream(file).buffered().use { fis ->
                        fis.copyTo(zos)
                    }
                    zos.closeEntry()
                    logger.log("ZIP'e eklendi: ${file.name}")
                }
            }

            logger.log("ZIP oluşturuldu: ${zipFile.absolutePath} (${zipFile.length() / 1024} KB)")
            zipFile

        } catch (e: Exception) {
            logger.log("ZIP oluşturma hatası: ${e.message}")
            null
        }
    }

    /**
     * SAF (Storage Access Framework) ile kullanıcının seçtiği klasöre ZIP'i kopyalar.
     * launcher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/zip"))
     */
    fun triggerSaveZip(
        zipFile: File,
        launcher: ActivityResultLauncher<String>
    ) {
        launcher.launch(zipFile.name)
    }

    /**
     * SAF callback'inden gelen Uri'ye ZIP içeriğini yazar.
     */
    fun writeZipToUri(zipFile: File, destinationUri: Uri): Boolean {
        return try {
            context.contentResolver.openOutputStream(destinationUri)?.use { out ->
                FileInputStream(zipFile).buffered().use { fis ->
                    fis.copyTo(out)
                }
            }
            logger.log("ZIP kopyalandı: $destinationUri")
            true
        } catch (e: Exception) {
            logger.log("ZIP kopyalama hatası: ${e.message}")
            false
        }
    }

    /**
     * Sistem paylaşım sayfasını aç — kullanıcı istediği uygulamaya/klasöre gönderebilir.
     */
    fun shareZip(zipFile: File): Intent {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            zipFile
        )
        return Intent(Intent.ACTION_SEND).apply {
            type = "application/zip"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    fun getZipFileSize(zipFile: File): String {
        val bytes = zipFile.length()
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            else -> "${bytes / (1024 * 1024)} MB"
        }
    }
}

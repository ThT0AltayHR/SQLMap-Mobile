package com.sqlmap.app.ui.viewmodel

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sqlmap.app.sqlmap.manager.DumpResultManager
import com.sqlmap.app.utils.helpers.NotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

data class DumpResultState(
    val isLoading: Boolean = false,
    val zipFile: File? = null,
    val zipSize: String = "",
    val errorMessage: String? = null
)

@HiltViewModel
class DumpResultViewModel @Inject constructor(
    private val dumpResultManager: DumpResultManager,
    private val notificationHelper: NotificationHelper
) : ViewModel() {

    private val _state = MutableStateFlow(DumpResultState())
    val state: StateFlow<DumpResultState> = _state

    fun buildZip(dumpFiles: List<File>, databaseName: String) {
        viewModelScope.launch {
            _state.value = DumpResultState(isLoading = true)

            val zip = withContext(Dispatchers.IO) {
                dumpResultManager.packageDumpAsZip(dumpFiles, databaseName)
            }

            if (zip != null) {
                _state.value = DumpResultState(
                    isLoading = false,
                    zipFile = zip,
                    zipSize = dumpResultManager.getZipFileSize(zip)
                )
                notificationHelper.showDumpCompleteNotification(
                    fileSize = dumpResultManager.getZipFileSize(zip)
                )
            } else {
                _state.value = DumpResultState(
                    isLoading = false,
                    errorMessage = "ZIP oluşturulamadı"
                )
            }
        }
    }

    /**
     * SAF callback — seçilen Uri'ye ZIP'i yaz.
     */
    fun writeZipToDestination(zipFile: File, uri: Uri): Boolean {
        return dumpResultManager.writeZipToUri(zipFile, uri)
    }

    /**
     * Sistem paylaşım intent'i — diğer uygulama veya dosya yöneticisine gönderim.
     */
    fun getShareIntent(zipFile: File): Intent {
        return dumpResultManager.shareZip(zipFile)
    }
}

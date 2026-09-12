package com.sqlmap.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sqlmap.app.utils.helpers.NotificationHelper
import com.sqlmap.app.wafw00f.core.WAFDetector
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WAFDetectionState(
    val isDetecting: Boolean = false,
    val wafName: String? = null,
    val confidence: Int = 0,
    val signatures: List<String> = emptyList(),
    val bypassStrategies: List<String> = emptyList(),
    val statusText: String = "Ready",
    val errorMessage: String? = null
)

@HiltViewModel
class WAFDetectionViewModel @Inject constructor(
    private val wafDetector: WAFDetector,
    private val notificationHelper: NotificationHelper
) : ViewModel() {

    private val _state = MutableStateFlow(WAFDetectionState())
    val state: StateFlow<WAFDetectionState> = _state

    fun startDetection(targetUrl: String) {
        viewModelScope.launch {
            _state.value = WAFDetectionState(
                isDetecting = true,
                statusText = "Sending probe requests..."
            )

            try {
                _state.value = _state.value.copy(statusText = "Analyzing response headers...")
                val result = wafDetector.detectWAF(targetUrl)

                _state.value = WAFDetectionState(
                    isDetecting = false,
                    wafName = result.wafName,
                    confidence = result.confidence,
                    signatures = result.signatures,
                    bypassStrategies = result.bypassStrategies,
                    statusText = if (result.wafDetected)
                        "WAF Detected: ${result.wafName}"
                    else
                        "No WAF detected"
                )

                if (result.wafDetected && result.wafName != null) {
                    notificationHelper.showWAFDetectedNotification(result.wafName)
                }

            } catch (e: Exception) {
                _state.value = WAFDetectionState(
                    isDetecting = false,
                    errorMessage = e.message ?: "Unknown error",
                    statusText = "Detection failed"
                )
            }
        }
    }
}

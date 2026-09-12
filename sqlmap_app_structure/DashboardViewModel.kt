package com.sqlmap.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sqlmap.app.data.repository.TestResultRepository
import com.sqlmap.app.sqlmap.manager.InjectionManager
import com.sqlmap.app.wafw00f.core.WAFDetector
import com.sqlmap.app.utils.helpers.NotificationHelper
import com.sqlmap.app.utils.helpers.LoggingHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardState(
    val targetUrl: String = "",
    val selectedParameter: String = "id",
    val isTesting: Boolean = false,
    val wafDetected: String? = null,
    val injectionVulnerable: Boolean = false,
    val currentStatus: String = "Ready",
    val logs: List<String> = emptyList()
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val injectionManager: InjectionManager,
    private val wafDetector: WAFDetector,
    private val testResultRepository: TestResultRepository,
    private val notificationHelper: NotificationHelper,
    private val logger: LoggingHelper
) : ViewModel() {
    
    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state
    
    fun updateTargetUrl(url: String) {
        _state.value = _state.value.copy(targetUrl = url)
    }
    
    fun updateParameter(parameter: String) {
        _state.value = _state.value.copy(selectedParameter = parameter)
    }
    
    fun startWAFDetection() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isTesting = true,
                currentStatus = "Detecting WAF..."
            )
            
            logger.log("WAF detection başlayıyor: ${_state.value.targetUrl}")
            
            try {
                val result = wafDetector.detectWAF(_state.value.targetUrl)
                
                _state.value = _state.value.copy(
                    wafDetected = result.wafName,
                    isTesting = false,
                    currentStatus = "WAF Detection Complete"
                )
                
                if (result.wafDetected) {
                    notificationHelper.showWAFDetectedNotification(result.wafName ?: "Unknown")
                    logger.logWAFDetection(result.wafName, result.confidence, result.signatures)
                }
            } catch (e: Exception) {
                logger.logError(e, "WAF Detection")
                _state.value = _state.value.copy(
                    isTesting = false,
                    currentStatus = "WAF Detection Failed"
                )
            }
        }
    }
    
    fun startInjectionTest() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isTesting = true,
                currentStatus = "Testing SQL Injection..."
            )
            
            logger.log("Enjeksiyon testi başlayıyor")
            notificationHelper.showInjectionStartedNotification()
            
            try {
                val result = injectionManager.testInjection(
                    targetUrl = _state.value.targetUrl,
                    parameter = _state.value.selectedParameter
                )
                
                _state.value = _state.value.copy(
                    injectionVulnerable = result.vulnerable,
                    isTesting = false,
                    currentStatus = if (result.vulnerable) "VULNERABLE FOUND!" else "Not Vulnerable"
                )
                
                // Sonucu kaydet
                testResultRepository.saveTestResult(
                    targetUrl = _state.value.targetUrl,
                    parameter = _state.value.selectedParameter,
                    injectionType = result.technique?.name ?: "UNKNOWN",
                    isVulnerable = result.vulnerable,
                    dbms = result.dbms,
                    dbName = null,
                    tableName = null,
                    responseTime = 0,
                    payload = ""
                )
                
                // Notification gönder
                notificationHelper.showInjectionResultNotification(
                    isVulnerable = result.vulnerable,
                    technique = result.technique?.name ?: "Unknown"
                )
                
                logger.log("Enjeksiyon test tamamlandı: ${result.technique}")
                
            } catch (e: Exception) {
                logger.logError(e, "Injection Test")
                _state.value = _state.value.copy(
                    isTesting = false,
                    currentStatus = "Test Failed"
                )
            }
        }
    }
    
    fun addLog(message: String) {
        val newLogs = _state.value.logs.toMutableList()
        newLogs.add("[${System.currentTimeMillis()}] $message")
        
        // Keep only last 100 logs
        if (newLogs.size > 100) {
            newLogs.removeAt(0)
        }
        
        _state.value = _state.value.copy(logs = newLogs)
    }
    
    fun clearLogs() {
        _state.value = _state.value.copy(logs = emptyList())
        logger.log("Logs cleared")
    }
    
    fun clearAll() {
        viewModelScope.launch {
            _state.value = DashboardState()
            testResultRepository.clearAllResults()
            logger.clearLogs()
        }
    }
}

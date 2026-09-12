package com.sqlmap.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sqlmap.app.data.repository.TestResultRepository
import com.sqlmap.app.sqlmap.manager.InjectionManager
import com.sqlmap.app.sqlmap.manager.InjectionTechnique
import com.sqlmap.app.utils.helpers.NotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InjectionTestResultItem(
    val technique: String,
    val payload: String,
    val isVulnerable: Boolean?,
    val responseTime: Long = 0
)

data class InjectionState(
    val isTesting: Boolean = false,
    val isComplete: Boolean = false,
    val vulnerable: Boolean = false,
    val foundTechnique: String? = null,
    val detectedDbms: String? = null,
    val currentTechnique: String = "",
    val progress: Float = 0f,
    val testResults: List<InjectionTestResultItem> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class InjectionViewModel @Inject constructor(
    private val injectionManager: InjectionManager,
    private val testResultRepository: TestResultRepository,
    private val notificationHelper: NotificationHelper
) : ViewModel() {

    private val _state = MutableStateFlow(InjectionState())
    val state: StateFlow<InjectionState> = _state

    fun startTest(targetUrl: String, parameter: String) {
        viewModelScope.launch {
            _state.value = InjectionState(isTesting = true)

            notificationHelper.showInjectionStartedNotification()

            try {
                val result = injectionManager.testInjection(
                    targetUrl = targetUrl,
                    parameter = parameter,
                    onTestUpdate = { test, index, total ->
                        _state.update { current ->
                            current.copy(
                                currentTechnique = test.technique.name,
                                progress = index.toFloat() / total,
                                testResults = current.testResults + InjectionTestResultItem(
                                    technique = test.technique.name,
                                    payload = test.payload,
                                    isVulnerable = test.isVulnerable,
                                    responseTime = test.responseTime
                                )
                            )
                        }
                    }
                )

                _state.value = InjectionState(
                    isTesting = false,
                    isComplete = true,
                    vulnerable = result.vulnerable,
                    foundTechnique = result.technique?.name,
                    detectedDbms = result.dbms,
                    progress = 1f,
                    testResults = _state.value.testResults
                )

                // Sonucu kaydet
                testResultRepository.saveTestResult(
                    targetUrl = targetUrl,
                    parameter = parameter,
                    injectionType = result.technique?.name ?: "NONE",
                    isVulnerable = result.vulnerable,
                    dbms = result.dbms,
                    dbName = null,
                    tableName = null,
                    responseTime = 0,
                    payload = result.tests.firstOrNull { it.isVulnerable }?.payload ?: ""
                )

                notificationHelper.showInjectionResultNotification(
                    isVulnerable = result.vulnerable,
                    technique = result.technique?.name ?: "None"
                )

            } catch (e: Exception) {
                _state.update { it.copy(
                    isTesting = false,
                    isComplete = true,
                    errorMessage = e.message ?: "Unknown error"
                ) }
            }
        }
    }
}

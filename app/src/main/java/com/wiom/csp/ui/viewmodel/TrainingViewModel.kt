package com.wiom.csp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wiom.csp.data.repository.OnboardingRepository
import com.wiom.csp.domain.model.TrainingModule
import com.wiom.csp.util.t
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TrainingUiState(
    val modules: List<TrainingModule> = emptyList(),
    val completedModuleIds: Set<String> = emptySet(),
    val activeModuleId: String? = null,
    val videoWatchedMap: Map<String, Boolean> = emptyMap(),
    val quizAnswersMap: Map<String, List<Int?>> = emptyMap(), // moduleId -> list of selected indices
    val isLoading: Boolean = false,
    val allModulesCompleted: Boolean = false,
)

@HiltViewModel
class TrainingViewModel @Inject constructor(
    private val repo: OnboardingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrainingUiState())
    val uiState: StateFlow<TrainingUiState> = _uiState.asStateFlow()

    fun loadModules() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repo.getTrainingModules()
                .onSuccess { modules ->
                    _uiState.update {
                        it.copy(
                            modules = modules,
                            isLoading = false
                        )
                    }
                }
                .onFailure {
                    _uiState.update { it.copy(isLoading = false) }
                }
        }
    }

    fun setModules(modules: List<TrainingModule>) {
        _uiState.update { it.copy(modules = modules) }
    }

    fun openModule(moduleId: String) {
        _uiState.update { it.copy(activeModuleId = moduleId) }
    }

    fun closeModule() {
        _uiState.update { it.copy(activeModuleId = null) }
    }

    fun markVideoWatched(moduleId: String) {
        _uiState.update {
            val updated = it.videoWatchedMap.toMutableMap()
            updated[moduleId] = true
            it.copy(videoWatchedMap = updated)
        }
    }

    fun isVideoWatched(moduleId: String): Boolean {
        return _uiState.value.videoWatchedMap[moduleId] == true
    }

    fun answerQuestion(moduleId: String, questionIndex: Int, selectedOption: Int) {
        _uiState.update { state ->
            val quizMap = state.quizAnswersMap.toMutableMap()
            val module = state.modules.find { it.id == moduleId } ?: return@update state
            val answers = (quizMap[moduleId] ?: List(module.questions.size) { null }).toMutableList()
            if (questionIndex in answers.indices) {
                answers[questionIndex] = selectedOption
            }
            quizMap[moduleId] = answers
            state.copy(quizAnswersMap = quizMap)
        }
    }

    fun getQuizScore(moduleId: String): Pair<Int, Int> {
        val state = _uiState.value
        val module = state.modules.find { it.id == moduleId } ?: return 0 to 0
        val answers = state.quizAnswersMap[moduleId] ?: return 0 to module.questions.size
        val correct = module.questions.indices.count { i ->
            answers.getOrNull(i) == module.questions[i].correctIndex
        }
        return correct to module.questions.size
    }

    fun isQuizComplete(moduleId: String): Boolean {
        val state = _uiState.value
        val module = state.modules.find { it.id == moduleId } ?: return false
        val answers = state.quizAnswersMap[moduleId] ?: return false
        return answers.size == module.questions.size && answers.all { it != null }
    }

    fun completeModule(moduleId: String) {
        _uiState.update { state ->
            val completed = state.completedModuleIds + moduleId
            state.copy(
                completedModuleIds = completed,
                activeModuleId = null,
                allModulesCompleted = state.modules.all { it.id in completed }
            )
        }
    }

    fun isModuleCompleted(moduleId: String): Boolean {
        return moduleId in _uiState.value.completedModuleIds
    }

    fun resetModule(moduleId: String) {
        _uiState.update { state ->
            val completed = state.completedModuleIds - moduleId
            val videoMap = state.videoWatchedMap.toMutableMap().apply { remove(moduleId) }
            val quizMap = state.quizAnswersMap.toMutableMap().apply { remove(moduleId) }
            state.copy(
                completedModuleIds = completed,
                videoWatchedMap = videoMap,
                quizAnswersMap = quizMap,
                allModulesCompleted = state.modules.all { it.id in completed }
            )
        }
    }
}

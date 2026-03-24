package com.wiom.csp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wiom.csp.data.repository.OnboardingRepository
import com.wiom.csp.domain.model.QuizQuestion
import com.wiom.csp.util.t
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PolicyQuizUiState(
    val questions: List<QuizQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val answers: List<Int?> = emptyList(), // selectedOption per question, null = unanswered
    val isFinished: Boolean = false,
    val score: Int = 0,
    val totalQuestions: Int = 0,
    val passed: Boolean = false,
    val isLoading: Boolean = false,
    val showHint: Boolean = false,
) {
    val scorePercentage: Int
        get() = if (totalQuestions > 0) (score * 100) / totalQuestions else 0

    val currentQuestion: QuizQuestion?
        get() = questions.getOrNull(currentQuestionIndex)

    val currentAnswer: Int?
        get() = answers.getOrNull(currentQuestionIndex)

    val isLastQuestion: Boolean
        get() = currentQuestionIndex >= questions.size - 1

    val progressFraction: Float
        get() = if (questions.isEmpty()) 0f
        else (currentQuestionIndex + 1).toFloat() / questions.size
}

@HiltViewModel
class PolicyQuizViewModel @Inject constructor(
    private val repo: OnboardingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PolicyQuizUiState())
    val uiState: StateFlow<PolicyQuizUiState> = _uiState.asStateFlow()

    fun loadQuestions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repo.getPolicyQuizQuestions()
                .onSuccess { questions ->
                    _uiState.update {
                        it.copy(
                            questions = questions,
                            answers = List(questions.size) { null },
                            totalQuestions = questions.size,
                            isLoading = false,
                            currentQuestionIndex = 0,
                            isFinished = false,
                            score = 0,
                            passed = false
                        )
                    }
                }
                .onFailure {
                    _uiState.update { it.copy(isLoading = false) }
                }
        }
    }

    fun setQuestions(questions: List<QuizQuestion>) {
        _uiState.update {
            it.copy(
                questions = questions,
                answers = List(questions.size) { null },
                totalQuestions = questions.size,
                currentQuestionIndex = 0,
                isFinished = false,
                score = 0,
                passed = false
            )
        }
    }

    fun answerQuestion(selectedIndex: Int) {
        _uiState.update { state ->
            val answers = state.answers.toMutableList()
            if (state.currentQuestionIndex in answers.indices) {
                answers[state.currentQuestionIndex] = selectedIndex
            }
            state.copy(answers = answers, showHint = false)
        }
    }

    fun nextQuestion() {
        val state = _uiState.value
        if (state.isLastQuestion) {
            finishQuiz()
        } else {
            _uiState.update {
                it.copy(
                    currentQuestionIndex = it.currentQuestionIndex + 1,
                    showHint = false
                )
            }
        }
    }

    fun previousQuestion() {
        _uiState.update {
            if (it.currentQuestionIndex > 0) {
                it.copy(currentQuestionIndex = it.currentQuestionIndex - 1, showHint = false)
            } else it
        }
    }

    fun toggleHint() {
        _uiState.update { it.copy(showHint = !it.showHint) }
    }

    private fun finishQuiz() {
        _uiState.update { state ->
            val correct = state.questions.indices.count { i ->
                state.answers.getOrNull(i) == state.questions[i].correctIndex
            }
            val passThreshold = (state.totalQuestions * 80) / 100 // 80% to pass
            state.copy(
                isFinished = true,
                score = correct,
                passed = correct >= passThreshold
            )
        }
    }

    fun retryQuiz() {
        _uiState.update {
            it.copy(
                answers = List(it.questions.size) { null },
                currentQuestionIndex = 0,
                isFinished = false,
                score = 0,
                passed = false,
                showHint = false
            )
        }
    }

    fun isAnswerCorrect(questionIndex: Int): Boolean? {
        val state = _uiState.value
        val answer = state.answers.getOrNull(questionIndex) ?: return null
        val question = state.questions.getOrNull(questionIndex) ?: return null
        return answer == question.correctIndex
    }
}

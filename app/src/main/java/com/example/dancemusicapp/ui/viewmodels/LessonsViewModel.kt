// LessonsViewModel.kt
package com.example.dancemusicapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dancemusicapp.local.Lesson
import com.example.dancemusicapp.repository.LessonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LessonsUiState(
    val lessons: List<Lesson> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class LessonsViewModel(
    application: Application,
    private val lessonRepository: LessonRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(LessonsUiState(isLoading = true))
    val uiState: StateFlow<LessonsUiState> = _uiState.asStateFlow()

    init {
        loadLessons()
    }

    private fun loadLessons() {
        viewModelScope.launch {
            try {
                lessonRepository.getAllLessons().collect { lessons ->
                    _uiState.update { currentState ->
                        currentState.copy(lessons = lessons, isLoading = false, error = null)
                    }
                }
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(isLoading = false, error = e.message)
                }
            }
        }
    }

    fun addLesson(lesson: Lesson) {
        viewModelScope.launch {
            try {
                lessonRepository.insertLesson(lesson)
                // loadLessons() вызывать не обязательно, Flow обновит список
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(error = e.message)
                }
            }
        }
    }

    // Оставим addTestLesson для кнопки "Записаться на занятие"
    fun addTestLesson() {
        viewModelScope.launch {
            try {
                val testLesson = Lesson(
                    title = "Тестовое занятие",
                    description = "Это тестовая запись.",
                    timestamp = System.currentTimeMillis(),
                    durationMinutes = 30
                )
                lessonRepository.insertLesson(testLesson)
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(error = e.message)
                }
            }
        }
    }
}
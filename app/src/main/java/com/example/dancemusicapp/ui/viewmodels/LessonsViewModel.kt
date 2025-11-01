// LessonsViewModel.kt
package com.example.dancemusicapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dancemusicapp.local.Lesson // Убедись, что путь правильный
import com.example.dancemusicapp.repository.LessonRepository // Убедись, что путь правильный
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// UI-состояние для занятий
data class LessonsUiState(
    val lessons: List<Lesson> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class LessonsViewModel(
    private val lessonRepository: LessonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LessonsUiState(isLoading = true))
    val uiState: StateFlow<LessonsUiState> = _uiState.asStateFlow()

    init {
        loadLessons()
    }

    private fun loadLessons() {
        viewModelScope.launch {
            try {
                // Предполагается, что lessonRepository.getAllLessons() возвращает Flow<List<Lesson>>
                lessonRepository.getAllLessons().collect { lessons ->
                    _uiState.update { it.copy(lessons = lessons, isLoading = false, error = null) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun addLesson(lesson: Lesson) {
        viewModelScope.launch {
            try {
                lessonRepository.insertLesson(lesson)
                // loadLessons() вызывать не обязательно, Flow обновит список
            } catch (e: Exception) {
                // Обработка ошибки (например, логирование или обновление UI-состояния ошибки)
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    // Оставим addTestLesson для кнопки "Записаться на занятие"
    fun addTestLesson() {
        viewModelScope.launch {
            try {
                val testLesson = Lesson(
                    // id = 0, // ID будет сгенерирован Room
                    title = "Тестовое занятие",
                    description = "Это тестовая запись.",
                    timestamp = System.currentTimeMillis(),
                    durationMinutes = 30
                )
                lessonRepository.insertLesson(testLesson)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}
// LessonsViewModel.kt
package com.example.dancemusicapp.ui.viewmodels // <-- Новое место

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dancemusicapp.local.Lesson // <-- Импорт модели
import com.example.dancemusicapp.repository.LessonRepository // <-- Импорт Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// UI-состояние для занятий
data class LessonsUiState(
    val lessons: List<Lesson> = emptyList(), // Список занятий
    val isLoading: Boolean = false,          // Флаг загрузки
    val error: String? = null               // Ошибка
)

class LessonsViewModel(
    private val lessonRepository: LessonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LessonsUiState(isLoading = true))
    val uiState: StateFlow<LessonsUiState> = _uiState.asStateFlow()

    init {
        // Начинаем наблюдать за изменениями в базе данных через Repository
        // и обновляем UI-состояние
        viewModelScope.launch {
            lessonRepository.getAllLessons().collect { lessons ->
                _uiState.update { it.copy(lessons = lessons, isLoading = false, error = null) }
            }
        }
    }

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
                // Обработка ошибки не обязательна здесь, так как Flow обновит список автоматически
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) } // Обновляем состояние ошибки
            }
        }
    }

    fun addLesson(title: String, description: String, timestamp: Long, durationMinutes: Int) {
        viewModelScope.launch {
            try {
                val newLesson = Lesson(
                    title = title,
                    description = description,
                    timestamp = timestamp,
                    durationMinutes = durationMinutes
                )
                lessonRepository.insertLesson(newLesson)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun deleteLesson(lesson: Lesson) {
        viewModelScope.launch {
            try {
                lessonRepository.deleteLesson(lesson)
                // Обработка ошибки не обязательна, Flow обновит список
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}
// ViewModelFactory.kt
package com.example.dancemusicapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dancemusicapp.repository.LessonRepository
import com.example.dancemusicapp.ui.viewmodels.LessonsViewModel

/**
 * Простая фабрика для создания ViewModel с зависимостями.
 * Можно расширить для других ViewModel.
 */
class ViewModelFactory private constructor(
    private val lessonRepository: LessonRepository? = null
    // Добавь другие зависимости для других ViewModel, если нужно
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LessonsViewModel::class.java) -> {
                LessonsViewModel(lessonRepository ?: throw IllegalArgumentException("LessonRepository must be provided for LessonsViewModel"))
                // as T // Приведение типа нужно, если LessonsViewModel не T. Обычно не требуется.
            }
            // Добавь другие case для других ViewModel
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        } as T
    }

    companion object {
        /**
         * Создаёт фабрику для LessonsViewModel.
         */
        fun forLessonsViewModel(repository: LessonRepository): ViewModelFactory {
            return ViewModelFactory(lessonRepository = repository)
        }
        // Добавь другие factory методы для других ViewModel
    }
}
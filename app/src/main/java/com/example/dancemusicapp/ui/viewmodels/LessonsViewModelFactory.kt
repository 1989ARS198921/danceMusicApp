// LessonsViewModelFactory.kt
package com.example.dancemusicapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dancemusicapp.repository.LessonRepository

class LessonsViewModelFactory private constructor(
    private val application: Application,
    private val lessonRepository: LessonRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LessonsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LessonsViewModel(
                application,
                lessonRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        fun forLessonsViewModel(
            application: Application,
            repository: LessonRepository
        ): LessonsViewModelFactory {
            return LessonsViewModelFactory(
                application = application,
                lessonRepository = repository
            )
        }
    }
}
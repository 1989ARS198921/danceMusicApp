// ViewModelFactory.kt (не рекомендуется для сложных случаев, лучше конкретные фабрики)
package com.example.dancemusicapp

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dancemusicapp.repository.LessonRepository
import com.example.dancemusicapp.repository.PlayerRepository
import com.example.dancemusicapp.ui.viewmodels.ChatViewModel
import com.example.dancemusicapp.ui.viewmodels.LessonsViewModel

class ViewModelFactory private constructor(
    private val application: Application,
    private val lessonRepository: LessonRepository? = null,
    private val playerRepository: PlayerRepository? = null
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        throw IllegalArgumentException("ViewModelFactory не может создать ${modelClass.name}. Используйте конкретную фабрику (например, LessonsViewModelFactory, ChatViewModelFactory).")
    }

    companion object {
        fun forLessonsViewModel(
            application: Application,
            repository: LessonRepository
        ): ViewModelFactory {
            return ViewModelFactory(application = application, lessonRepository = repository)
        }

        fun forChatViewModel(
            application: Application,
            lessonRepo: LessonRepository,
            playerRepo: PlayerRepository
        ): ViewModelFactory {
            return ViewModelFactory(
                application = application,
                lessonRepository = lessonRepo,
                playerRepository = playerRepo
            )
        }
    }
}
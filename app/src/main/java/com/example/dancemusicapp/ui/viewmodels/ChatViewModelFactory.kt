// ChatViewModelFactory.kt
package com.example.dancemusicapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dancemusicapp.controllers.LessonController
import com.example.dancemusicapp.repository.LessonRepository
import com.example.dancemusicapp.repository.PlayerRepository

class ChatViewModelFactory(
    private val application: Application,
    private val lessonRepository: LessonRepository,
    private val playerRepository: PlayerRepository?,
    private val lessonController: LessonController
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(
                application,
                lessonRepository,
                playerRepository,
                lessonController
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        fun create(
            application: Application,
            lessonRepo: LessonRepository,
            playerRepo: PlayerRepository?,
            lessonCtrl: LessonController
        ): ChatViewModelFactory {
            return ChatViewModelFactory(
                application = application,
                lessonRepository = lessonRepo,
                playerRepository = playerRepo,
                lessonController = lessonCtrl
            )
        }
    }
}
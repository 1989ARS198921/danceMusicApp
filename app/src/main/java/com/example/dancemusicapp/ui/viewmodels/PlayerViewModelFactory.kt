// PlayerViewModelFactory.kt
package com.example.dancemusicapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dancemusicapp.repository.PlayerRepository // <-- Импорт PlayerRepository (если используется)

class PlayerViewModelFactory private constructor(
    private val application: Application,
    private val playerRepository: PlayerRepository? = null // Может быть null, если PlayerViewModel не использует Repository напрямую
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlayerViewModel(
                application // <-- Передаём Application в конструктор PlayerViewModel
                // playerRepository // <-- Передай playerRepository, если PlayerViewModel его принимает
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        fun create(
            application: Application,
            repository: PlayerRepository? = null // Может быть null
        ): PlayerViewModelFactory {
            return PlayerViewModelFactory(
                application = application,
                playerRepository = repository
            )
        }
    }
}
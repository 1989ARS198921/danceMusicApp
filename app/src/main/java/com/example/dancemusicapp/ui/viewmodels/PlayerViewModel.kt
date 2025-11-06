// PlayerViewModel.kt
package com.example.dancemusicapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dancemusicapp.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn


// UI-состояние для плеера
data class PlayerUiState(
    val songs: List<Song> = emptyList(),
    val currentSong: Song? = null,
    val isPlaying: Boolean = false,
    // ... другие поля
)

// Меняем наследование с ViewModel на AndroidViewModel
class PlayerViewModel(
    application: Application // <-- Принимаем Application
) : AndroidViewModel(application) { // <-- Наследуемся от AndroidViewModel

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    // Свойство для потока песен (если нужно отдельно)
    val songsState: StateFlow<List<Song>> = _uiState.map { it.songs }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Методы для управления плеером
    fun playSong(path: String) {
        // Логика воспроизведения песни по пути path
        // Например, обновление _uiState, чтобы currentSong = ..., isPlaying = true
        _uiState.value = _uiState.value.copy(
            currentSong = _uiState.value.songs.find { it.path == path }, // Пример поиска
            isPlaying = true
        )
    }

    fun play() {
        // Логика продолжения воспроизведения
        _uiState.value = _uiState.value.copy(isPlaying = true)
    }

    fun pause() {
        // Логика паузы
        _uiState.value = _uiState.value.copy(isPlaying = false)
    }

    fun stop() {
        // Логика остановки
        _uiState.value = _uiState.value.copy(isPlaying = false, currentSong = null)
    }
    // ... остальные методы ...
}
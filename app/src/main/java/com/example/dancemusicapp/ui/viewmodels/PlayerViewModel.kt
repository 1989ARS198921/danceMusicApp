// PlayerViewModel.kt
package com.example.dancemusicapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dancemusicapp.Song
import kotlinx.coroutines.flow.* // <-- Импортируем все необходимые элементы из kotlinx.coroutines.flow

data class PlayerUiState(
    val songs: List<Song> = emptyList(),
    val currentSong: Song? = null,
    val isPlaying: Boolean = false,
    // ... другие поля
)

class PlayerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    // Если ты хочешь отдельный StateFlow для песен
    // Теперь map, SharingStarted, stateIn будут найдены благодаря import kotlinx.coroutines.flow.*
    val songsState: StateFlow<List<Song>> = _uiState.map { it.songs }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000), // 5 секунд
        initialValue = emptyList()
    )

    // ... другие методы

    // --- Добавь недостающие методы, которые вызываются из PlayerFragment ---
    fun playSong(path: String) {
        // TODO: Реализовать логику воспроизведения песни по пути path
        // Например, обновить _uiState, чтобы currentSong = ..., isPlaying = true
        _uiState.value = _uiState.value.copy(
            currentSong = _uiState.value.songs.find { it.path == path }, // Пример поиска
            isPlaying = true
        )
    }

    fun play() {
        // TODO: Реализовать продолжение воспроизведения
        _uiState.value = _uiState.value.copy(isPlaying = true)
    }

    fun pause() {
        // TODO: Реализовать паузу
        _uiState.value = _uiState.value.copy(isPlaying = false)
    }

    fun stop() {
        // TODO: Реализовать остановку
        _uiState.value = _uiState.value.copy(isPlaying = false, currentSong = null)
    }
    // --- Конец добавленных методов ---
}
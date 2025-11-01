// PlayerRepository.kt
package com.example.dancemusicapp.repository

import com.example.dancemusicapp.Song
import kotlinx.coroutines.flow.Flow

class PlayerRepository {
    // Пример метода
    fun playDefaultMusic() {
        // Реализуй логику воспроизведения, например, через PlayerService или напрямую MediaPlayer
        println("Playing default music...") // Заглушка
    }

    // Пример метода получения списка песен
    fun getSongs(): Flow<List<Song>> {
        // Здесь можно получить список из ресурсов, базы данных (если будет) или API
        return kotlinx.coroutines.flow.flowOf(
            listOf(
                Song(1, "Song 1", "Artist 1", "path1"),
                Song(2, "Song 2", "Artist 2", "path2")
            )
        )
    }
}
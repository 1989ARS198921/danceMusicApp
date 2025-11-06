// PlayerRepository.kt
package com.example.dancemusicapp.repository

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import com.example.dancemusicapp.R // <-- Добавь этот import

class PlayerRepository(private val context: Context) { // Принимает Context

    private var mediaPlayer: MediaPlayer? = null
    private var currentSongPath: String? = null

    fun playMusic(path: String) {
        if (currentSongPath == path && mediaPlayer?.isPlaying == true) {
            Log.d("PlayerRepository", "Music at $path is already playing.")
            return
        }

        stopMusic() // Останавливаем текущую песню

        try {
            val uri = Uri.parse(path)
            mediaPlayer = MediaPlayer.create(context, uri) // Создаём через Context
            if (mediaPlayer != null) {
                mediaPlayer!!.setOnCompletionListener { mp ->
                    Log.d("PlayerRepository", "Playback completed for $path")
                }
                mediaPlayer!!.start()
                currentSongPath = path
                Log.d("PlayerRepository", "Started playing: $path")
            } else {
                Log.e("PlayerRepository", "Failed to create MediaPlayer for path: $path")
            }
        } catch (e: Exception) {
            Log.e("PlayerRepository", "Error playing music at $path", e)
        }
    }

    fun playDefaultMusic() { // <-- Правильное имя метода
        // Пример: воспроизвести предустановленный трек из ресурсов
        val defaultUri = Uri.parse("android.resource://${context.packageName}/${R.raw.sample}") // Используем sample, если он есть
        playMusic(defaultUri.toString())
    }

    fun pauseMusic() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            Log.d("PlayerRepository", "Paused playback. Current position: ${mediaPlayer?.currentPosition}")
        } else {
            Log.d("PlayerRepository", "Attempted to pause, but player is not playing.")
        }
    }

    fun resumeMusic() {
        if (mediaPlayer?.isPlaying == false && mediaPlayer?.currentPosition != 0) {
            mediaPlayer?.start()
            Log.d("PlayerRepository", "Resumed playback. Current position: ${mediaPlayer?.currentPosition}")
        } else {
            Log.d("PlayerRepository", "Attempted to resume, but player is already playing or has no valid position.")
        }
    }

    fun stopMusic() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null
        currentSongPath = null
        Log.d("PlayerRepository", "Stopped playback and released resources.")
    }

    fun isPlaying(): Boolean = mediaPlayer?.isPlaying == true
    fun getCurrentSongPath(): String? = currentSongPath

    fun getDanceSongsList(): List<com.example.dancemusicapp.Song> = emptyList()
}
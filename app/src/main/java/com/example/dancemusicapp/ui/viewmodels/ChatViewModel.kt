// ChatViewModel.kt
package com.example.dancemusicapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dancemusicapp.ChatMessage
import com.example.dancemusicapp.repository.LessonRepository // Убедись, что импорт правильный
import com.example.dancemusicapp.repository.PlayerRepository // <-- Добавь этот импорт
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// UI-состояние для чата
data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ChatViewModel(
    private val lessonRepository: LessonRepository,
    private val playerRepository: PlayerRepository // <-- Добавь зависимость
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun sendMessage(userMessageText: String) {
        if (userMessageText.isBlank()) return

        val userMessage = ChatMessage(text = userMessageText, isBot = false)

        _uiState.update { currentState ->
            currentState.copy(
                messages = currentState.messages + userMessage,
                isLoading = true
            )
        }

        viewModelScope.launch {
            val botResponse = processUserCommand(userMessageText)
            _uiState.update { currentState ->
                currentState.copy(
                    messages = currentState.messages + botResponse,
                    isLoading = false
                )
            }
        }
    }

    private suspend fun processUserCommand(message: String): ChatMessage {
        val lowerMessage = message.lowercase()
        return if (lowerMessage.contains("танцы") && lowerMessage.contains("запиши")) {
            // Пример: добавление тестовой записи на занятие
            lessonRepository.insertLesson(
                com.example.dancemusicapp.local.Lesson( // Убедись, что путь к Lesson правильный
                    title = "Танцы",
                    description = "Индивидуальное занятие по современным танцам",
                    timestamp = System.currentTimeMillis(),
                    durationMinutes = 60
                )
            )
            ChatMessage(text = "Вы записаны на занятие по танцам!", isBot = true)
        } else if (lowerMessage.contains("музыку") || lowerMessage.contains("включи")) {
            // Вызов метода из PlayerRepository
            playerRepository.playDefaultMusic() // <-- Теперь этот вызов должен работать
            ChatMessage(text = "Включаю музыку для танцев!", isBot = true)
        } else {
            ChatMessage(text = "Простите, я не понимаю. Попробуйте: 'Запиши меня на танцы' или 'Включи музыку для танцев'.", isBot = true)
        }
    }
}
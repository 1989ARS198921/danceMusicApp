// ChatViewModel.kt
package com.example.dancemusicapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dancemusicapp.ChatMessageItem
import com.example.dancemusicapp.PendingLessonData
import com.example.dancemusicapp.controllers.LessonController // <-- Добавь этот import
import com.example.dancemusicapp.repository.LessonRepository
import com.example.dancemusicapp.repository.PlayerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat // <-- Добавь этот import
import java.util.* // <-- Добавь этот import (включает Locale, Date, Calendar)

// UI-состояние для чата
data class ChatUiState(
    val messages: List<ChatMessageItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val pendingLessonData: PendingLessonData? = null
)

class ChatViewModel(
    application: Application,
    private val lessonRepository: LessonRepository,
    private val playerRepository: PlayerRepository?, // <-- Теперь nullable
    private val lessonController: LessonController // <-- Добавляем LessonController как зависимость
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun sendMessage(userMessageText: String) {
        if (userMessageText.isBlank()) return

        val userMessageItem = ChatMessageItem.TextMessage(text = userMessageText, isBot = false)

        _uiState.update { currentState ->
            currentState.copy(
                messages = currentState.messages + userMessageItem,
                isLoading = true
            )
        }

        viewModelScope.launch {
            val botResponseItem = processUserMessageWithContext(userMessageText)
            _uiState.update { currentState ->
                currentState.copy(
                    messages = currentState.messages + botResponseItem,
                    isLoading = false
                )
            }
        }
    }

    private suspend fun processUserMessageWithContext(message: String): ChatMessageItem {
        val lowerMessage = message.lowercase()

        // --- ЛОГИКА ОБРАБОТКИ КОМАНД ---
        // Команда: "Запиши меня на танцы [на] {дата} в {время} [на {длительность}]"
        val regexLesson = Regex("""запиши\s+меня\s+на\s+(?<subject>[^\s]+)(?:\s+на)?\s+(?<date>[^в]+)\s+в\s+(?<time>\d{1,2}[:.]\d{2})(?:\s+на\s+(?<duration>\d+)\s*(?:минут|мин))?""")
        val matchLesson = regexLesson.find(lowerMessage)

        if (matchLesson != null) {
            val subject = matchLesson.groups["subject"]?.value ?: "занятие"
            val dateStr = matchLesson.groups["date"]?.value?.trim() ?: ""
            val timeStr = matchLesson.groups["time"]?.value?.trim() ?: ""
            val durationStr = matchLesson.groups["duration"]?.value?.trim()

            // Попробуем распарсить дату и время. Если не получится, перейдём в состояние ожидания уточнений.
            val calendar = parseDateAndTime(dateStr, timeStr)
            if (calendar != null) {
                // Дата и время распознаны, создаём занятие
                val durationMinutes = durationStr?.toIntOrNull() ?: 60
                val newLesson = com.example.dancemusicapp.local.Lesson( // Убедись, что импорт правильный
                    title = subject.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                    description = "Индивидуальное занятие по $subject",
                    timestamp = calendar.timeInMillis,
                    durationMinutes = durationMinutes
                )
                lessonRepository.insertLesson(newLesson)
                val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                // val formattedDate = dateFormat.format(Date(calendar.time)) // <-- НЕПРАВИЛЬНО: Date(Date)
                val formattedDate = dateFormat.format(calendar.time) // <-- ПРАВИЛЬНО: Date (java.util.Date)
                return ChatMessageItem.InfoMessage(text = "Вы записаны на занятие по ${newLesson.title} на $durationMinutes минут в $formattedDate!")
            } else {
                // Не удалось распознать дату/время, переходим в состояние ожидания уточнений
                val pendingData = PendingLessonData( // <-- Убедись, что PendingLessonData определён и импортирован
                    subject = subject,
                    dateStr = dateStr,
                    timeStr = timeStr,
                    durationMinutes = durationStr?.toIntOrNull() ?: 60
                )
                // Здесь ты можешь обновить uiState, чтобы показать диалог выбора даты/времени
                // или просто вернуть сообщение с просьбой уточнить.
                return ChatMessageItem.InfoMessage(text = "Уточните дату и время. Например: 'Завтра в 15:00'.")
            }
        }

        // Команда: "Включи музыку для танцев" или "музыку"
        if (lowerMessage.contains("музыку") || lowerMessage.contains("включи")) {
            // Убедись, что метод существует в PlayerRepository и правильно вызывается
            playerRepository?.playDefaultMusic() // <-- ИСПОЛЬЗУЙ ПРАВИЛЬНОЕ ИМЯ МЕТОДА!
            return ChatMessageItem.InfoMessage(text = "Включаю музыку для танцев!")
        }

        // Новая команда: "Покажи мои занятия"
        if (lowerMessage.contains("покажи") && lowerMessage.contains("занятия")) {
            // lessonController может предоставить список занятий
            // Предположим, у lessonController есть suspend-метод getAllLessons()
            // Это сложнее, так как collect нельзя вызвать напрямую в suspend-функции
            // Лучше вызывать через viewModelScope.launch в ChatFragment
            // Пока что просто возвращаем сообщение о том, что команда распознана
            // Для простоты, получим список синхронно (НЕ РЕКОМЕНДУЕТСЯ в корутине, но для примера)
            val lessonsList = try {
                lessonController.getAllLessonsSync() // <-- Теперь вызов работает
            } catch (e: Exception) {
                emptyList<com.example.dancemusicapp.local.Lesson>()
            }
            val response = if (lessonsList.isEmpty()) {
                "У вас пока нет запланированных занятий."
            } else {
                val sb = StringBuilder("Ваши запланированные занятия:\n")
                lessonsList.forEach { lesson ->
                    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                    // val formattedDate = dateFormat.format(Date(lesson.timestamp)) // <-- НЕПРАВИЛЬНО: Date(Long)
                    val formattedDate = dateFormat.format(Date(lesson.timestamp)) // <-- ПРАВИЛЬНО: Date(Long)
                    sb.append("- ${lesson.title}: $formattedDate (${lesson.durationMinutes} мин)\n")
                }
                sb.toString().trim()
            }
            return ChatMessageItem.InfoMessage(text = response)
        }

        // --- СТАРАЯ ЛОГИКА (резервная) ---
        var botResponse = "Простите, я не понимаю. Попробуйте: 'Запиши меня на танцы в субботу в 15:00' или 'Включи музыку для танцев'."

        if (lowerMessage.contains("танцы") && lowerMessage.contains("запиши")) {
            botResponse = "Вы записаны на занятие по танцам!" // Пример
        } else if (lowerMessage.contains("музыку") || lowerMessage.contains("включи")) {
            botResponse = "Включаю музыку для танцев!"
        }

        return ChatMessageItem.InfoMessage(text = botResponse)
    }

    // Вспомогательный метод для парсинга даты и времени
    private fun parseDateAndTime(dateStr: String, timeStr: String): Calendar? {
        val calendar = Calendar.getInstance()

        // Попробуем распарсить время
        val timeParts = timeStr.split("[:.]").mapNotNull { it.toIntOrNull() }
        if (timeParts.size != 2) return null
        val hour = timeParts[0]
        val minute = timeParts[1]

        // Очень простой парсинг даты (можно усложнить)
        when {
            dateStr.contains("сегодня") -> {
                // calendar.timeInMillis уже установлен на текущее время
            }
            dateStr.contains("завтра") -> {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }
            else -> {
                // Попробуем формат dd.mm.yyyy
                val dateParts = dateStr.split("[./]".toRegex()).mapNotNull { it.toIntOrNull() }
                if (dateParts.size < 2) return null // Нужен хотя бы день и месяц
                val day = dateParts[0]
                val month = dateParts[1] - 1 // Месяцы в Calendar с 0
                val year = dateParts.getOrNull(2) ?: calendar.get(Calendar.YEAR) // Используем текущий год, если не указан

                calendar.set(year, month, day)
            }
        }
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        // Проверим, не в прошлом ли время
        if (calendar.before(Calendar.getInstance())) {
            // Можно вернуть null или сообщение об ошибке
            // Для простоты вернём null
            return null
        }

        return calendar
    }
}
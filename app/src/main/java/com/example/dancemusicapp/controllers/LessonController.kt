// LessonController.kt
package com.example.dancemusicapp.controllers // <-- Новый пакет

import android.content.Context
import com.example.dancemusicapp.local.AppDatabase
import com.example.dancemusicapp.local.Lesson
import com.example.dancemusicapp.repository.LessonRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull // <-- Добавь этот import
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class LessonController(
    private val context: Context,
    private val lessonRepository: LessonRepository // Зависимость от Repository
) {
    // Метод для планирования занятия из чата
    suspend fun scheduleLessonFromChat(
        subject: String,
        dateStr: String,
        timeStr: String,
        durationMinutes: Int
    ): String {
        return try {
            // Попробуем распарсить дату и время из строк
            val calendar = parseDateAndTime(dateStr, timeStr)
            if (calendar != null) {
                // Дата и время распознаны, создаём занятие
                val newLesson = Lesson(
                    title = subject.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                    description = "Индивидуальное занятие по $subject",
                    timestamp = calendar.timeInMillis, // <-- Используем .timeInMillis для получения Long
                    durationMinutes = durationMinutes
                )
                lessonRepository.insertLesson(newLesson)
                val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                // val formattedDate = dateFormat.format(Date(calendar.time)) // <-- НЕПРАВИЛЬНО: Date(Date)
                val formattedDate = dateFormat.format(calendar.time) // <-- ПРАВИЛЬНО: Date (java.util.Date)
                "Вы записаны на занятие по ${newLesson.title} на $durationMinutes минут в $formattedDate!"
            } else {
                // Не удалось распознать дату/время, возвращаем сообщение с просьбой уточнить
                "Не удалось распознать дату и время: '$dateStr $timeStr'. Пожалуйста, уточните (например, 'завтра в 15:00')."
            }
        } catch (e: Exception) {
            "Произошла ошибка при записи на занятие: ${e.message}"
        }
    }

    // Метод для получения всех занятий (для команды "Покажи мои занятия")
    suspend fun getAllLessonsSync(): List<Lesson> {
        return try {
            // Выполняем в IO-контексте, так как это сетевая/дисковая операция
            withContext(Dispatchers.IO) {
                lessonRepository.getAllLessons().firstOrNull() ?: emptyList() // <-- firstOrNull теперь доступен
            }
        } catch (e: Exception) {
            emptyList() // В случае ошибки возвращаем пустой список
        }
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
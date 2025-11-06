// PendingLessonData.kt
package com.example.dancemusicapp

/**
 * Временное хранилище для данных нового занятия, пока пользователь уточняет детали.
 */
data class PendingLessonData(
    val subject: String = "",
    val dateStr: String = "",
    val timeStr: String = "",
    val durationMinutes: Int = 60
)


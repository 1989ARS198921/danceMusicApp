// Lesson.kt
package com.example.dancemusicapp.local // <-- Новое место (ранее мог быть в корне или в models)

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lessons") // Указываем имя таблицы
data class Lesson(
    @PrimaryKey(autoGenerate = true) // Автогенерируемый ID
    val id: Long = 0,
    val title: String,
    val description: String,
    val timestamp: Long, // Хранение даты как timestamp (Long)
    val durationMinutes: Int
)